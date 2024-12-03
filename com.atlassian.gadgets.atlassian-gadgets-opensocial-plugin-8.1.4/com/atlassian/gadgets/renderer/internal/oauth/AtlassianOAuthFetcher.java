/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.Request$HttpMethod
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.bridge.Requests
 *  com.atlassian.oauth.bridge.ServiceProviders
 *  com.atlassian.oauth.bridge.consumer.ConsumerTokens
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthAccessor
 *  net.oauth.OAuthException
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthServiceProvider
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.shindig.common.uri.UriBuilder
 *  org.apache.shindig.gadgets.http.HttpFetcher
 *  org.apache.shindig.gadgets.http.HttpRequest
 *  org.apache.shindig.gadgets.http.HttpResponse
 *  org.apache.shindig.gadgets.http.HttpResponseBuilder
 *  org.apache.shindig.gadgets.oauth.OAuthFetcher
 *  org.apache.shindig.gadgets.oauth.OAuthFetcherConfig
 *  org.apache.shindig.gadgets.oauth.OAuthProtocolException
 *  org.apache.shindig.gadgets.oauth.OAuthResponseParams$OAuthRequestException
 */
package com.atlassian.gadgets.renderer.internal.oauth;

import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.oauth.Request;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.bridge.Requests;
import com.atlassian.oauth.bridge.ServiceProviders;
import com.atlassian.oauth.bridge.consumer.ConsumerTokens;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.shindig.common.uri.UriBuilder;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.HttpResponseBuilder;
import org.apache.shindig.gadgets.oauth.OAuthFetcher;
import org.apache.shindig.gadgets.oauth.OAuthFetcherConfig;
import org.apache.shindig.gadgets.oauth.OAuthProtocolException;
import org.apache.shindig.gadgets.oauth.OAuthResponseParams;

public class AtlassianOAuthFetcher
extends OAuthFetcher {
    private static final String XOAUTH_REQUESTOR_ID = "xoauth_requestor_id";
    private static final String OAUTH_TOKEN = "oauth_token";
    private final ConsumerService consumerService;
    private final ReadOnlyApplicationLinkService linkService;
    private HttpRequest realRequest;

    public AtlassianOAuthFetcher(@ComponentImport ConsumerService consumerService, @ComponentImport ReadOnlyApplicationLinkService linkService, OAuthFetcherConfig fetcherConfig, HttpFetcher nextFetcher, HttpRequest request) {
        super(fetcherConfig, nextFetcher);
        this.consumerService = consumerService;
        this.linkService = linkService;
        this.realRequest = request;
    }

    protected HttpResponseBuilder attemptFetch() throws OAuthResponseParams.OAuthRequestException, OAuthProtocolException {
        if (this.is2LOImpersonationAllowed()) {
            return this.attempt2LOImpersonationFetch();
        }
        return super.attemptFetch();
    }

    protected OAuthMessage sign(OAuthAccessor accessor, String httpMethod, String uri, List<OAuth.Parameter> params) throws OAuthException {
        Request request = new Request(Request.HttpMethod.valueOf((String)httpMethod.toUpperCase()), URI.create(uri), Requests.fromOAuthParameters(params));
        ServiceProvider serviceProvider = ServiceProviders.fromOAuthServiceProvider((OAuthServiceProvider)accessor.consumer.serviceProvider);
        if (accessor.requestToken != null || accessor.accessToken != null) {
            ConsumerToken token = ConsumerTokens.asConsumerToken((OAuthAccessor)accessor);
            return Requests.asOAuthMessage((Request)this.consumerService.sign(request, serviceProvider, token));
        }
        return Requests.asOAuthMessage((Request)this.consumerService.sign(request, accessor.consumer.consumerKey, serviceProvider));
    }

    protected HttpRequest createHttpRequest(HttpRequest base, List<Map.Entry<String, String>> oauthParams) throws OAuthResponseParams.OAuthRequestException {
        List filteredParameters = oauthParams.stream().filter(parameter -> !XOAUTH_REQUESTOR_ID.equals(parameter.getKey())).collect(Collectors.toList());
        return super.createHttpRequest(base, filteredParameters);
    }

    private boolean is2LOImpersonationAllowed() {
        if (this.realRequest.getSecurityToken() == null || StringUtils.isBlank((CharSequence)this.realRequest.getSecurityToken().getViewerId())) {
            return false;
        }
        ReadOnlyApplicationLink appLink = this.resolveApplicationLink(this.realRequest);
        if (appLink == null) {
            return false;
        }
        ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory(TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
        return requestFactory != null;
    }

    private HttpResponseBuilder attempt2LOImpersonationFetch() throws OAuthResponseParams.OAuthRequestException, OAuthProtocolException {
        String userId = this.realRequest.getSecurityToken().getViewerId();
        ArrayList<OAuth.Parameter> params = new ArrayList<OAuth.Parameter>();
        params.add(new OAuth.Parameter(OAUTH_TOKEN, ""));
        params.add(new OAuth.Parameter(XOAUTH_REQUESTOR_ID, userId));
        HttpRequest signed = this.sanitizeAndSign(this.realRequest, params, false);
        String url = this.appendRequestorId(signed.getUri().toString(), userId);
        signed.setUri(UriBuilder.parse((String)url).toUri());
        HttpResponse response = this.fetchFromServer(signed);
        this.checkForProtocolProblem(response);
        return new HttpResponseBuilder(response);
    }

    private ReadOnlyApplicationLink resolveApplicationLink(HttpRequest request) {
        String url = request.getUri().toString();
        for (ReadOnlyApplicationLink link : this.linkService.getApplicationLinks()) {
            if (!url.startsWith(link.getRpcUrl().toString()) && !url.startsWith(link.getDisplayUrl().toString())) continue;
            return link;
        }
        return null;
    }

    private String appendRequestorId(String url, String userId) {
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + XOAUTH_REQUESTOR_ID + "=" + Uri.encodeUriComponent((String)userId);
    }
}

