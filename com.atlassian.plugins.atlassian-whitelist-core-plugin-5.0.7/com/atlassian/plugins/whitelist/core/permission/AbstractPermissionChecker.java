/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.NotAuthorizedException
 */
package com.atlassian.plugins.whitelist.core.permission;

import com.atlassian.plugins.whitelist.NotAuthorizedException;
import com.atlassian.plugins.whitelist.core.permission.PermissionChecker;

public abstract class AbstractPermissionChecker
implements PermissionChecker {
    @Override
    public void checkCurrentUserCanManageWhitelist() {
        if (!this.canCurrentUserManageWhitelist()) {
            throw new NotAuthorizedException("The current user is not allowed to change the whitelist.");
        }
    }
}

