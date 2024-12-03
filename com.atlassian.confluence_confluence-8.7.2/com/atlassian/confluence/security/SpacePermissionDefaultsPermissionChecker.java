/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;

public class SpacePermissionDefaultsPermissionChecker
implements SetSpacePermissionChecker {
    private final PermissionManager permissionManager;

    public SpacePermissionDefaultsPermissionChecker(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Deprecated(since="8.0", forRemoval=true)
    public SpacePermissionDefaultsPermissionChecker(PermissionManager permissionManager, UserAccessor userAccessor) {
        this(permissionManager);
    }

    @Override
    public boolean canSetPermission(User user, SpacePermission spacePermission) {
        return this.permissionManager.isConfluenceAdministrator(user);
    }
}

