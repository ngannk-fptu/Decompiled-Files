/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.filter.logout;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.filter.logout.LogoutFilter;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceLogoutFilter
extends LogoutFilter {
    static final String LOGOUT_QUERY_PARAMETER = "logout";

    @Inject
    public ConfluenceLogoutFilter(@ComponentImport ApplicationProperties applicationProperties, SsoConfigService ssoConfigService, IdpConfigService idpConfigService, JohnsonChecker johnsonChecker) {
        super(applicationProperties, ssoConfigService, idpConfigService, johnsonChecker);
    }

    @Override
    protected boolean shouldRequestBeRedirected(HttpServletRequest request, List<IdpConfig> enabledIdpConfigs) {
        return Boolean.parseBoolean(request.getParameter(LOGOUT_QUERY_PARAMETER));
    }
}

