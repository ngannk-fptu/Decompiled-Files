/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.security;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.troubleshooting.stp.security.AuthenticationException;
import com.atlassian.troubleshooting.stp.security.AuthorisationException;
import org.springframework.beans.factory.annotation.Autowired;

public class PermissionValidationService {
    private final I18nResolver i18nResolver;
    private final UserManager userManager;

    @Autowired
    public PermissionValidationService(I18nResolver i18nResolver, UserManager userManager) {
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
    }

    public void validateIsAdmin() {
        this.validateIsAuthenticated();
        if (!this.userManager.isAdmin(this.userManager.getRemoteUserKey())) {
            throw new AuthorisationException(this.i18nResolver.getText("stp.security.admin.required"));
        }
    }

    public void validateIsAuthenticated() {
        if (this.userManager.getRemoteUserKey() == null) {
            throw new AuthenticationException(this.i18nResolver.getText("stp.security.not.authenticated"));
        }
    }

    public void validateIsSysadmin() {
        this.validateIsAuthenticated();
        if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey())) {
            throw new AuthorisationException(this.i18nResolver.getText("stp.security.sysadmin.required"));
        }
    }
}

