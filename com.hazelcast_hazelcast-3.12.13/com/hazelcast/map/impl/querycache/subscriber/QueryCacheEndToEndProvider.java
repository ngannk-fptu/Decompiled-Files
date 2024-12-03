/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.NullQueryCache;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.UuidUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueryCacheEndToEndProvider<K, V> {
    private final ContextMutexFactory mutexFactory;
    private final ConcurrentMap<String, ConcurrentMap<String, InternalQueryCache<K, V>>> queryCacheRegistryPerMap;
    private final ConstructorFunction<String, ConcurrentMap<String, InternalQueryCache<K, V>>> queryCacheRegistryConstructor = new ConstructorFunction<String, ConcurrentMap<String, InternalQueryCache<K, V>>>(){

        @Override
        public ConcurrentMap<String, InternalQueryCache<K, V>> createNew(String arg) {
            return new ConcurrentHashMap();
        }
    };

    public QueryCacheEndToEndProvider(ContextMutexFactory mutexFactory) {
        this.mutexFactory = mutexFactory;
        this.queryCacheRegistryPerMap = new ConcurrentHashMap<String, ConcurrentMap<String, InternalQueryCache<K, V>>>();
    }

    public InternalQueryCache<K, V> getOrCreateQueryCache(String mapName, String cacheName, ConstructorFunction<String, InternalQueryCache<K, V>> constructor) {
        InternalQueryCache<K, V> existingQueryCache = this.getExistingQueryCacheOrNull(mapName, cacheName);
        if (existingQueryCache != null) {
            return existingQueryCache;
        }
        return this.tryCreateQueryCache(mapName, cacheName, constructor);
    }

    private InternalQueryCache<K, V> getExistingQueryCacheOrNull(String mapName, String cacheName) {
        InternalQueryCache queryCache;
        ConcurrentMap queryCacheRegistry = (ConcurrentMap)this.queryCacheRegistryPerMap.get(mapName);
        if (queryCacheRegistry != null && (queryCache = (InternalQueryCache)queryCacheRegistry.get(cacheName)) != null) {
            return queryCache;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public InternalQueryCache<K, V> tryCreateQueryCache(String mapName, String cacheName, ConstructorFunction<String, InternalQueryCache<K, V>> constructor) {
        ContextMutexFactory.Mutex mutex;
        block6: {
            InternalQueryCache<String, V> internalQueryCache;
            mutex = this.mutexFactory.mutexFor(mapName);
            try {
                ContextMutexFactory.Mutex mutex2 = mutex;
                // MONITORENTER : mutex2
                ConcurrentMap<String, InternalQueryCache<String, InternalQueryCache<String, V>>> queryCacheRegistry = ConcurrencyUtil.getOrPutIfAbsent(this.queryCacheRegistryPerMap, mapName, this.queryCacheRegistryConstructor);
                InternalQueryCache<String, V> queryCache = (InternalQueryCache<String, V>)queryCacheRegistry.get(cacheName);
                String cacheId = queryCache == null ? UuidUtil.newUnsecureUuidString() : queryCache.getCacheId();
                queryCache = constructor.createNew(cacheId);
                if (queryCache == NullQueryCache.NULL_QUERY_CACHE) break block6;
                queryCacheRegistry.put(cacheName, queryCache);
                internalQueryCache = queryCache;
                // MONITOREXIT : mutex2
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(mutex);
                throw throwable;
            }
            IOUtil.closeResource(mutex);
            return internalQueryCache;
        }
        InternalQueryCache<K, V> internalQueryCache = null;
        // MONITOREXIT : mutex2
        IOUtil.closeResource(mutex);
        return internalQueryCache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeSingleQueryCache(String mapName, String cacheName) {
        ContextMutexFactory.Mutex mutex = this.mutexFactory.mutexFor(mapName);
        try {
            ContextMutexFactory.Mutex mutex2 = mutex;
            synchronized (mutex2) {
                Map queryCacheRegistry = (Map)this.queryCacheRegistryPerMap.get(mapName);
                if (queryCacheRegistry != null) {
                    queryCacheRegistry.remove(cacheName);
                }
            }
        }
        finally {
            IOUtil.closeResource(mutex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroyAllQueryCaches(String mapName) {
        ContextMutexFactory.Mutex mutex = this.mutexFactory.mutexFor(mapName);
        try {
            ContextMutexFactory.Mutex mutex2 = mutex;
            synchronized (mutex2) {
                Map queryCacheRegistry = (Map)this.queryCacheRegistryPerMap.remove(mapName);
                if (queryCacheRegistry != null) {
                    for (InternalQueryCache queryCache : queryCacheRegistry.values()) {
                        queryCache.destroy();
                    }
                }
            }
        }
        finally {
            IOUtil.closeResource(mutex);
        }
    }

    public int getQueryCacheCount(String mapName) {
        Map queryCacheRegistry = (Map)this.queryCacheRegistryPerMap.get(mapName);
        if (queryCacheRegistry == null) {
            return 0;
        }
        return queryCacheRegistry.size();
    }
}

