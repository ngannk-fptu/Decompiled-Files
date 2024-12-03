/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.applinks.oauth.auth.twolo;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.oauth.auth.ServiceProviderUtil;
import com.atlassian.applinks.oauth.auth.twolo.TwoLeggedOAuthRequest;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class TwoLeggedOAuthRequestFactoryImpl
implements ApplicationLinkRequestFactory {
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ApplicationLink applicationLink;
    private final ConsumerService consumerService;
    private final RequestFactory requestFactory;

    public TwoLeggedOAuthRequestFactoryImpl(ApplicationLink applicationLink, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, RequestFactory requestFactory) {
        this.applicationLink = Objects.requireNonNull(applicationLink, "applicationLink can't be null");
        this.authenticationConfigurationManager = Objects.requireNonNull(authenticationConfigurationManager, "authenticationConfigurationManager can't be null");
        this.consumerService = Objects.requireNonNull(consumerService, "consumerService can't be null");
        this.requestFactory = Objects.requireNonNull(requestFactory, "requestFactory can't be null");
    }

    public ApplicationLinkRequest createRequest(Request.MethodType methodType, String uri) throws CredentialsRequiredException {
        Map config = this.authenticationConfigurationManager.getConfiguration(this.applicationLink.getId(), OAuthAuthenticationProvider.class);
        if (config == null) {
            throw new IllegalStateException(String.format("OAuth Authentication is not configured for application link %s", this.applicationLink));
        }
        ServiceProvider serviceProvider = ServiceProviderUtil.getServiceProvider(config, this.applicationLink);
        Request request = this.requestFactory.createRequest(methodType, uri);
        return new TwoLeggedOAuthRequest(uri, methodType, request, serviceProvider, this.consumerService, this.applicationLink.getId());
    }

    public URI getAuthorisationURI() {
        return null;
    }

    public URI getAuthorisationURI(URI callback) {
        return null;
    }
}

