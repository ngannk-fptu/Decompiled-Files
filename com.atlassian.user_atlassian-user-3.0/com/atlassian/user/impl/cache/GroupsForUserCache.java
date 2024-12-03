/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 */
package com.atlassian.user.impl.cache;

import com.atlassian.cache.CacheFactory;
import com.atlassian.user.User;
import com.atlassian.user.util.GenericCacheWrapper;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupsForUserCache {
    private final CacheFactory cacheFactory;
    private final String cacheName;

    public GroupsForUserCache(CacheFactory cacheFactory, String cacheName) {
        this.cacheFactory = cacheFactory;
        this.cacheName = cacheName;
    }

    private GenericCacheWrapper<String, List<String>> getCache() {
        return new GenericCacheWrapper<String, List<String>>(this.cacheFactory.getCache(this.cacheName));
    }

    public void put(User user, List<String> groupNames) {
        this.getCache().put(user.getName(), groupNames);
    }

    public List<String> get(User user) {
        return this.getCache().get(user.getName());
    }

    public void remove(User user) {
        this.getCache().remove(user.getName());
    }

    public void remove(List<String> usernames) {
        for (String username : usernames) {
            this.getCache().remove(username);
        }
    }
}

