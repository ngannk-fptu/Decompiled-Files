/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.map.impl.querycache.publisher.QueryCacheListenerRegistry;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapListenerRegistry
implements Registry<String, QueryCacheListenerRegistry> {
    private final ConstructorFunction<String, QueryCacheListenerRegistry> registryConstructorFunction = new ConstructorFunction<String, QueryCacheListenerRegistry>(){

        @Override
        public QueryCacheListenerRegistry createNew(String mapName) {
            return new QueryCacheListenerRegistry(MapListenerRegistry.this.context, mapName);
        }
    };
    private final QueryCacheContext context;
    private final ConcurrentMap<String, QueryCacheListenerRegistry> listeners = new ConcurrentHashMap<String, QueryCacheListenerRegistry>();

    public MapListenerRegistry(QueryCacheContext context) {
        this.context = context;
    }

    @Override
    public QueryCacheListenerRegistry getOrCreate(String mapName) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.listeners, mapName, this.registryConstructorFunction);
    }

    @Override
    public QueryCacheListenerRegistry getOrNull(String mapName) {
        return (QueryCacheListenerRegistry)this.listeners.get(mapName);
    }

    @Override
    public Map<String, QueryCacheListenerRegistry> getAll() {
        return Collections.unmodifiableMap(this.listeners);
    }

    @Override
    public QueryCacheListenerRegistry remove(String id) {
        return (QueryCacheListenerRegistry)this.listeners.remove(id);
    }
}

