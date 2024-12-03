/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.user.User;

public class ThreadLocalProfilePictureCache {
    private static final ThreadLocalCacheAccessor<Object, ProfilePictureInfo> cacheAccessor = ThreadLocalCacheAccessor.newInstance();

    public static ProfilePictureInfo hasProfilePicture(User user) {
        return cacheAccessor.get(new CachedUserProfilePicture(user));
    }

    public static void cacheHasProfilePicture(User user, ProfilePictureInfo p) {
        cacheAccessor.put(new CachedUserProfilePicture(user), p);
    }

    private static final class CachedUserProfilePicture {
        private final String username;

        private CachedUserProfilePicture(User user) {
            this.username = user == null ? null : user.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CachedUserProfilePicture that = (CachedUserProfilePicture)o;
            return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
        }

        public int hashCode() {
            int result = this.username != null ? this.username.hashCode() : 0;
            return result;
        }
    }
}

