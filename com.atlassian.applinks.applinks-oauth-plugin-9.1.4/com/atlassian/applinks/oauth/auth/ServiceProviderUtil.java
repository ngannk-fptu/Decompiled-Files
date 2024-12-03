/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.oauth.ServiceProvider
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.oauth.auth.servlets.consumer.AddServiceProviderManuallyServlet;
import com.atlassian.oauth.ServiceProvider;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class ServiceProviderUtil {
    public static ServiceProvider getServiceProvider(URI rpcUrl, URI displayUrl) {
        URI requestTokenUri = URIUtil.uncheckedConcatenate((URI)rpcUrl, (String[])new String[]{"/plugins/servlet/oauth/request-token"});
        URI authorizeTokenUri = URIUtil.uncheckedConcatenate((URI)displayUrl, (String[])new String[]{"/plugins/servlet/oauth/authorize"});
        URI accessTokenUri = URIUtil.uncheckedConcatenate((URI)rpcUrl, (String[])new String[]{"/plugins/servlet/oauth/access-token"});
        return new ServiceProvider(requestTokenUri, authorizeTokenUri, accessTokenUri);
    }

    public static ServiceProvider getServiceProvider(Map<String, String> config, ApplicationLink applicationLink) {
        if (config.containsKey(AddServiceProviderManuallyServlet.CONSUMER_KEY_OUTBOUND)) {
            String accessTokenUrl = ServiceProviderUtil.makeAbsoluteUrl(config.get(AddServiceProviderManuallyServlet.SERVICE_PROVIDER_ACCESS_TOKEN_URL), applicationLink.getRpcUrl());
            String requestTokenUrl = ServiceProviderUtil.makeAbsoluteUrl(config.get(AddServiceProviderManuallyServlet.SERVICE_PROVIDER_REQUEST_TOKEN_URL), applicationLink.getRpcUrl());
            String authorizeUrl = ServiceProviderUtil.makeAbsoluteUrl(config.get(AddServiceProviderManuallyServlet.SERVICE_PROVIDER_AUTHORIZE_URL), applicationLink.getDisplayUrl());
            return new ServiceProvider(URI.create(requestTokenUrl), URI.create(authorizeUrl), URI.create(accessTokenUrl));
        }
        return ServiceProviderUtil.getServiceProvider(applicationLink.getRpcUrl(), applicationLink.getDisplayUrl());
    }

    protected static String makeAbsoluteUrl(String uri, URI baseUri) {
        Objects.requireNonNull(uri, "uri can't be null");
        Objects.requireNonNull(baseUri, "baseUri can't be null");
        if (uri.startsWith("/")) {
            return baseUri.resolve(uri).toASCIIString();
        }
        return uri;
    }
}

