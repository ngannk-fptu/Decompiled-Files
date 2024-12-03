/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.service;

import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import com.atlassian.confluence.security.service.IllegalPermissionStateException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.List;

public class DefaultAnonymousUserPermissionsService
implements AnonymousUserPermissionsService {
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;

    public DefaultAnonymousUserPermissionsService(PermissionManager permissionManager, SpacePermissionManager spacePermissionManager) {
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public void setUsePermission(boolean enabled) {
        this.checkCanAdministerApplication();
        if (enabled) {
            this.addPermission("USECONFLUENCE");
        } else {
            this.removePermission("VIEWUSERPROFILES");
            this.removePermission("USECONFLUENCE");
        }
    }

    @Override
    public void setViewUserProfilesPermission(boolean enabled) {
        this.checkCanAdministerApplication();
        if (enabled) {
            SpacePermission newPermission = new SpacePermission("USECONFLUENCE");
            if (!this.hasPermission(newPermission)) {
                throw new IllegalPermissionStateException("You must have USECONFLUENCE set before you can set VIEWUSERPROFILES");
            }
            this.addPermission("VIEWUSERPROFILES");
        } else {
            this.removePermission("VIEWUSERPROFILES");
        }
    }

    private void addPermission(String permission) {
        SpacePermission newPermission = new SpacePermission(permission);
        if (!this.hasPermission(newPermission)) {
            this.spacePermissionManager.savePermission(newPermission);
        }
    }

    private void removePermission(String permission) {
        List<SpacePermission> globalPermissions = this.spacePermissionManager.getGlobalPermissions();
        SpacePermission permissionToBeRemoved = new SpacePermission(permission);
        for (SpacePermission globalPermission : globalPermissions) {
            if (!permissionToBeRemoved.equals(globalPermission)) continue;
            this.spacePermissionManager.removePermission(globalPermission);
        }
    }

    private boolean hasPermission(SpacePermission permission) {
        List<SpacePermission> globalPermissions = this.spacePermissionManager.getGlobalPermissions();
        return globalPermissions.contains(permission);
    }

    private void checkCanAdministerApplication() {
        User currentUser = this.getUser();
        if (!this.permissionManager.isConfluenceAdministrator(currentUser)) {
            throw new NotAuthorizedException(currentUser, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
        }
    }

    private User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

