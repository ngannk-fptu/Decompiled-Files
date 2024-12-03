/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.logout;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.filter.AbstractJohnsonAwareFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutFilter
extends AbstractJohnsonAwareFilter {
    private static final Logger log = LoggerFactory.getLogger(LogoutFilter.class);
    protected final ApplicationProperties applicationProperties;
    protected final IdpConfigService idpConfigService;
    protected final SsoConfigService ssoConfigService;

    public LogoutFilter(@ComponentImport ApplicationProperties applicationProperties, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, JohnsonChecker johnsonChecker) {
        super(johnsonChecker);
        this.applicationProperties = applicationProperties;
        this.ssoConfigService = ssoConfigService;
        this.idpConfigService = idpConfigService;
    }

    protected boolean isLoginFormSoleEnabledLoginOption(SsoConfig ssoConfig, List<IdpConfig> idpConfigs) {
        return ssoConfig.getShowLoginForm() && idpConfigs.isEmpty();
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        List<IdpConfig> enabledIdpConfigs = this.idpConfigService.getIdpConfigs(IdpSearchParameters.allEnabled());
        if (!this.isLoginFormSoleEnabledLoginOption(this.ssoConfigService.getSsoConfig(), enabledIdpConfigs) && this.shouldRequestBeRedirected((HttpServletRequest)request, enabledIdpConfigs)) {
            log.trace("Redirecting user to SSO logout page");
            this.redirectToSsoLogoutPage((HttpServletRequest)request, (HttpServletResponse)response);
        } else {
            log.trace("There are multiple login options or request should not be intercepted, continuing to product logout page.");
            chain.doFilter(request, response);
        }
    }

    protected boolean shouldRequestBeRedirected(HttpServletRequest request, List<IdpConfig> enabledIdpConfigs) {
        return true;
    }

    protected void redirectToSsoLogoutPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendRedirect(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE) + "/plugins/servlet/sso-logout");
    }
}

