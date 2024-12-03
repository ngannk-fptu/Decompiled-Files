/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.internal.user;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.ratelimiting.internal.user.AnonymousUserProfile;
import com.atlassian.ratelimiting.internal.user.CrowdUserService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Optional;
import javax.annotation.Nonnull;

public class UserProfileCache {
    private final Cache<UserKey, Optional<UserProfile>> internalUserProfileCache;

    public UserProfileCache(Cache<UserKey, Optional<UserProfile>> internalUserProfileCache) {
        this.internalUserProfileCache = internalUserProfileCache;
    }

    public Optional<UserProfile> get(@Nonnull UserKey userKey) {
        return (Optional)this.internalUserProfileCache.get((Object)userKey);
    }

    public static class Loader
    implements CacheLoader<UserKey, Optional<UserProfile>> {
        private final UserManager userManager;

        public Loader(UserManager userManager) {
            this.userManager = userManager;
        }

        @Nonnull
        public Optional<UserProfile> load(@Nonnull UserKey key) {
            return AnonymousUserProfile.isAnonymousRepresentativeUser(key) ? Optional.of(CrowdUserService.ANONYMOUS_REPRESENTATIVE_USER) : Optional.ofNullable(this.userManager.getUserProfile(key));
        }
    }
}

