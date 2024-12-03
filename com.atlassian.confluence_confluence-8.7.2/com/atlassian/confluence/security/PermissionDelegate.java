/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.user.User;

public interface PermissionDelegate<TARGET> {
    public boolean canView(User var1, TARGET var2);

    public boolean canView(User var1);

    public boolean canEdit(User var1, TARGET var2);

    public boolean canSetPermissions(User var1, TARGET var2);

    public boolean canRemove(User var1, TARGET var2);

    default public boolean canMove(User user, TARGET source, Object target, String movePoint) {
        throw new UnsupportedOperationException("Hierarchy move permissions are not supported for this content type");
    }

    default public boolean canRemoveHierarchy(User user, TARGET target) {
        throw new UnsupportedOperationException("Hierarchy remove permissions are not supported for this content type");
    }

    public boolean canExport(User var1, TARGET var2);

    public boolean canAdminister(User var1, TARGET var2);

    public boolean canCreate(User var1, Object var2);

    public boolean canCreateInTarget(User var1, Class var2);
}

