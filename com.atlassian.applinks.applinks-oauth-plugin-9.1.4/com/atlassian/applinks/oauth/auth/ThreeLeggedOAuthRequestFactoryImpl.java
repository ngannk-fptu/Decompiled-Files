/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.AuthorisationAdminURIGenerator
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.rest.context.CurrentContext
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.AuthorisationAdminURIGenerator;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.oauth.auth.ServiceProviderUtil;
import com.atlassian.applinks.oauth.auth.ThreeLeggedOAuthRequest;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Validate;

public class ThreeLeggedOAuthRequestFactoryImpl
implements ApplicationLinkRequestFactory,
AuthorisationAdminURIGenerator {
    private static final String OAUTH_ACCESS_TOKENS_ADMIN_SERVLET_LOCATION = "/plugins/servlet/oauth/users/access-tokens";
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ApplicationLink applicationLink;
    private final ConsumerService consumerService;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final RequestFactory requestFactory;
    private final UserManager userManager;
    private final HostApplication hostApplication;

    public ThreeLeggedOAuthRequestFactoryImpl(ApplicationLink applicationLink, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, ConsumerTokenStoreService consumerTokenStoreService, RequestFactory requestFactory, UserManager userManager, HostApplication hostApplication) {
        this.applicationLink = Objects.requireNonNull(applicationLink, "applicationLink can't be null");
        this.authenticationConfigurationManager = Objects.requireNonNull(authenticationConfigurationManager, "authenticationConfigurationManager can't be null");
        this.consumerService = Objects.requireNonNull(consumerService, "consumerService can't be null");
        this.consumerTokenStoreService = Objects.requireNonNull(consumerTokenStoreService, "consumerTokenStoreService can't be null");
        this.requestFactory = Objects.requireNonNull(requestFactory, "requestFactory can't be null");
        this.userManager = Objects.requireNonNull(userManager, "userManager can't be null");
        this.hostApplication = Objects.requireNonNull(hostApplication, "hostApplication can't be null");
    }

    public ApplicationLinkRequest createRequest(Request.MethodType methodType, String uri) throws CredentialsRequiredException {
        Map config = this.authenticationConfigurationManager.getConfiguration(this.applicationLink.getId(), OAuthAuthenticationProvider.class);
        Validate.isTrue((config != null ? 1 : 0) != 0, (String)String.format("OAuth Authentication is not configured for application link %s", this.applicationLink), (Object[])new Object[0]);
        ServiceProvider serviceProvider = ServiceProviderUtil.getServiceProvider(config, this.applicationLink);
        Request request = this.requestFactory.createRequest(methodType, uri);
        String username = Objects.requireNonNull(this.userManager.getRemoteUsername(), "You have to be logged in to use oauth authentication.");
        return new ThreeLeggedOAuthRequest(uri, methodType, request, serviceProvider, this.consumerService, this.retrieveConsumerToken(username), this.consumerTokenStoreService, this.applicationLink.getId(), username);
    }

    private ConsumerToken retrieveConsumerToken(String username) throws CredentialsRequiredException {
        ConsumerToken consumerToken = this.consumerTokenStoreService.getConsumerToken(this.applicationLink, username);
        if (consumerToken == null || consumerToken.isRequestToken()) {
            throw new CredentialsRequiredException((AuthorisationURIGenerator)this, "You do not have an authorized access token for the remote resource.");
        }
        return consumerToken;
    }

    @HtmlSafe
    public URI getAuthorisationURI() {
        HttpServletRequest request = CurrentContext.getHttpServletRequest();
        URI baseUrl = request != null ? RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) : this.hostApplication.getBaseUrl();
        return URIUtil.uncheckedConcatenate((URI)baseUrl, (String[])new String[]{"/plugins/servlet/applinks/oauth/login-dance/authorize?applicationLinkID=" + URIUtil.utf8Encode((String)this.applicationLink.getId().get())});
    }

    @HtmlSafe
    public URI getAuthorisationURI(URI callback) {
        return URIUtil.uncheckedToUri((String)(this.getAuthorisationURI().toString() + "&redirectUrl=" + URIUtil.utf8Encode((URI)Objects.requireNonNull(callback))));
    }

    @HtmlSafe
    public URI getAuthorisationAdminURI() {
        return URIUtil.uncheckedConcatenate((URI)this.applicationLink.getDisplayUrl(), (String[])new String[]{OAUTH_ACCESS_TOKENS_ADMIN_SERVLET_LOCATION});
    }
}

