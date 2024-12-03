/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.user.User;

public class SystemPermissionsDelegate
implements PermissionDelegate {
    private SpacePermissionManager spacePermissionManager;

    public boolean canView(User user, Object target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canView(User user) {
        throw new UnsupportedOperationException();
    }

    public boolean canEdit(User user, Object target) {
        throw new UnsupportedOperationException();
    }

    public boolean canSetPermissions(User user, Object target) {
        throw new UnsupportedOperationException();
    }

    public boolean canRemove(User user, Object target) {
        throw new UnsupportedOperationException();
    }

    public boolean canExport(User user, Object target) {
        throw new UnsupportedOperationException();
    }

    public boolean canAdminister(User user, Object target) {
        return this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

