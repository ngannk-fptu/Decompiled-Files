/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.links.AbstractLink;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;

public class LinkPermissionsDelegate
implements PermissionDelegate<AbstractLink> {
    private PermissionManager permissionManager;

    @Override
    public boolean canView(User user, AbstractLink target) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, this.getContent(target));
    }

    @Override
    public boolean canView(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canEdit(User user, AbstractLink target) {
        throw new UnsupportedOperationException("Links do not support edit permissions");
    }

    @Override
    public boolean canSetPermissions(User user, AbstractLink target) {
        throw new UnsupportedOperationException("Links do not support permission-setting permissions");
    }

    @Override
    public boolean canRemove(User user, AbstractLink target) {
        throw new UnsupportedOperationException("Links do not support remove permissions");
    }

    @Override
    public boolean canExport(User user, AbstractLink target) {
        throw new UnsupportedOperationException("Links do not support export permissions");
    }

    @Override
    public boolean canAdminister(User user, AbstractLink target) {
        throw new UnsupportedOperationException("Links do not support administer permissions");
    }

    @Override
    public boolean canCreate(User user, Object container) {
        throw new UnsupportedOperationException("Links do not support create permissions");
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        throw new UnsupportedOperationException("Links do not support create permissions. Type received: " + typeToCreate);
    }

    @Deprecated
    public void setPermissionManagerTarget(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    private Object getContent(AbstractLink target) {
        return target.getSourceContent();
    }
}

