/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.healthcheck.core.security;

import com.atlassian.healthcheck.core.security.AuthenticationException;
import com.atlassian.healthcheck.core.security.AuthorisationException;
import com.atlassian.sal.api.user.UserManager;

public class PermissionValidationService {
    private final UserManager userManager;

    public PermissionValidationService(UserManager userManager) {
        this.userManager = userManager;
    }

    public void validateIsAdmin() {
        this.validateIsAuthenticated();
        if (!this.userManager.isAdmin(this.userManager.getRemoteUserKey())) {
            throw new AuthorisationException("User is not administrator");
        }
    }

    public void validateIsAuthenticated() {
        if (this.userManager.getRemoteUserKey() == null) {
            throw new AuthenticationException("User is not authenticated");
        }
    }
}

