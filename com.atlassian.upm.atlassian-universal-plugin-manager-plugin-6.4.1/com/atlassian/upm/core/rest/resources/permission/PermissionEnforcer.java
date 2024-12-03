/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.upm.core.rest.resources.permission;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.permission.UserAttributes;
import com.atlassian.upm.core.rest.resources.permission.PermissionException;
import java.net.URI;
import java.util.Objects;

public class PermissionEnforcer {
    private final UserManager userManager;
    private final PermissionService permissionService;

    public PermissionEnforcer(UserManager userManager, PermissionService permissionService) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.permissionService = Objects.requireNonNull(permissionService, "permissionService");
    }

    public void enforcePermission(Permission permission) {
        for (PermissionService.PermissionError error : this.permissionService.getPermissionError(this.getUserAttributes(), permission)) {
            this.handleError(error);
        }
    }

    public void enforcePermission(Permission permission, Plugin plugin) {
        for (PermissionService.PermissionError error : this.permissionService.getPermissionError(this.getUserAttributes(), permission, plugin)) {
            this.handleError(error);
        }
    }

    public void enforcePermission(Permission permission, Plugin.Module module) {
        for (PermissionService.PermissionError error : this.permissionService.getPermissionError(this.getUserAttributes(), permission, module)) {
            this.handleError(error);
        }
    }

    public boolean hasPermission(Permission permission) {
        return !this.permissionService.getPermissionError(this.getUserAttributes(), permission).isDefined();
    }

    public boolean hasPermission(Permission permission, Plugin plugin) {
        return !this.permissionService.getPermissionError(this.getUserAttributes(), permission, plugin).isDefined();
    }

    public boolean hasPermission(Permission permission, Plugin.Module module) {
        return !this.permissionService.getPermissionError(this.getUserAttributes(), permission, module).isDefined();
    }

    public boolean hasVendorFeedbackPermission(Plugin plugin) {
        return (this.hasPermission(Permission.MANAGE_PLUGIN_UNINSTALL, plugin) || this.hasPermission(Permission.MANAGE_PLUGIN_ENABLEMENT, plugin)) && this.hasPermission(Permission.ADD_ANALYTICS_ACTIVITY);
    }

    public boolean hasInProcessInstallationFromUriPermission(URI uri) {
        return this.hasInProcessInstallationFromUriPermission(this.userManager.getRemoteUserKey(), uri);
    }

    public boolean hasInProcessInstallationFromUriPermission(UserKey userKey, URI uri) {
        return !this.permissionService.getInProcessInstallationFromUriPermissionError(UserAttributes.fromUserKey(userKey, this.userManager), uri).isDefined();
    }

    public void enforceInProcessInstallationFromUriPermission(URI uri) {
        for (PermissionService.PermissionError error : this.permissionService.getInProcessInstallationFromUriPermissionError(this.getUserAttributes(), uri)) {
            this.handleError(error);
        }
    }

    public void handleError(PermissionService.PermissionError error) {
        switch (error) {
            case UNAUTHORIZED: {
                throw PermissionException.unauthorized();
            }
            case FORBIDDEN: {
                throw PermissionException.forbidden();
            }
            case CONFLICT: {
                throw PermissionException.conflict();
            }
        }
    }

    public boolean isAdmin() {
        return this.isLoggedIn() && (this.isSystemAdmin() || this.userManager.isAdmin(this.userManager.getRemoteUserKey()));
    }

    public void enforceAdmin() {
        if (!this.isAdmin()) {
            throw PermissionException.forbidden();
        }
    }

    public boolean isSystemAdmin() {
        return this.isLoggedIn() && this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey());
    }

    public void enforceSystemAdmin() {
        if (!this.isSystemAdmin()) {
            throw PermissionException.forbidden();
        }
    }

    public boolean isLoggedIn() {
        return this.userManager.getRemoteUserKey() != null;
    }

    private UserAttributes getUserAttributes() {
        return UserAttributes.fromCurrentUser(this.userManager);
    }
}

