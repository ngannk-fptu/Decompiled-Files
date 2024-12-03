/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.NotAuthenticatedException
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.sal.core.permission;

import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.NotAuthenticatedException;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

public class DefaultPermissionEnforcer
implements PermissionEnforcer {
    private final UserManager userManager;

    public DefaultPermissionEnforcer(UserManager userManager) {
        this.userManager = userManager;
    }

    public void enforceAdmin() {
        if (!this.userManager.isAdmin(this.getRemoteUserOrThrow())) {
            this.throwNotAuthorised("You must be an administrator to access this resource");
        }
    }

    public void enforceAuthenticated() {
        this.getRemoteUserOrThrow();
    }

    public void enforceSiteAccess() {
        UserKey key = this.userManager.getRemoteUserKey();
        if (key == null) {
            if (!this.userManager.isAnonymousAccessEnabled()) {
                this.throwNotAuthenticated("You must be authenticated to access this resource");
            }
        } else if (!this.userManager.isLicensed(key) && !this.userManager.isLimitedUnlicensedUser(key)) {
            this.throwNotAuthorised("You must have at least limited site access to access this resource");
        }
    }

    public void enforceSystemAdmin() {
        if (!this.userManager.isSystemAdmin(this.getRemoteUserOrThrow())) {
            this.throwNotAuthorised("You must be a system administrator to access this resource");
        }
    }

    public boolean isAdmin() {
        UserKey key = this.userManager.getRemoteUserKey();
        return key != null && this.userManager.isAdmin(key);
    }

    public boolean isAuthenticated() {
        return this.userManager.getRemoteUserKey() != null;
    }

    public boolean isLicensedOrLimitedUnlicensedUser() {
        UserKey key = this.userManager.getRemoteUserKey();
        return key != null && (this.userManager.isLicensed(key) || this.userManager.isLimitedUnlicensedUser(key));
    }

    public boolean isLicensed() {
        UserKey key = this.userManager.getRemoteUserKey();
        return key != null && this.userManager.isLicensed(key);
    }

    public boolean isSystemAdmin() {
        UserKey key = this.userManager.getRemoteUserKey();
        return key != null && this.userManager.isSystemAdmin(key);
    }

    protected void throwNotAuthorised(String message) {
        if (message != null) {
            throw new AuthorisationException(message);
        }
        throw new AuthorisationException();
    }

    protected void throwNotAuthenticated(String message) {
        if (message != null) {
            throw new NotAuthenticatedException(message);
        }
        throw new NotAuthenticatedException();
    }

    protected UserKey getRemoteUserOrThrow() {
        UserKey key = this.userManager.getRemoteUserKey();
        if (key == null) {
            this.throwNotAuthenticated(null);
        }
        return key;
    }
}

