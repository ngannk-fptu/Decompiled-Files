/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.util.TargetUrlNormalizer;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationRequest;
import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAuthenticationHandler<T extends IdpConfig>
implements AuthenticationHandler<T> {
    private static final int COOKIE_WITH_FRAGMENT_MAXIMUM_AGE_MINUTES = 5;
    private static final Logger log = LoggerFactory.getLogger(AbstractAuthenticationHandler.class);
    protected final ApplicationProperties applicationProperties;
    protected final ApplicationStateValidator applicationStateValidator;
    protected final SessionDataService sessionDataService;
    protected final TargetUrlNormalizer targetUrlNormalizer;
    protected final WebResourceUrlProvider webResourceUrlProvider;
    protected final SoyTemplateRenderer soyTemplateRenderer;

    protected AbstractAuthenticationHandler(ApplicationProperties applicationProperties, ApplicationStateValidator applicationStateValidator, SessionDataService sessionDataService, TargetUrlNormalizer targetUrlNormalizer, WebResourceUrlProvider webResourceUrlProvider, SoyTemplateRenderer soyTemplateRenderer) {
        this.applicationProperties = applicationProperties;
        this.applicationStateValidator = applicationStateValidator;
        this.sessionDataService = sessionDataService;
        this.targetUrlNormalizer = targetUrlNormalizer;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    @Override
    @Nonnull
    public String getIssuerUrl() {
        return this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.CANONICAL);
    }

    @Override
    public void processAuthenticationRequest(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nullable String destinationUrl, T idpConfig) throws IOException {
        this.processAuthenticationRequestForRelativeDestinationUrl(request, response, this.targetUrlNormalizer.getRelativeTargetUrl(destinationUrl), idpConfig);
    }

    @VisibleForTesting
    public void processAuthenticationRequestForRelativeDestinationUrl(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nullable URI relativeDestinationUri, T idpConfig) throws IOException {
        this.applicationStateValidator.checkCanProcessAuthenticationRequest((IdpConfig)idpConfig);
        this.sessionDataService.ensureSessionExists(request);
        AuthenticationRequest authenticationRequest = this.prepareAuthenticationRequest(request, response, idpConfig);
        this.sessionDataService.setSessionData(request, response, authenticationRequest.getSessionDataKey(), new SessionData(authenticationRequest, relativeDestinationUri, idpConfig.getId()));
        if (relativeDestinationUri == null) {
            log.trace("No destination uri present, redirecting user to the login request url.");
            response.sendRedirect(authenticationRequest.getLoginRequestUrl());
        } else {
            log.trace("Destination uri present, saving URL fragment and proceeding with SSO.");
            this.renderFragmentSavingPage(response, authenticationRequest.getLoginRequestUrl(), authenticationRequest.getPublicId());
        }
    }

    @Override
    public boolean isCorrectlyConfigured(IdpConfig idpConfig) {
        return this.applicationStateValidator.canProcessAuthenticationRequest(idpConfig);
    }

    protected abstract AuthenticationRequest prepareAuthenticationRequest(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2, T var3);

    protected boolean isPermissionViolation(HttpServletRequest request) {
        if (this.sessionDataService.isUserLoggedInWithSso(request)) {
            log.info("User is already logged in. Looks like permissions violation. Force re-authentication in IDP");
            return true;
        }
        return false;
    }

    private void renderFragmentSavingPage(@Nonnull HttpServletResponse response, @Nonnull String loginRequestUrl, @Nonnull String cookieSuffix) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        ImmutableMap soyTemplateParameters = ImmutableMap.builder().put((Object)"idpRequest", (Object)loginRequestUrl).put((Object)"cookieName", (Object)("atlassian-authentication-plugin-url-fragment_" + cookieSuffix)).put((Object)"cookiePath", (Object)this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.RELATIVE)).put((Object)"cookieExpirationTimeInMinutesFromNow", (Object)5).put((Object)"jsCookieLibraryUrl", (Object)this.getJsCookieLibraryUrl()).build();
        this.soyTemplateRenderer.render((Appendable)response.getWriter(), "com.atlassian.plugins.authentication.atlassian-authentication-plugin:save-fragment", "AuthenticationPlugin.SaveHash.display", (Map)soyTemplateParameters);
    }

    private String getJsCookieLibraryUrl() {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl("com.atlassian.plugins.authentication.atlassian-authentication-plugin:save-fragment", "js.cookie.js", UrlMode.RELATIVE);
    }
}

