/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.upm.core.permission;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

public abstract class UserAttributes {
    public static final UserAttributes REGULAR_USER = new FixedPermissionUserAttributes(false, false);
    public static final UserAttributes ADMIN_USER = new FixedPermissionUserAttributes(true, false);
    public static final UserAttributes SYSTEM_ADMIN_USER = new FixedPermissionUserAttributes(true, true);

    public abstract boolean isAdmin();

    public abstract boolean isSystemAdmin();

    public static UserAttributes fromCurrentUser(UserManager userManager) {
        return UserAttributes.fromUserKey(userManager.getRemoteUserKey(), userManager);
    }

    public static UserAttributes fromUserKey(UserKey userKey, UserManager userManager) {
        return userKey == null ? null : new UserKeyUserAttributes(userKey, userManager);
    }

    private static class UserKeyUserAttributes
    extends UserAttributes {
        private final UserKey userKey;
        private final UserManager userManager;

        UserKeyUserAttributes(UserKey userKey, UserManager userManager) {
            this.userKey = userKey;
            this.userManager = userManager;
        }

        @Override
        public boolean isAdmin() {
            return this.userManager.isAdmin(this.userKey);
        }

        @Override
        public boolean isSystemAdmin() {
            return this.userManager.isSystemAdmin(this.userKey);
        }

        public boolean equals(Object other) {
            if (other instanceof UserKeyUserAttributes) {
                UserKeyUserAttributes o = (UserKeyUserAttributes)other;
                return o.userKey.equals((Object)this.userKey) && o.userManager == this.userManager;
            }
            return false;
        }
    }

    private static class FixedPermissionUserAttributes
    extends UserAttributes {
        private final boolean admin;
        private final boolean systemAdmin;

        FixedPermissionUserAttributes(boolean admin, boolean systemAdmin) {
            this.admin = admin;
            this.systemAdmin = systemAdmin;
        }

        @Override
        public boolean isAdmin() {
            return this.admin;
        }

        @Override
        public boolean isSystemAdmin() {
            return this.systemAdmin;
        }
    }
}

