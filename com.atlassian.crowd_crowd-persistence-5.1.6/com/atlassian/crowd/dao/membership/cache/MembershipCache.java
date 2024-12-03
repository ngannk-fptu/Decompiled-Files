/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.DirectoryEntities
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.cache.Cache;
import com.atlassian.crowd.dao.direntity.DirectoryEntityResolver;
import com.atlassian.crowd.dao.membership.cache.CacheFactory;
import com.atlassian.crowd.dao.membership.cache.CacheInvalidations;
import com.atlassian.crowd.dao.membership.cache.QueryType;
import com.atlassian.crowd.dao.membership.cache.QueryTypeCacheKey;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.DirectoryEntities;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.InternalDirectoryEntity;
import com.atlassian.crowd.util.cache.LocalCacheUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class MembershipCache {
    private final CacheFactory cacheFactory;
    private final ConcurrentMap<QueryTypeCacheKey, Cache<String, List<String>>> caches;
    @Nullable
    private final DirectoryEntityResolver entityResolver;
    private final Set<QueryType> cacheableTypes;
    private final Duration cacheTtl;
    private final int groupMembershipCacheMax;
    private final int queryTypeInvalidationThreshold;
    ThreadLocal<CacheInvalidations> cacheInvalidationThreadLocal = new ThreadLocal();

    public MembershipCache(CacheFactory cacheFactory, Set<QueryType> cacheableTypes, Duration cacheTtl, int groupMembershipCacheMax, int queryTypeInvalidationThreshold, @Nullable DirectoryEntityResolver entityResolver, ScheduledExecutorService cleanupPool) {
        this.cacheFactory = cacheFactory;
        this.cacheableTypes = ImmutableSet.copyOf(cacheableTypes);
        this.cacheTtl = cacheTtl;
        this.groupMembershipCacheMax = groupMembershipCacheMax;
        this.queryTypeInvalidationThreshold = queryTypeInvalidationThreshold;
        this.caches = LocalCacheUtils.createExpiringAfterAccessMap(cacheTtl, cleanupPool);
        this.entityResolver = entityResolver;
    }

    public Set<QueryType> getCacheableTypes() {
        return this.cacheableTypes;
    }

    public void invalidateCache(long directoryId) {
        this.getCacheInvalidation().addInvalidation(directoryId);
    }

    public void invalidateCache(long directoryId, QueryType queryType) {
        this.getCacheInvalidation().addInvalidation(directoryId, queryType);
    }

    public void invalidateCache(long directoryId, QueryType queryType, String key) {
        this.getCacheInvalidation().addInvalidation(directoryId, queryType, key);
    }

    public <T> void put(long directoryId, QueryType queryType, String key, List<T> data) {
        QueryTypeCacheKey cacheKey = new QueryTypeCacheKey(directoryId, queryType);
        if (!this.isInvalidated(cacheKey, key)) {
            T first;
            if (this.entityResolver != null && !data.isEmpty() && (first = data.get(0)) instanceof DirectoryEntity && this.supports(first.getClass())) {
                this.entityResolver.putAll(data);
            }
            this.getOrCreateCache(cacheKey).put((Object)IdentifierUtils.toLowerCase((String)key), this.namesOf(data));
        }
    }

    private List<String> namesOf(List<?> list) {
        if (list.isEmpty() || list.get(0) instanceof String) {
            return ImmutableList.copyOf(list);
        }
        return ImmutableList.copyOf((Iterable)DirectoryEntities.namesOf(list));
    }

    @Nullable
    public <T> List<T> get(long directoryId, QueryType queryType, String key, Class<T> returnType) {
        List<String> names = this.getNames(directoryId, queryType, key);
        if (returnType == String.class) {
            return names;
        }
        if (names == null || !this.supports(returnType)) {
            return null;
        }
        return this.entityResolver.resolveAllOrNothing(directoryId, names, returnType);
    }

    @Nullable
    public List<String> getNames(long directoryId, QueryType queryType, String key) {
        QueryTypeCacheKey cacheKey = new QueryTypeCacheKey(directoryId, queryType);
        if (this.isInvalidated(cacheKey, key)) {
            return null;
        }
        return (List)this.getOrCreateCache(cacheKey).get((Object)IdentifierUtils.toLowerCase((String)key));
    }

    protected void processInvalidations(CacheInvalidations invalidations) {
        invalidations.getQueryTypesInvalidations().stream().map(this::getOrCreateCache).forEach(Cache::removeAll);
        for (Map.Entry<QueryTypeCacheKey, Set<String>> entry : invalidations.getKeyInvalidations().entrySet()) {
            entry.getValue().forEach(arg_0 -> this.getOrCreateCache(entry.getKey()).remove(arg_0));
        }
    }

    protected Cache<String, List<String>> getOrCreateCache(QueryTypeCacheKey cacheKey) {
        Preconditions.checkArgument((boolean)this.cacheableTypes.contains((Object)cacheKey.getQueryType()));
        return this.caches.computeIfAbsent(cacheKey, k -> this.cacheFactory.createCache((QueryTypeCacheKey)k, this.cacheTtl, this.groupMembershipCacheMax));
    }

    protected CacheInvalidations getCacheInvalidation() {
        CacheInvalidations invalidations = this.cacheInvalidationThreadLocal.get();
        if (invalidations == null) {
            CacheInvalidations newInvalidations;
            invalidations = newInvalidations = new CacheInvalidations(this.cacheableTypes, this.queryTypeInvalidationThreshold);
            this.cacheInvalidationThreadLocal.set(invalidations);
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronization(){

                public void afterCompletion(int status) {
                    MembershipCache.this.cacheInvalidationThreadLocal.remove();
                    if (status == 0 || status == 2) {
                        MembershipCache.this.processInvalidations(newInvalidations);
                    }
                }

                public void suspend() {
                    MembershipCache.this.cacheInvalidationThreadLocal.remove();
                }

                public void resume() {
                    MembershipCache.this.cacheInvalidationThreadLocal.set(newInvalidations);
                }
            });
        }
        return invalidations;
    }

    protected boolean isInvalidated(QueryTypeCacheKey cacheKey, String key) {
        CacheInvalidations invalidations = this.cacheInvalidationThreadLocal.get();
        return invalidations != null && invalidations.isInvalidated(cacheKey, key);
    }

    public void clear() {
        this.caches.values().forEach(Cache::removeAll);
    }

    public void clear(long directoryId) {
        for (QueryType queryType : this.cacheableTypes) {
            this.getOrCreateCache(new QueryTypeCacheKey(directoryId, queryType)).removeAll();
        }
    }

    public int cacheCount() {
        return this.caches.size();
    }

    public boolean supports(Class<?> resultClass) {
        return this.entityResolver == null ? resultClass == String.class : !InternalDirectoryEntity.class.isAssignableFrom(resultClass);
    }
}

