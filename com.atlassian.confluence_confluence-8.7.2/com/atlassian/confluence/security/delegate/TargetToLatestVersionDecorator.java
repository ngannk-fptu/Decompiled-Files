/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.user.User;

public class TargetToLatestVersionDecorator
implements PermissionDelegate {
    private PermissionDelegate delegate;

    public TargetToLatestVersionDecorator(PermissionDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean canView(User user, Object target) {
        return this.delegate.canView(user, this.toLatestVersion(target));
    }

    @Override
    public boolean canView(User user) {
        return this.delegate.canView(user);
    }

    public boolean canEdit(User user, Object target) {
        return this.delegate.canEdit(user, this.toLatestVersion(target));
    }

    public boolean canSetPermissions(User user, Object target) {
        return this.delegate.canSetPermissions(user, this.toLatestVersion(target));
    }

    public boolean canRemove(User user, Object target) {
        return this.delegate.canRemove(user, this.toLatestVersion(target));
    }

    public boolean canRemoveHierarchy(User user, Object target) {
        return this.delegate.canRemoveHierarchy(user, target);
    }

    public boolean canMove(User user, Object source, Object target, String movePoint) {
        return this.delegate.canMove(user, source, target, movePoint);
    }

    public boolean canExport(User user, Object target) {
        return this.delegate.canExport(user, this.toLatestVersion(target));
    }

    public boolean canAdminister(User user, Object target) {
        return this.delegate.canAdminister(user, this.toLatestVersion(target));
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.delegate.canCreate(user, container);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        return this.delegate.canCreateInTarget(user, typeToCreate);
    }

    private Object toLatestVersion(Object target) {
        if (target instanceof Versioned) {
            return ((Versioned)target).getLatestVersion();
        }
        return target;
    }
}

