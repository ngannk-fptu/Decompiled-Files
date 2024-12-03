/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.core.permission;

public interface PermissionChecker {
    public boolean canCurrentUserManageWhitelist();

    public void checkCurrentUserCanManageWhitelist();
}

