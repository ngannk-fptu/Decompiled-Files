/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.aggregate;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraAggregateCacheStore {
    static final String CACHE_NAME = JiraAggregateCacheStore.class.getCanonicalName();
    static final String ERROR_CACHE_NAME = CACHE_NAME + "Errors";
    private final Cache<Long, JiraAggregate> cache;
    private final Cache<Long, JiraAggregate> errorCache;
    private final CacheManager cacheManager;
    private volatile Duration currentTtl = Duration.ofMinutes(5L);

    @Autowired
    public JiraAggregateCacheStore(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.cache = cacheManager.getCache(CACHE_NAME);
        this.errorCache = cacheManager.getCache(ERROR_CACHE_NAME);
        this.setTimeToLive(this.cache.getName(), this.currentTtl);
        this.setTimeToLive(this.errorCache.getName(), this.currentTtl);
    }

    JiraAggregate get(long pageId) {
        JiraAggregate aggregateData = (JiraAggregate)this.cache.get((Object)pageId);
        if (aggregateData == null) {
            aggregateData = (JiraAggregate)this.errorCache.get((Object)pageId);
        }
        return aggregateData;
    }

    void put(long pageId, JiraAggregate aggregateData) {
        if (aggregateData.isIncomplete()) {
            this.errorCache.put((Object)pageId, (Object)aggregateData);
        } else {
            this.cache.put((Object)pageId, (Object)aggregateData);
        }
    }

    void invalidate(long pageId) {
        this.cache.remove((Object)pageId);
        this.errorCache.remove((Object)pageId);
    }

    void invalidateAll() {
        this.cache.removeAll();
        this.errorCache.removeAll();
    }

    void setTimeToLive(long time, TimeUnit unit) {
        this.setTimeToLive(Duration.ofMillis(unit.toMillis(time)));
    }

    void setTimeToLive(Duration ttl) {
        if (!ttl.equals(this.currentTtl)) {
            if (ttl.toMillis() > this.currentTtl.toMillis()) {
                this.cache.removeAll();
            }
            this.setTimeToLive(this.cache.getName(), ttl);
            this.currentTtl = ttl;
        }
    }

    void setTimeToLive(String cacheName, Duration duration) {
        ManagedCache managedCache = this.cacheManager.getManagedCache(cacheName);
        if (managedCache != null) {
            managedCache.updateExpireAfterWrite(duration.toMillis(), TimeUnit.MILLISECONDS);
        }
    }
}

