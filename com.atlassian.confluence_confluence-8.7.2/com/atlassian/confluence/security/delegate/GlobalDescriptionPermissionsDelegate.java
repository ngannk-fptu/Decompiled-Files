/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.user.User;

public class GlobalDescriptionPermissionsDelegate
implements PermissionDelegate<GlobalDescription> {
    private SpacePermissionManager spacePermissionManager;

    @Override
    public boolean canAdminister(User user, GlobalDescription target) {
        return this.isApplicationAdminOrSysAdmin(user);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.isApplicationAdminOrSysAdmin(user);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canEdit(User user, GlobalDescription target) {
        return this.isApplicationAdminOrSysAdmin(user);
    }

    @Override
    public boolean canExport(User user, GlobalDescription target) {
        return this.isApplicationAdminOrSysAdmin(user);
    }

    @Override
    public boolean canRemove(User user, GlobalDescription target) {
        return this.isApplicationAdminOrSysAdmin(user);
    }

    @Override
    public boolean canSetPermissions(User user, GlobalDescription target) {
        return this.isApplicationAdminOrSysAdmin(user);
    }

    @Override
    public boolean canView(User user, GlobalDescription target) {
        return true;
    }

    @Override
    public boolean canView(User user) {
        throw new UnsupportedOperationException();
    }

    private boolean isApplicationAdminOrSysAdmin(User user) {
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

