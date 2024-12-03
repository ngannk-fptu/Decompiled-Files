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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.lang.Nullable;

public class NoOpCacheManager
implements CacheManager {
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>(16);
    private final Set<String> cacheNames = new LinkedHashSet<String>(16);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Cache getCache(String name) {
        Cache cache = (Cache)this.caches.get(name);
        if (cache == null) {
            this.caches.computeIfAbsent(name, key -> new NoOpCache(name));
            Set<String> set = this.cacheNames;
            synchronized (set) {
                this.cacheNames.add(name);
            }
        }
        return (Cache)this.caches.get(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<String> getCacheNames() {
        Set<String> set = this.cacheNames;
        synchronized (set) {
            return Collections.unmodifiableSet(this.cacheNames);
        }
    }
}

