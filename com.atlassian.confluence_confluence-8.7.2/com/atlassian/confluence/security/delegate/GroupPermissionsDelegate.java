/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;

public class GroupPermissionsDelegate
implements PermissionDelegate<Group> {
    private SpacePermissionManager spacePermissionManager;

    @Override
    public boolean canView(User user, Group target) {
        return true;
    }

    @Override
    public boolean canView(User user) {
        return true;
    }

    @Override
    public boolean canEdit(User user, Group group) {
        return this.hasRelevantAdministrativePermission(user, group);
    }

    @Override
    public boolean canRemove(User user, Group target) {
        return this.hasRelevantAdministrativePermission(user, target);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canExport(User user, Group target) {
        throw new UnsupportedOperationException("Export privileges is undefined for groups and hence does not apply.");
    }

    @Override
    public boolean canSetPermissions(User user, Group target) {
        return this.hasRelevantAdministrativePermission(user, target);
    }

    private boolean hasRelevantAdministrativePermission(User user, Group targetGroup) {
        if (this.spacePermissionManager.groupHasPermission("SYSTEMADMINISTRATOR", null, targetGroup.getName()) || "confluence-administrators".equals(targetGroup.getName())) {
            return this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
        }
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    @Override
    public boolean canAdminister(User user, Group target) {
        throw new UnsupportedOperationException("Administer privileges is undefined for groups and hence does not apply.");
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

