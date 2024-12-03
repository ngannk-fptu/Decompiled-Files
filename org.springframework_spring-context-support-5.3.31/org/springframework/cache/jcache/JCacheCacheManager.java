/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.CacheManager
 *  javax.cache.Caching
 *  org.springframework.cache.Cache
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.jcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.cache.CacheManager;
import javax.cache.Caching;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JCacheCacheManager
extends AbstractTransactionSupportingCacheManager {
    @Nullable
    private CacheManager cacheManager;
    private boolean allowNullValues = true;

    public JCacheCacheManager() {
    }

    public JCacheCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheManager(@Nullable CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Nullable
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public void afterPropertiesSet() {
        if (this.getCacheManager() == null) {
            this.setCacheManager(Caching.getCachingProvider().getCacheManager());
        }
        super.afterPropertiesSet();
    }

    protected Collection<Cache> loadCaches() {
        CacheManager cacheManager = this.getCacheManager();
        Assert.state((cacheManager != null ? 1 : 0) != 0, (String)"No CacheManager set");
        LinkedHashSet<Cache> caches = new LinkedHashSet<Cache>();
        for (String cacheName : cacheManager.getCacheNames()) {
            javax.cache.Cache jcache = cacheManager.getCache(cacheName);
            caches.add((Cache)new JCacheCache((javax.cache.Cache<Object, Object>)jcache, this.isAllowNullValues()));
        }
        return caches;
    }

    protected Cache getMissingCache(String name) {
        CacheManager cacheManager = this.getCacheManager();
        Assert.state((cacheManager != null ? 1 : 0) != 0, (String)"No CacheManager set");
        javax.cache.Cache jcache = cacheManager.getCache(name);
        if (jcache != null) {
            return new JCacheCache((javax.cache.Cache<Object, Object>)jcache, this.isAllowNullValues());
        }
        return null;
    }
}

