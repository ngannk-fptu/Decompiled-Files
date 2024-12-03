/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.mobile;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

public class AnonymousUserSupport {
    public static final SpacePermission VIEW_USER_PROFILE_PERMISSION = new SpacePermission("VIEWUSERPROFILES");
    private final SpacePermissionManager spacePermissionManager;

    public AnonymousUserSupport(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public boolean isProfileViewPermitted() {
        User user = AuthenticatedUserThreadLocal.getUser();
        if (user != null) {
            return true;
        }
        return this.spacePermissionManager.permissionExists(VIEW_USER_PROFILE_PERMISSION);
    }
}

