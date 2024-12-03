/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.user.User;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Locale;

public abstract class Permission
implements Serializable {
    public static final Permission VIEW = new Permission("VIEW"){

        @Override
        public boolean checkAgainst(PermissionDelegate delegate, User user, Object target) {
            return delegate.canView(user, target);
        }

        @Override
        public boolean isMutative() {
            return false;
        }
    };
    public static final Permission EDIT = new Permission("EDIT"){

        @Override
        public boolean checkAgainst(PermissionDelegate delegate, User user, Object target) {
            return delegate.canEdit(user, target);
        }

        @Override
        public boolean isMutative() {
            return true;
        }
    };
    public static final Permission SET_PERMISSIONS = new Permission("SET_PERMISSIONS"){

        @Override
        public boolean checkAgainst(PermissionDelegate delegate, User user, Object target) {
            return delegate.canSetPermissions(user, target);
        }

        @Override
        public boolean isMutative() {
            return true;
        }
    };
    public static final Permission REMOVE = new Permission("REMOVE"){

        @Override
        public boolean checkAgainst(PermissionDelegate delegate, User user, Object target) {
            return delegate.canRemove(user, target);
        }

        @Override
        public boolean isMutative() {
            return true;
        }
    };
    public static final Permission EXPORT = new Permission("EXPORT"){

        @Override
        public boolean checkAgainst(PermissionDelegate delegate, User user, Object target) {
            return delegate.canExport(user, target);
        }

        @Override
        public boolean isMutative() {
            return false;
        }
    };
    public static final Permission ADMINISTER = new Permission("ADMINISTER"){

        @Override
        public boolean checkAgainst(PermissionDelegate delegate, User user, Object target) {
            return delegate.canAdminister(user, target);
        }

        @Override
        public boolean isMutative() {
            return false;
        }
    };
    private final String myName;
    private static final Permission[] ALL_PERMISSIONS = new Permission[]{VIEW, EDIT, SET_PERMISSIONS, REMOVE, EXPORT, ADMINISTER};

    public static Permission forName(String permissionName) {
        String upperName = permissionName.toUpperCase(Locale.ENGLISH);
        for (Permission permission : ALL_PERMISSIONS) {
            if (!permission.toString().equals(upperName)) continue;
            return permission;
        }
        throw new IllegalArgumentException("Permission not found with name: " + permissionName);
    }

    private Permission(String name) {
        this.myName = name;
    }

    public String toString() {
        return this.myName;
    }

    public abstract boolean checkAgainst(PermissionDelegate var1, User var2, Object var3);

    public final boolean equals(Object that) {
        return super.equals(that);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    Object readResolve() throws ObjectStreamException {
        for (Permission permission : ALL_PERMISSIONS) {
            if (!this.myName.equals(permission.myName)) continue;
            return permission;
        }
        throw new IllegalStateException("No permission can be deserialized with name: " + this.myName);
    }

    public abstract boolean isMutative();
}

