/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.DefaultMapOperationProvider;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.WANAwareOperationProvider;

public class MapOperationProviders {
    protected final MapServiceContext mapServiceContext;
    protected final MapOperationProvider wanAwareProvider;
    protected final MapOperationProvider defaultProvider = new DefaultMapOperationProvider();

    public MapOperationProviders(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.wanAwareProvider = new WANAwareOperationProvider(mapServiceContext, this.defaultProvider);
    }

    public MapOperationProvider getOperationProvider(String name) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(name);
        return mapContainer.isWanReplicationEnabled() ? this.wanAwareProvider : this.defaultProvider;
    }

    public MapOperationProvider getOperationProvider(MapConfig mapConfig) {
        if (mapConfig.getWanReplicationRef() == null) {
            return this.defaultProvider;
        }
        return this.wanAwareProvider;
    }
}

