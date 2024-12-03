/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 */
package com.atlassian.user.impl.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.util.List;

public class MembershipCache {
    private final CacheFactory cacheFactory;
    private final String cacheName;

    public MembershipCache(CacheFactory cacheFactory, String cacheName) {
        this.cacheFactory = cacheFactory;
        this.cacheName = cacheName;
    }

    private Cache getCache() {
        return this.cacheFactory.getCache(this.cacheName);
    }

    protected String getMembershipKey(String username, Group group) {
        return username + "_" + group.getName();
    }

    public void put(User user, Group group, boolean isMember) {
        this.getCache().put((Object)this.getMembershipKey(user.getName(), group), (Object)isMember);
    }

    public Boolean get(User user, Group group) {
        return (Boolean)this.getCache().get((Object)this.getMembershipKey(user.getName(), group));
    }

    public void remove(User user, Group group) {
        this.getCache().remove((Object)this.getMembershipKey(user.getName(), group));
    }

    public void remove(List usernames, Group group) {
        for (String username : usernames) {
            this.getCache().remove((Object)this.getMembershipKey(username, group));
        }
    }
}

