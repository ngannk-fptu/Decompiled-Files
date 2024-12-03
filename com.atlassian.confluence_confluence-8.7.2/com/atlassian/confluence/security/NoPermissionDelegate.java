/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.user.User;

public final class NoPermissionDelegate<T>
implements PermissionDelegate<T> {
    @Override
    public boolean canView(User user, T target) {
        return false;
    }

    @Override
    public boolean canView(User user) {
        return false;
    }

    @Override
    public boolean canEdit(User user, T target) {
        return false;
    }

    @Override
    public boolean canSetPermissions(User user, T target) {
        return false;
    }

    @Override
    public boolean canRemove(User user, T target) {
        return false;
    }

    @Override
    public boolean canExport(User user, T target) {
        return false;
    }

    @Override
    public boolean canAdminister(User user, T target) {
        return false;
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return false;
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        return false;
    }
}

