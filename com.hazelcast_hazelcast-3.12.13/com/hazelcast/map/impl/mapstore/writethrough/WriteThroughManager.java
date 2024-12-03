/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writethrough;

import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapDataStores;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreManager;

public class WriteThroughManager
implements MapStoreManager {
    private final MapDataStore mapDataStore;

    public WriteThroughManager(MapStoreContext mapStoreContext) {
        this.mapDataStore = MapDataStores.createWriteThroughStore(mapStoreContext);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public MapDataStore getMapDataStore(String mapName, int partitionId) {
        return this.mapDataStore;
    }
}

