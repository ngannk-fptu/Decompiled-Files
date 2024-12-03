/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.sal.api.permission;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.NotAuthenticatedException;

@PublicApi
public interface PermissionEnforcer {
    public void enforceAdmin() throws AuthorisationException;

    public void enforceAuthenticated() throws NotAuthenticatedException;

    public void enforceSiteAccess() throws AuthorisationException;

    public void enforceSystemAdmin() throws AuthorisationException;

    public boolean isAdmin();

    public boolean isAuthenticated();

    public boolean isLicensedOrLimitedUnlicensedUser();

    public boolean isLicensed();

    public boolean isSystemAdmin();
}

