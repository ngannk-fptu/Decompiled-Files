/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.core.auth.ApplicationLinkRequestAdaptor
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.applinks.basic.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestAdaptor;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

public class BasicAuthRequestFactoryImpl
implements ApplicationLinkRequestFactory {
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ApplicationLink applicationLink;
    private final RequestFactory requestFactory;

    public BasicAuthRequestFactoryImpl(AuthenticationConfigurationManager authenticationConfigurationManager, ApplicationLink applicationLink, RequestFactory requestFactory) {
        this.authenticationConfigurationManager = Objects.requireNonNull(authenticationConfigurationManager, "authenticationConfigurationManager can't be null");
        this.applicationLink = Objects.requireNonNull(applicationLink, "applicationLink can't be null");
        this.requestFactory = Objects.requireNonNull(requestFactory, "requestFactory can't be null");
    }

    public ApplicationLinkRequest createRequest(Request.MethodType methodType, String url) {
        URI uri;
        Map config = this.authenticationConfigurationManager.getConfiguration(this.applicationLink.getId(), BasicAuthenticationProvider.class);
        if (config == null) {
            throw new IllegalStateException(String.format("Basic HTTP Authentication is not configured for application link %s", this.applicationLink));
        }
        try {
            uri = new URI(url);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("invalid url '" + url + "'", e);
        }
        return new ApplicationLinkRequestAdaptor(this.requestFactory.createRequest(methodType, url)).addBasicAuthentication(uri.getHost(), (String)config.get(USERNAME_KEY), (String)config.get(PASSWORD_KEY));
    }

    public URI getAuthorisationURI() {
        return null;
    }

    public URI getAuthorisationURI(URI callback) {
        return null;
    }
}

