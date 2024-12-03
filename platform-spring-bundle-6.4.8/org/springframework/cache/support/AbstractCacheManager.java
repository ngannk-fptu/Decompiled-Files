/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

public abstract class AbstractCacheManager
implements CacheManager,
InitializingBean {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
    private volatile Set<String> cacheNames = Collections.emptySet();

    @Override
    public void afterPropertiesSet() {
        this.initializeCaches();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initializeCaches() {
        Collection<? extends Cache> caches = this.loadCaches();
        ConcurrentMap<String, Cache> concurrentMap = this.cacheMap;
        synchronized (concurrentMap) {
            this.cacheNames = Collections.emptySet();
            this.cacheMap.clear();
            LinkedHashSet<String> cacheNames = new LinkedHashSet<String>(caches.size());
            for (Cache cache : caches) {
                String name = cache.getName();
                this.cacheMap.put(name, this.decorateCache(cache));
                cacheNames.add(name);
            }
            this.cacheNames = Collections.unmodifiableSet(cacheNames);
        }
    }

    protected abstract Collection<? extends Cache> loadCaches();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Cache getCache(String name) {
        Cache cache = (Cache)this.cacheMap.get(name);
        if (cache != null) {
            return cache;
        }
        Cache missingCache = this.getMissingCache(name);
        if (missingCache != null) {
            ConcurrentMap<String, Cache> concurrentMap = this.cacheMap;
            synchronized (concurrentMap) {
                cache = (Cache)this.cacheMap.get(name);
                if (cache == null) {
                    cache = this.decorateCache(missingCache);
                    this.cacheMap.put(name, cache);
                    this.updateCacheNames(name);
                }
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    @Nullable
    protected final Cache lookupCache(String name) {
        return (Cache)this.cacheMap.get(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    protected final void addCache(Cache cache) {
        String name = cache.getName();
        ConcurrentMap<String, Cache> concurrentMap = this.cacheMap;
        synchronized (concurrentMap) {
            if (this.cacheMap.put(name, this.decorateCache(cache)) == null) {
                this.updateCacheNames(name);
            }
        }
    }

    private void updateCacheNames(String name) {
        LinkedHashSet<String> cacheNames = new LinkedHashSet<String>(this.cacheNames);
        cacheNames.add(name);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }

    protected Cache decorateCache(Cache cache) {
        return cache;
    }

    @Nullable
    protected Cache getMissingCache(String name) {
        return null;
    }
}

