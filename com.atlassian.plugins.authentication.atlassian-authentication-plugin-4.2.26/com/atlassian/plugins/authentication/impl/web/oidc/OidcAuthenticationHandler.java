/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.util.TargetUrlNormalizer;
import com.atlassian.plugins.authentication.impl.web.AbstractAuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationRequest;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcAuthenticationRequestFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

@Named
public class OidcAuthenticationHandler
extends AbstractAuthenticationHandler<OidcConfig> {
    private final OidcAuthenticationRequestFactory oidcAuthenticationRequestFactory;
    private static final String LOGIN_HINT_ATT_NAME = OidcAuthenticationHandler.class.getSimpleName() + ".login_hint";

    @Inject
    public OidcAuthenticationHandler(@ComponentImport ApplicationProperties applicationProperties, ApplicationStateValidator applicationStateValidator, SessionDataService sessionDataService, OidcAuthenticationRequestFactory oidcAuthenticationRequestFactory, TargetUrlNormalizer targetUrlNormalizer, WebResourceUrlProvider webResourceUrlProvider, SoyTemplateRenderer soyTemplateRenderer) {
        super(applicationProperties, applicationStateValidator, sessionDataService, targetUrlNormalizer, webResourceUrlProvider, soyTemplateRenderer);
        this.oidcAuthenticationRequestFactory = oidcAuthenticationRequestFactory;
    }

    @Override
    @Nonnull
    public String getConsumerServletUrl() {
        return StringUtils.removeEnd((String)this.getIssuerUrl(), (String)"/") + "/plugins/servlet/oidc/callback";
    }

    @Override
    protected AuthenticationRequest prepareAuthenticationRequest(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, OidcConfig oidcConfig) {
        return this.oidcAuthenticationRequestFactory.prepareOidcAuthenticationRequest(this.getConsumerServletUrl(), OidcAuthenticationHandler.getLoginHint(request), this.isPermissionViolation(request), oidcConfig);
    }

    public static void setLoginHint(@Nonnull HttpServletRequest request, String loginHint) {
        request.setAttribute(LOGIN_HINT_ATT_NAME, (Object)loginHint);
    }

    public static String getLoginHint(@Nonnull HttpServletRequest request) {
        return (String)request.getAttribute(LOGIN_HINT_ATT_NAME);
    }
}

