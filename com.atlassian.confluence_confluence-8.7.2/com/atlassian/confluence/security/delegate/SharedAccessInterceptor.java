/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.user.User;
import java.util.function.BiPredicate;

public class SharedAccessInterceptor
implements PermissionDelegate {
    private final PermissionDelegate delegate;

    public SharedAccessInterceptor(PermissionDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean canView(User user, Object target) {
        return this.checkAccess(user, target, this.delegate::canView);
    }

    @Override
    public boolean canView(User user) {
        return this.delegate.canView(user);
    }

    public boolean canEdit(User user, Object target) {
        return this.checkAccess(user, target, this.delegate::canEdit);
    }

    public boolean canSetPermissions(User user, Object target) {
        return this.checkAccess(user, target, this.delegate::canSetPermissions);
    }

    public boolean canRemove(User user, Object target) {
        return this.checkAccess(user, target, this.delegate::canRemove);
    }

    public boolean canRemoveHierarchy(User user, Object target) {
        return this.delegate.canRemoveHierarchy(user, target);
    }

    public boolean canMove(User user, Object source, Object target, String movePoint) {
        return this.delegate.canMove(user, source, target, movePoint);
    }

    public boolean canExport(User user, Object target) {
        return this.checkAccess(user, target, this.delegate::canExport);
    }

    public boolean canAdminister(User user, Object target) {
        return this.delegate.canAdminister(user, target);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.delegate.canCreate(user, container);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        return this.delegate.canCreateInTarget(user, typeToCreate);
    }

    private boolean checkAccess(User user, Object target, BiPredicate<User, Object> permissionsCheck) {
        ContentEntityObject ceo;
        if (target instanceof ContentEntityObject && !(ceo = (ContentEntityObject)target).sharedAccessAllowed(user)) {
            return false;
        }
        return permissionsCheck.test(user, target);
    }
}

