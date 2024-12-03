/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 *  org.apache.shindig.common.cache.Cache
 *  org.apache.shindig.common.cache.CacheProvider
 *  org.apache.shindig.common.cache.LruCacheProvider
 */
package com.atlassian.gadgets.renderer.internal.cache;

import com.atlassian.gadgets.renderer.internal.cache.ClearableCacheProvider;
import com.google.common.collect.MapMaker;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.apache.shindig.common.cache.Cache;
import org.apache.shindig.common.cache.CacheProvider;
import org.apache.shindig.common.cache.LruCacheProvider;

public class DefaultClearableCacheProvider
implements ClearableCacheProvider {
    private CacheProvider delegateProvider;
    private final ConcurrentMap<String, ClearableCacheWrapper> caches = new MapMaker().makeMap();
    private ClearableCacheWrapper anonymousCache;

    public DefaultClearableCacheProvider() {
        this.resetDelegateProvider();
        this.anonymousCache = new ClearableCacheWrapper(this.delegateProvider.createCache(null));
    }

    private void resetDelegateProvider() {
        this.delegateProvider = new LruCacheProvider(1000);
    }

    public <K, V> Cache<K, V> createCache(String name) {
        ClearableCacheWrapper cache;
        if (name == null) {
            return this.anonymousCache;
        }
        ClearableCacheWrapper value = (ClearableCacheWrapper)this.caches.get(name);
        if (value == null && (value = this.caches.putIfAbsent(name, cache = new ClearableCacheWrapper(this.delegateProvider.createCache(name)))) == null) {
            value = cache;
        }
        return value;
    }

    @Override
    public void clear() {
        this.resetDelegateProvider();
        for (Map.Entry cacheEntry : this.caches.entrySet()) {
            String cacheName = (String)cacheEntry.getKey();
            ClearableCacheWrapper cache = (ClearableCacheWrapper)cacheEntry.getValue();
            cache.clear(this.delegateProvider.createCache(cacheName));
        }
        this.anonymousCache.clear(this.delegateProvider.createCache(null));
    }

    static class ClearableCacheWrapper<K, V>
    implements Cache<K, V> {
        private Cache<K, V> delegate;

        ClearableCacheWrapper(Cache<K, V> delegate) {
            this.delegate = delegate;
        }

        public V getElement(K key) {
            return (V)this.delegate.getElement(key);
        }

        public void addElement(K key, V value) {
            this.delegate.addElement(key, value);
        }

        public V removeElement(K key) {
            return (V)this.delegate.removeElement(key);
        }

        public long getCapacity() {
            return this.delegate.getCapacity();
        }

        public long getSize() {
            return this.delegate.getSize();
        }

        public void clear(Cache<K, V> delegate) {
            this.delegate = delegate;
        }
    }
}

