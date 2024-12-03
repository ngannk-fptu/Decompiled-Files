/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserRole
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.ui.auth;

import com.atlassian.applinks.ui.auth.AdminFilter;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserRole;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysAdminFilter
extends AdminFilter {
    private I18nResolver i18nResolver;

    public SysAdminFilter(AdminUIAuthenticator uiAuthenticator, I18nResolver i18nResolver, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties) {
        super(uiAuthenticator, loginUriProvider, applicationProperties);
        this.i18nResolver = i18nResolver;
    }

    @Override
    UserRole getForRole() {
        return UserRole.SYSADMIN;
    }

    @Override
    boolean checkAccess(String username, String password, AdminUIAuthenticator.SessionHandler sessionHandler) {
        return this.uiAuthenticator.checkSysadminUIAccessBySessionOrPasswordAndActivateSysadminSession(username, password, sessionHandler);
    }

    @Override
    protected void handleAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("no-check".equals(request.getHeader("X-Atlassian-Token"))) {
            response.sendError(403, this.i18nResolver.getText("applinks.error.only.sysadmin.operation"));
        } else {
            super.handleAccessDenied(request, response);
        }
    }
}

