/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.EntityRuntimeException;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.administrators.AbstractEditPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.UserChecker;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class EditGlobalPermissionsAdministrator
extends AbstractEditPermissionsAdministrator {
    private final UserChecker userChecker;

    protected EditGlobalPermissionsAdministrator(SpacePermissionManager spacePermissionManager, PermissionResolver permissionResolver, SetSpacePermissionChecker setSpacePermissionChecker, UserChecker userChecker, UserAccessor userAccessor) {
        super(spacePermissionManager, permissionResolver, setSpacePermissionChecker, userAccessor);
        this.userChecker = userChecker;
    }

    @Override
    public String getAdministrativePermissionType() {
        return "SYSTEMADMINISTRATOR";
    }

    @Override
    public void addPermission(SpacePermission permissionToAdd) {
        if (this.canAddPermission(permissionToAdd)) {
            this.getPermissions().add(permissionToAdd);
            this.spacePermissionManager.savePermission(permissionToAdd);
            this.userChecker.resetResult();
        }
    }

    @Override
    public void removePermission(SpacePermission permissionToRemove) {
        super.removePermission(permissionToRemove);
        this.userChecker.resetResult();
    }

    @Override
    public SpacePermission createUserGuardPermission(String guardPermission, ConfluenceUser user) {
        return SpacePermission.createUserSpacePermission(guardPermission, null, user);
    }

    @Override
    public SpacePermission createGroupGuardPermission(String guardPermission, String groupName) {
        return SpacePermission.createGroupSpacePermission(guardPermission, null, groupName);
    }

    @Override
    public Collection<SpacePermission> getInitialPermissionsFromForm(Map requestParams) {
        return this.buildPermissionsFromWebForm(null, requestParams, "initial");
    }

    @Override
    public Collection<SpacePermission> getRequestedPermissionsFromForm(Map requestParams) {
        return this.buildPermissionsFromWebForm(null, requestParams, "checkbox");
    }

    @Override
    public Collection<SpacePermission> buildPermissionsFromWebForm(Map formParameters, String parameterQualifier) {
        return this.buildPermissionsFromWebForm(null, formParameters, parameterQualifier);
    }

    public boolean canSetPermissionOnUser(String permission, ConfluenceUser user) throws EntityRuntimeException {
        if (!SpacePermission.GLOBAL_PERMISSIONS.contains(permission.toUpperCase(Locale.ENGLISH))) {
            throw new IllegalArgumentException("Unknown global permission: " + permission);
        }
        return this.setSpacePermissionChecker.canSetPermission(this.remoteUser, SpacePermission.createUserSpacePermission(permission, null, user));
    }

    public boolean canSetPermissionOnGroup(String permission, String groupName) throws EntityRuntimeException {
        if (!SpacePermission.GLOBAL_PERMISSIONS.contains(permission.toUpperCase(Locale.ENGLISH))) {
            throw new IllegalArgumentException("Unknown global permission: " + permission);
        }
        return this.setSpacePermissionChecker.canSetPermission(this.remoteUser, SpacePermission.createGroupSpacePermission(permission, null, groupName));
    }

    public boolean canSetSystemAdministratorPermission() {
        return this.spacePermissionManager.hasPermission("SYSTEMADMINISTRATOR", null, this.remoteUser);
    }

    @Override
    public void applyPermissionChanges(Collection<SpacePermission> oldPermissions, Collection<SpacePermission> newPermissions) throws IllegalArgumentException {
        super.applyPermissionChanges(oldPermissions, newPermissions);
        this.userChecker.resetResult();
    }
}

