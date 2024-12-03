/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.core.IFunction;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueryCacheListenerRegistry
implements Registry<String, String> {
    private final ConstructorFunction<String, String> registryConstructorFunction = new ConstructorFunction<String, String>(){

        @Override
        public String createNew(String ignored) {
            IFunction<String, String> registration = QueryCacheListenerRegistry.this.context.getPublisherContext().getListenerRegistrator();
            return registration.apply(QueryCacheListenerRegistry.this.mapName);
        }
    };
    private final String mapName;
    private final QueryCacheContext context;
    private final ConcurrentMap<String, String> listeners;

    public QueryCacheListenerRegistry(QueryCacheContext context, String mapName) {
        this.context = context;
        this.mapName = mapName;
        this.listeners = new ConcurrentHashMap<String, String>();
    }

    @Override
    public String getOrCreate(String cacheId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.listeners, cacheId, this.registryConstructorFunction);
    }

    @Override
    public String getOrNull(String cacheId) {
        return (String)this.listeners.get(cacheId);
    }

    @Override
    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(this.listeners);
    }

    @Override
    public String remove(String cacheId) {
        return (String)this.listeners.remove(cacheId);
    }
}

