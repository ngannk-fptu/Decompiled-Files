/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionDelegate
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;

public class JsonPropertyPermissionDelegate
implements PermissionDelegate<Object> {
    private final PermissionManager permissionManager;

    public JsonPropertyPermissionDelegate(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public boolean canView(User user, Object target) {
        return this.hasPermission(user, target, Permission.VIEW);
    }

    public boolean canView(User user) {
        return false;
    }

    public boolean canEdit(User user, Object target) {
        return this.hasPermission(user, target, Permission.EDIT);
    }

    public boolean canSetPermissions(User user, Object target) {
        return false;
    }

    public boolean canRemove(User user, Object target) {
        return this.canEdit(user, target);
    }

    public boolean canExport(User user, Object target) {
        throw new UnsupportedOperationException();
    }

    public boolean canAdminister(User user, Object target) {
        return this.hasPermission(user, target, Permission.ADMINISTER);
    }

    public boolean canCreate(User user, Object container) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.EDIT, container);
    }

    public boolean canCreateInTarget(User user, Class typeToCreate) {
        return false;
    }

    private boolean hasPermission(User user, Object target, Permission permission) {
        CustomContentEntityObject entityObject = (CustomContentEntityObject)target;
        ContentEntityObject container = entityObject.getContainer();
        if (container != null) {
            return this.permissionManager.hasPermissionNoExemptions(user, permission, (Object)container);
        }
        return this.permissionManager.hasPermissionNoExemptions(user, permission, (Object)entityObject.getSpace());
    }
}

