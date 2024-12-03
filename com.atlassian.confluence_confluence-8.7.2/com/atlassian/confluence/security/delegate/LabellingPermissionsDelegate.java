/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public class LabellingPermissionsDelegate
extends AbstractPermissionsDelegate<Labelling> {
    private PermissionManager permissionManager;

    @Override
    public boolean canView(User user, Labelling target) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, target.getLableable());
    }

    @Override
    public boolean canEdit(User user, Labelling target) {
        throw new UnsupportedOperationException("This permission check is not supported");
    }

    @Override
    public boolean canSetPermissions(User user, Labelling target) {
        throw new UnsupportedOperationException("This permission check is not supported");
    }

    @Override
    public boolean canRemove(User user, Labelling target) {
        throw new UnsupportedOperationException("This permission check is not supported");
    }

    @Override
    public boolean canExport(User user, Labelling target) {
        throw new UnsupportedOperationException("This permission check is not supported");
    }

    @Override
    public boolean canAdminister(User user, Labelling target) {
        throw new UnsupportedOperationException("This permission check is not supported");
    }

    @Override
    public boolean canCreate(User user, Object container) {
        throw new UnsupportedOperationException("This permission check is not supported");
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        EditableLabelable ceo;
        if (target instanceof Labelling && (ceo = ((Labelling)target).getLableable()) instanceof SpaceContentEntityObject) {
            return ((SpaceContentEntityObject)ceo).getSpace();
        }
        return null;
    }

    @Deprecated
    public void setPermissionManagerTarget(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

