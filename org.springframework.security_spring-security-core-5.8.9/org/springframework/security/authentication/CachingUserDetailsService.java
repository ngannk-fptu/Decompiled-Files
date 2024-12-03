/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

public class CachingUserDetailsService
implements UserDetailsService {
    private UserCache userCache = new NullUserCache();
    private final UserDetailsService delegate;

    public CachingUserDetailsService(UserDetailsService delegate) {
        this.delegate = delegate;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
            user = this.delegate.loadUserByUsername(username);
        }
        Assert.notNull((Object)user, () -> "UserDetailsService " + this.delegate + " returned null for username " + username + ". This is an interface contract violation");
        this.userCache.putUserInCache(user);
        return user;
    }
}

