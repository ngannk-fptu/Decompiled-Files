/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.delegate.SpacePermissionsDelegate;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.user.User;

public class SpaceDescriptionPermissionsDelegate
implements PermissionDelegate<SpaceDescription> {
    private SpacePermissionsDelegate spacePermissionsDelegate;
    private SpacePermissionManager spacePermissionManager;

    @Override
    public boolean canView(User user, SpaceDescription target) {
        return this.spacePermissionsDelegate.canView(user, target.getSpace());
    }

    @Override
    public boolean canView(User user) {
        return this.spacePermissionsDelegate.canView(user);
    }

    @Override
    public boolean canEdit(User user, SpaceDescription target) {
        return this.spacePermissionsDelegate.canEdit(user, target.getSpace());
    }

    @Override
    public boolean canSetPermissions(User user, SpaceDescription target) {
        return this.spacePermissionsDelegate.canSetPermissions(user, target.getSpace());
    }

    @Override
    public boolean canRemove(User user, SpaceDescription target) {
        return this.spacePermissionsDelegate.canRemove(user, target.getSpace());
    }

    @Override
    public boolean canExport(User user, SpaceDescription target) {
        return this.spacePermissionsDelegate.canExport(user, target.getSpace());
    }

    @Override
    public boolean canAdminister(User user, SpaceDescription target) {
        return this.spacePermissionsDelegate.canAdminister(user, target.getSpace());
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.spacePermissionManager.hasPermissionNoExemptions("CREATESPACE", null, user);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        return this.spacePermissionsDelegate.canCreateInTarget(user, typeToCreate);
    }

    public void setSpacePermissionsDelegate(SpacePermissionsDelegate spacePermissionsDelegate) {
        this.spacePermissionsDelegate = spacePermissionsDelegate;
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

