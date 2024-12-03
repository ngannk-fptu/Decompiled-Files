/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.sal.api.net.Request$MethodType
 */
package com.atlassian.confluence.extra.jira.util;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.sal.api.net.Request;

public class JiraConnectorUtils {
    private JiraConnectorUtils() {
    }

    public static ApplicationLinkRequest getApplicationLinkRequest(ReadOnlyApplicationLink applicationLink, Request.MethodType methodType, String url) throws CredentialsRequiredException {
        ApplicationLinkRequest applicationLinkRequest;
        try {
            ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory();
            applicationLinkRequest = requestFactory.createRequest(methodType, url);
        }
        catch (CredentialsRequiredException e) {
            ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory(Anonymous.class);
            applicationLinkRequest = requestFactory.createRequest(methodType, url);
        }
        return applicationLinkRequest;
    }

    public static Object[] getApplicationLinkRequestWithOauUrl(ReadOnlyApplicationLink applicationLink, Request.MethodType methodType, String url) throws CredentialsRequiredException {
        ApplicationLinkRequest applicationLinkRequest;
        String oauUrl = null;
        try {
            ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory();
            applicationLinkRequest = requestFactory.createRequest(methodType, url);
        }
        catch (CredentialsRequiredException e) {
            oauUrl = e.getAuthorisationURI().toString();
            ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory(Anonymous.class);
            applicationLinkRequest = requestFactory.createRequest(methodType, url);
        }
        return new Object[]{applicationLinkRequest, oauUrl};
    }

    public static String getAuthUrl(AuthenticationConfigurationManager authenticationConfigurationManager, ReadOnlyApplicationLink applicationLink) {
        if (authenticationConfigurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class)) {
            try {
                applicationLink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.GET, "");
            }
            catch (CredentialsRequiredException e) {
                return e.getAuthorisationURI().toString();
            }
        }
        return null;
    }

    public static ReadOnlyApplicationLink getApplicationLink(ReadOnlyApplicationLinkService applicationLinkService, String appId) throws TypeNotInstalledException {
        ReadOnlyApplicationLink applicationLink = applicationLinkService.getApplicationLink(new ApplicationId(appId));
        if (applicationLink == null) {
            throw new TypeNotInstalledException("Can not get Application Link", null, null);
        }
        return applicationLink;
    }
}

