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

public class UserPermissionsDelegate
implements PermissionDelegate<User> {
    private SpacePermissionManager spacePermissionManager;
    private ConfluenceAccessManager confluenceAccessManager;

    @Override
    public boolean canView(User user, User target) {
        return target != null && target.equals(user) || this.canView(user);
    }

    @Override
    public boolean canView(User user) {
        if (this.shouldCheckBrowseUsersPermission(user)) {
            return this.spacePermissionManager.hasPermissionNoExemptions("VIEWUSERPROFILES", null, user);
        }
        return true;
    }

    private boolean shouldCheckBrowseUsersPermission(User user) {
        return this.isAnonymous(user) || !this.confluenceAccessManager.getUserAccessStatus(user).hasLicensedAccess();
    }

    @Override
    public boolean canEdit(User user, User target) {
        if (this.isAnonymous(user)) {
            return false;
        }
        if (target.getName().equals(user.getName())) {
            return true;
        }
        return this.hasRelevantAdministrativePermissions(user, target);
    }

    @Override
    public boolean canRemove(User user, User target) {
        if (this.isAnonymous(user)) {
            return false;
        }
        return this.hasRelevantAdministrativePermissions(user, target);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        if (this.isAnonymous(user)) {
            return false;
        }
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canExport(User user, User target) {
        throw new UnsupportedOperationException("Export privileges do not apply to users");
    }

    @Override
    public boolean canSetPermissions(User user, User target) {
        return this.hasRelevantAdministrativePermissions(user, target);
    }

    @Override
    public boolean canAdminister(User user, User target) {
        throw new UnsupportedOperationException("Administer privileges is undefined for users and hence does not apply.");
    }

    private boolean hasRelevantAdministrativePermissions(User user, User targetUser) {
        if (this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, targetUser)) {
            return this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
        }
        return this.spacePermissionManager.hasPermissionNoExemptions("ADMINISTRATECONFLUENCE", null, user) || this.spacePermissionManager.hasPermissionNoExemptions("SYSTEMADMINISTRATOR", null, user);
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setConfluenceAccessManager(ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }

    private boolean isAnonymous(User user) {
        return user == null;
    }
}

