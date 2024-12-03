/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails.cache;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

public class NullUserCache
implements UserCache {
    @Override
    public UserDetails getUserFromCache(String username) {
        return null;
    }

    @Override
    public void putUserInCache(UserDetails user) {
    }

    @Override
    public void removeUserFromCache(String username) {
    }
}

