/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.inject.Inject
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.acegisecurity.context.SecurityContextHolder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.impl.web.filter.logout;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.filter.logout.LogoutFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.lang3.StringUtils;

public class BambooLogoutFilter
extends LogoutFilter {
    static final String LOGOUT_QUERY_PARAMETER = "logout";

    @Inject
    public BambooLogoutFilter(@ComponentImport ApplicationProperties applicationProperties, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, JohnsonChecker johnsonChecker) {
        super(applicationProperties, ssoConfigService, idpConfigService, johnsonChecker);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    protected void redirectToSsoLogoutPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityConfig securityConfig = SecurityConfigFactory.getInstance();
        Authenticator authenticator = securityConfig.getAuthenticator();
        try {
            if (authenticator.getUser(request, response) != null) {
                authenticator.logout(request, response);
            }
        }
        catch (AuthenticatorException e) {
            throw new ServletException((Throwable)e);
        }
        SecurityContextHolder.clearContext();
        response.sendRedirect(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE) + "/plugins/servlet/sso-logout");
    }

    @Override
    public void destroy() {
    }

    @Override
    protected boolean shouldRequestBeRedirected(HttpServletRequest request, List<IdpConfig> enabledIdpConfigs) {
        return StringUtils.isBlank((CharSequence)request.getParameter(LOGOUT_QUERY_PARAMETER));
    }
}

