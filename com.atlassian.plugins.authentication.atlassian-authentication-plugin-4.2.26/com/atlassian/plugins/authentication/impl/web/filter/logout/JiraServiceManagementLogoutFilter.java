/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web.filter.logout;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.filter.logout.LogoutFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JiraServiceManagementLogoutFilter
extends LogoutFilter {
    public static final String JSM_LOGOUT_QUERY_PARAM = "sd-logout";
    static final String XHR_HEADER_VALUE = "XMLHttpRequest";
    static final String XHR_HEADER_NAME = "X-Requested-With";
    private final SessionDataService sessionDataService;

    @Inject
    public JiraServiceManagementLogoutFilter(SessionDataService sessionDataService, @ComponentImport ApplicationProperties applicationProperties, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, JohnsonChecker johnsonChecker) {
        super(applicationProperties, ssoConfigService, idpConfigService, johnsonChecker);
        this.sessionDataService = sessionDataService;
    }

    @Override
    protected boolean shouldRequestBeRedirected(HttpServletRequest request, List<IdpConfig> enabledIdpConfigs) {
        List configs = enabledIdpConfigs.stream().filter(IdpConfig::isIncludeCustomerLogins).collect(Collectors.toList());
        SsoConfig ssoConfig = this.ssoConfigService.getSsoConfig();
        return !ssoConfig.getShowLoginFormForJsm() && configs.size() == 1 && (this.sessionDataService.wasLoggedOutFromJsmCustomerPortal(request) || Boolean.parseBoolean(request.getParameter("logout")));
    }

    @Override
    protected void redirectToSsoLogoutPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.wasRequestedByAjax(request)) {
            this.sessionDataService.setLoggedOutFromJsmCustomerPortal(request, false);
        }
        response.sendRedirect(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE) + "/plugins/servlet/sso-logout" + "?" + JSM_LOGOUT_QUERY_PARAM + "=" + true);
    }

    private boolean wasRequestedByAjax(HttpServletRequest request) {
        return XHR_HEADER_VALUE.equals(request.getHeader(XHR_HEADER_NAME));
    }

    @Override
    protected boolean isLoginFormSoleEnabledLoginOption(SsoConfig ssoConfig, List<IdpConfig> idpConfigs) {
        return ssoConfig.getShowLoginFormForJsm() && idpConfigs.stream().noneMatch(IdpConfig::isIncludeCustomerLogins);
    }
}

