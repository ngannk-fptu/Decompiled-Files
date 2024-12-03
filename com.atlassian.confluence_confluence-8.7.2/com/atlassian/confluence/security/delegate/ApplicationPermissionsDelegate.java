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

public class ApplicationPermissionsDelegate
implements PermissionDelegate {
    private SpacePermissionManager spacePermissionManager;

    public boolean canView(User user, Object target) {
        return this.spacePermissionManager.hasPermissionNoExemptions("USECONFLUENCE", null, user);
    }

    @Override
    public boolean canView(User user) {
        throw new UnsupportedOperationException();
    }

    public boolean canEdit(User user, Object target) {
        throw new IllegalStateException("Edit permission does not apply on the application level.");
    }

    public boolean canSetPermissions(User user, Object target) {
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    public boolean canRemove(User user, Object target) {
        throw new IllegalStateException("Remove permission does not apply on the application level.");
    }

    public boolean canExport(User user, Object target) {
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    public boolean canAdminister(User user, Object target) {
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        throw new IllegalStateException("Create permission does not apply on the application level.");
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

