/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.security;

import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ThreadLocalPermissionsCacheInternal {
    private static final ThreadLocalCacheAccessor<Object, Boolean> cacheAccessor = ThreadLocalCacheAccessor.newInstance();
    private static final ThreadLocalCacheAccessor<CachedUserAccessStatus, AccessStatus> accessTypeCacheAccessor = ThreadLocalCacheAccessor.newInstance();

    public static Boolean hasSpacePermission(String permission, Space space, User user) {
        return cacheAccessor.get(new CachedSpacePermission(user, permission, space));
    }

    public static void cacheSpacePermission(User user, String permission, Space space, boolean hasPermission) {
        cacheAccessor.put(new CachedSpacePermission(user, permission, space), hasPermission);
    }

    @Deprecated
    public static Boolean canUseConfluence(User user) {
        return cacheAccessor.get(new CachedUseConfluencePermission(user));
    }

    @Deprecated
    public static void cacheCanUseConfluence(User user, boolean canUse) {
        cacheAccessor.put(new CachedUseConfluencePermission(user), canUse);
    }

    public static Option<AccessStatus> getUserAccessStatus(User user) {
        return Option.option((Object)accessTypeCacheAccessor.get(new CachedUserAccessStatus(user)));
    }

    public static void cacheUserAccessStatus(@Nullable User user, @NonNull AccessStatus accessStatus) {
        accessTypeCacheAccessor.put(new CachedUserAccessStatus(user), accessStatus);
    }

    public static void flushUserAccessStatusForUser(@Nullable User user) {
        accessTypeCacheAccessor.put(new CachedUserAccessStatus(user), null);
    }

    public static void flushUserAccessStatusForAllUsers() {
        accessTypeCacheAccessor.flush();
    }

    public static void flush() {
        cacheAccessor.flush();
        accessTypeCacheAccessor.flush();
    }

    public static Boolean hasPermissionExemption(User user) {
        return cacheAccessor.get(new CachedPermissionExemption(user));
    }

    public static void cachePermissionExemption(User user, boolean exempt) {
        cacheAccessor.put(new CachedPermissionExemption(user), exempt);
    }

    public static boolean hasTemporaryPermissionExemption() {
        return cacheAccessor.get((Object)TemporaryPermissionException.INSTANCE) == Boolean.TRUE;
    }

    public static void enableTemporaryPermissionExemption() {
        cacheAccessor.put((Object)TemporaryPermissionException.INSTANCE, Boolean.TRUE);
    }

    public static void disableTemporaryPermissionExemption() {
        cacheAccessor.put((Object)TemporaryPermissionException.INSTANCE, null);
    }

    private static enum TemporaryPermissionException {
        INSTANCE;

    }

    private static class CachedPermissionExemption {
        private final String username;

        public CachedPermissionExemption(User user) {
            this.username = user == null ? null : user.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CachedPermissionExemption that = (CachedPermissionExemption)o;
            return this.username == null ? that.username == null : this.username.equals(that.username);
        }

        public int hashCode() {
            return this.username != null ? this.username.hashCode() : 0;
        }
    }

    private static final class CachedSpacePermission {
        private final String username;
        private final String permission;
        private final String spaceKey;

        private CachedSpacePermission(User user, String permission, Space space) {
            this.username = user == null ? null : user.getName();
            this.permission = permission;
            this.spaceKey = space == null ? null : space.getKey();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CachedSpacePermission that = (CachedSpacePermission)o;
            if (this.permission != null ? !this.permission.equals(that.permission) : that.permission != null) {
                return false;
            }
            if (this.spaceKey != null ? !this.spaceKey.equals(that.spaceKey) : that.spaceKey != null) {
                return false;
            }
            return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
        }

        public int hashCode() {
            int result = this.username != null ? this.username.hashCode() : 0;
            result = 31 * result + (this.permission != null ? this.permission.hashCode() : 0);
            result = 31 * result + (this.spaceKey != null ? this.spaceKey.hashCode() : 0);
            return result;
        }
    }

    private static final class CachedUserAccessStatus {
        private final String username;

        private CachedUserAccessStatus(User user) {
            this.username = user == null ? null : user.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CachedUserAccessStatus that = (CachedUserAccessStatus)o;
            return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
        }

        public int hashCode() {
            return this.username != null ? this.username.hashCode() : 0;
        }
    }

    private static final class CachedUseConfluencePermission {
        private final String username;

        private CachedUseConfluencePermission(User user) {
            this.username = user == null ? null : user.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CachedUseConfluencePermission that = (CachedUseConfluencePermission)o;
            return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
        }

        public int hashCode() {
            return this.username != null ? this.username.hashCode() : 0;
        }
    }
}

