/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.logout;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.filter.AbstractJohnsonAwareFilter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggedOutFromServiceManagementFilter
extends AbstractJohnsonAwareFilter {
    private static final Logger log = LoggerFactory.getLogger(LoggedOutFromServiceManagementFilter.class);
    private final SessionDataService sessionDataService;
    private final SsoConfigService ssoConfigService;
    private final IdpConfigService idpConfigService;

    @Inject
    public LoggedOutFromServiceManagementFilter(SessionDataService sessionDataService, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, JohnsonChecker johnsonChecker) {
        super(johnsonChecker);
        this.sessionDataService = sessionDataService;
        this.ssoConfigService = ssoConfigService;
        this.idpConfigService = idpConfigService;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        SsoConfig ssoConfig = this.ssoConfigService.getSsoConfig();
        List configs = this.idpConfigService.getIdpConfigs(IdpSearchParameters.allEnabled()).stream().filter(IdpConfig::isIncludeCustomerLogins).collect(Collectors.toList());
        if (!ssoConfig.getShowLoginFormForJsm() && configs.size() == 1) {
            log.trace("JSM login form is disabled and the amount of JSM IdPs is not equal to 1, setting logged out from JSM flag");
            this.sessionDataService.setLoggedOutFromJsmCustomerPortal((HttpServletRequest)request, true);
        }
    }
}

