/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.subscriber.DefaultQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueryCacheFactory {
    private final ConcurrentMap<String, InternalQueryCache> internalQueryCaches = new ConcurrentHashMap<String, InternalQueryCache>();

    public InternalQueryCache create(QueryCacheRequest request, String cacheId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.internalQueryCaches, cacheId, new InternalQueryCacheConstructor(request));
    }

    public boolean remove(InternalQueryCache queryCache) {
        return this.internalQueryCaches.remove(queryCache.getCacheId(), queryCache);
    }

    public InternalQueryCache getOrNull(String cacheId) {
        return (InternalQueryCache)this.internalQueryCaches.get(cacheId);
    }

    public int getQueryCacheCount() {
        return this.internalQueryCaches.size();
    }

    private static class InternalQueryCacheConstructor
    implements ConstructorFunction<String, InternalQueryCache> {
        private final QueryCacheRequest request;

        InternalQueryCacheConstructor(QueryCacheRequest request) {
            this.request = request;
        }

        @Override
        public InternalQueryCache createNew(String cacheId) {
            IMap delegate = this.request.getMap();
            String mapName = this.request.getMapName();
            String cacheName = this.request.getCacheName();
            QueryCacheContext context = this.request.getContext();
            QueryCacheConfig queryCacheConfig = this.request.getQueryCacheConfig();
            DefaultQueryCache queryCache = new DefaultQueryCache(cacheId, cacheName, queryCacheConfig, delegate, context);
            MapListener listener = this.request.getListener();
            if (listener != null) {
                this.request.getContext().getSubscriberContext().getEventService().addListener(mapName, cacheId, listener);
            }
            return queryCache;
        }
    }
}

