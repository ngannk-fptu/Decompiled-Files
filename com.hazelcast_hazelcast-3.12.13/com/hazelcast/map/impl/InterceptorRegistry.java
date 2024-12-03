/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.MapInterceptor;
import com.hazelcast.spi.impl.operationexecutor.impl.PartitionOperationThread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptorRegistry {
    private volatile List<MapInterceptor> interceptors = Collections.emptyList();
    private volatile Map<String, MapInterceptor> id2InterceptorMap = Collections.emptyMap();

    public List<MapInterceptor> getInterceptors() {
        return this.interceptors;
    }

    public Map<String, MapInterceptor> getId2InterceptorMap() {
        return this.id2InterceptorMap;
    }

    public synchronized void register(String id, MapInterceptor interceptor) {
        assert (!(Thread.currentThread() instanceof PartitionOperationThread));
        if (this.id2InterceptorMap.containsKey(id)) {
            return;
        }
        HashMap<String, MapInterceptor> tmpMap = new HashMap<String, MapInterceptor>(this.id2InterceptorMap);
        tmpMap.put(id, interceptor);
        this.id2InterceptorMap = Collections.unmodifiableMap(tmpMap);
        ArrayList<MapInterceptor> tmpInterceptors = new ArrayList<MapInterceptor>(this.interceptors);
        tmpInterceptors.add(interceptor);
        this.interceptors = Collections.unmodifiableList(tmpInterceptors);
    }

    public synchronized void deregister(String id) {
        assert (!(Thread.currentThread() instanceof PartitionOperationThread));
        if (!this.id2InterceptorMap.containsKey(id)) {
            return;
        }
        HashMap<String, MapInterceptor> tmpMap = new HashMap<String, MapInterceptor>(this.id2InterceptorMap);
        MapInterceptor removedInterceptor = (MapInterceptor)tmpMap.remove(id);
        this.id2InterceptorMap = Collections.unmodifiableMap(tmpMap);
        ArrayList<MapInterceptor> tmpInterceptors = new ArrayList<MapInterceptor>(this.interceptors);
        tmpInterceptors.remove(removedInterceptor);
        this.interceptors = Collections.unmodifiableList(tmpInterceptors);
    }
}

