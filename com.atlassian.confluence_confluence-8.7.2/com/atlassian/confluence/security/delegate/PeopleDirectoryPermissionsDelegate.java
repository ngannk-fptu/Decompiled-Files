/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.user.User;

public class PeopleDirectoryPermissionsDelegate
implements PermissionDelegate {
    private SpacePermissionManager spacePermissionManager;
    private ConfluenceAccessManager confluenceAccessManager;

    public boolean canView(User user, Object target) {
        if (this.shouldCheckBrowseUsersPermission(user)) {
            return this.spacePermissionManager.hasPermissionNoExemptions("VIEWUSERPROFILES", null, user);
        }
        return true;
    }

    @Override
    public boolean canView(User user) {
        throw new UnsupportedOperationException();
    }

    private boolean shouldCheckBrowseUsersPermission(User user) {
        return user == null || !this.confluenceAccessManager.getUserAccessStatus(user).hasLicensedAccess();
    }

    public boolean canEdit(User user, Object target) {
        throw new IllegalStateException("Edit permission does not apply to the People Directory.");
    }

    public boolean canSetPermissions(User user, Object target) {
        throw new IllegalStateException("canSet permission does not apply to the People Directory.");
    }

    public boolean canRemove(User user, Object target) {
        throw new IllegalStateException("canRemove permission does not apply to the People Directory.");
    }

    public boolean canExport(User user, Object target) {
        throw new IllegalStateException("canExport permission does not apply to the People Directory.");
    }

    public boolean canAdminister(User user, Object target) {
        throw new IllegalStateException("canAdminister permission does not apply to the People Directory.");
    }

    @Override
    public boolean canCreate(User user, Object container) {
        throw new IllegalStateException("canCreate permission does not apply to the People Directory.");
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    public void setConfluenceAccessManager(ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

