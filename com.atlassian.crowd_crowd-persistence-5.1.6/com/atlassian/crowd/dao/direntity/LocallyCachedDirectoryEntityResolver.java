/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.dao.direntity;

import com.atlassian.crowd.dao.direntity.DirectoryEntityResolver;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.util.cache.LocalCacheUtils;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nullable;

public class LocallyCachedDirectoryEntityResolver
implements DirectoryEntityResolver {
    private final ConcurrentMap<Object, ConcurrentMap<String, Object>> caches;
    private final Duration cacheTtl;

    public LocallyCachedDirectoryEntityResolver(Duration cacheTtl, ScheduledExecutorService cleanupPool) {
        this.caches = LocalCacheUtils.createExpiringAfterAccessMap(cacheTtl, cleanupPool);
        this.cacheTtl = cacheTtl;
    }

    @Override
    @Nullable
    public <T extends DirectoryEntity> T resolve(long directoryId, String name, Class<T> entityClass) {
        return (T)((DirectoryEntity)this.resolve(this.getCache(directoryId, entityClass), name, entityClass));
    }

    @Override
    public <T extends DirectoryEntity> void put(T entity) {
        ConcurrentMap<String, Object> cache = this.getCache(entity.getDirectoryId(), entity.getClass());
        cache.put(IdentifierUtils.toLowerCase((String)entity.getName()), entity);
    }

    @Override
    @Nullable
    public <T extends DirectoryEntity> List<T> resolveAllOrNothing(long directoryId, Collection<String> names, Class<T> entityClass) {
        ConcurrentMap<String, Object> cache = this.getCache(directoryId, entityClass);
        ArrayList<DirectoryEntity> results = new ArrayList<DirectoryEntity>();
        for (String name : names) {
            DirectoryEntity t = (DirectoryEntity)this.resolve(cache, name, entityClass);
            if (t == null) {
                return null;
            }
            results.add(t);
        }
        return results;
    }

    <T> T resolve(Map<String, Object> cache, String name, Class<T> entityClass) {
        Object entity = cache.get(IdentifierUtils.toLowerCase((String)name));
        return entityClass.isInstance(entity) ? (T)entityClass.cast(entity) : null;
    }

    @Override
    public void putAll(List<? extends DirectoryEntity> entities) {
        entities.forEach(this::put);
    }

    private ConcurrentMap<String, Object> getCache(long directoryId, Class<?> entityClass) {
        List<Serializable> key = Arrays.asList(directoryId, User.class.isAssignableFrom(entityClass));
        return this.caches.computeIfAbsent(key, k -> LocalCacheUtils.createExpiringAfterAccessMap(this.cacheTtl));
    }
}

