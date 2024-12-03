/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.user.User;

public class SpacePermissionsDelegate
extends AbstractPermissionsDelegate<Space> {
    @Override
    public boolean canView(User user, Space target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target);
    }

    @Override
    public boolean canEdit(User user, Space target) {
        return this.canView(user, target) && this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canSetPermissions(User user, Space target) {
        return this.canView(user, target) && this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canRemove(User user, Space target) {
        return this.canView(user, target) && this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canExport(User user, Space target) {
        return this.canView(user, target) && this.hasSpaceLevelPermission("EXPORTSPACE", user, target);
    }

    @Override
    public boolean canAdminister(User user, Space target) {
        return this.canView(user, target) && this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        if (PermissionManager.TARGET_APPLICATION.equals(container)) {
            return this.spacePermissionManager.hasPermissionNoExemptions("CREATESPACE", null, user);
        }
        if (container instanceof PersonalInformation) {
            return this.spacePermissionManager.hasPermissionNoExemptions("PERSONALSPACE", null, user);
        }
        throw new IllegalArgumentException("Can only create spaces within the application, or for a user");
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        return (Space)target;
    }
}

