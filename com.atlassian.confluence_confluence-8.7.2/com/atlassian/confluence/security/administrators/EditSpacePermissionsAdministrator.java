/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.administrators.AbstractEditPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Deprecated
public class EditSpacePermissionsAdministrator
extends AbstractEditPermissionsAdministrator {
    private Space space;

    protected EditSpacePermissionsAdministrator(SpacePermissionManager spacePermissionManager, PermissionResolver permissionResolver, SetSpacePermissionChecker setSpacePermissionChecker, UserAccessor userAccessor) {
        super(spacePermissionManager, permissionResolver, setSpacePermissionChecker, userAccessor);
    }

    @Override
    public String getAdministrativePermissionType() {
        return "SETSPACEPERMISSIONS";
    }

    @Override
    public void removeAllPermissions(Set<SpacePermission> permissionsToRemove) {
        for (SpacePermission spacePermission : permissionsToRemove) {
            if (this.isPersonalSpaceOwner(spacePermission)) continue;
            this.removePermission(spacePermission);
        }
    }

    @Override
    public void addPermission(SpacePermission permissionToAdd) {
        if (this.canAddPermission(permissionToAdd)) {
            this.space.addPermission(permissionToAdd);
            this.spacePermissionManager.savePermission(permissionToAdd);
        }
    }

    @Override
    public SpacePermission createUserGuardPermission(String guardPermission, ConfluenceUser user) {
        return SpacePermission.createUserSpacePermission(guardPermission, this.space, user);
    }

    @Override
    public SpacePermission createGroupGuardPermission(String guardPermission, String groupName) {
        return SpacePermission.createGroupSpacePermission(guardPermission, this.space, groupName);
    }

    @Override
    public Collection<SpacePermission> getInitialPermissionsFromForm(Map requestParams) {
        return this.buildPermissionsFromWebForm(this.space, requestParams, "initial");
    }

    @Override
    public Collection<SpacePermission> getRequestedPermissionsFromForm(Map requestParams) {
        return this.buildPermissionsFromWebForm(this.space, requestParams, "checkbox");
    }

    @Override
    public Collection<SpacePermission> buildPermissionsFromWebForm(Map formParameters, String parameterQualifier) {
        return this.buildPermissionsFromWebForm(this.space, formParameters, parameterQualifier);
    }

    private boolean isPersonalSpaceOwner(SpacePermission spacePermission) {
        ConfluenceUser userSubject = spacePermission.getUserSubject();
        if (this.space != null && this.space.isPersonal() && userSubject != null) {
            return userSubject.equals(this.space.getCreator());
        }
        return false;
    }

    public void setSpace(Space space) {
        this.space = space;
    }
}

