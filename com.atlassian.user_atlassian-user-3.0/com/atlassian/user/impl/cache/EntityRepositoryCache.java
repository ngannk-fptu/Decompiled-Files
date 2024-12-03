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
import com.atlassian.user.Entity;
import com.atlassian.user.repository.RepositoryIdentifier;

public class EntityRepositoryCache {
    private final CacheFactory cacheFactory;
    private final String cacheName;

    public EntityRepositoryCache(CacheFactory cacheFactory, String cacheName) {
        this.cacheFactory = cacheFactory;
        this.cacheName = cacheName;
    }

    private Cache getCache() {
        return this.cacheFactory.getCache(this.cacheName);
    }

    public void put(Entity entity, RepositoryIdentifier repository) {
        this.getCache().put((Object)entity.getName(), (Object)repository);
    }

    public RepositoryIdentifier get(Entity entity) {
        return (RepositoryIdentifier)this.getCache().get((Object)entity.getName());
    }

    public void remove(Entity entity) {
        this.getCache().remove((Object)entity.getName());
    }
}

