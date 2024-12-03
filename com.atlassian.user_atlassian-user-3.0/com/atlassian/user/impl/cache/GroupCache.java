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

public class GroupCache {
    private final CacheFactory cacheFactory;
    private final String cacheName;
    public static final Group NULL_GROUP = new Group(){
        private String NAME = "NULL GROUP";

        public String getName() {
            return this.NAME;
        }

        public boolean equals(Object obj) {
            return obj.getClass().getName().equals(this.getClass().getName());
        }

        public int hashCode() {
            return this.getClass().getName().hashCode();
        }
    };

    public GroupCache(CacheFactory cacheFactory, String cacheName) {
        this.cacheFactory = cacheFactory;
        this.cacheName = cacheName;
    }

    private Cache getCache() {
        return this.cacheFactory.getCache(this.cacheName);
    }

    public void put(String groupName, Group group) {
        this.getCache().put((Object)groupName, (Object)(group == null ? NULL_GROUP : group));
    }

    public Group get(String groupName) {
        return (Group)this.getCache().get((Object)groupName);
    }

    public void remove(String groupName) {
        this.getCache().remove((Object)groupName);
    }
}

