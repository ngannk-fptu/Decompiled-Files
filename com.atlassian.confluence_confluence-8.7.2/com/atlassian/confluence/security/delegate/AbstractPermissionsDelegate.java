/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public abstract class AbstractPermissionsDelegate<TARGET>
implements PermissionDelegate<TARGET> {
    protected SpacePermissionManager spacePermissionManager;

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    protected boolean hasSpaceLevelPermission(String permission, User user, Object target) {
        return this.spacePermissionManager.hasPermissionNoExemptions(permission, this.getSpaceFrom(target), user);
    }

    @Override
    public boolean canView(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException();
    }

    protected abstract Space getSpaceFrom(Object var1);
}

