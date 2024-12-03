/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapDataStores;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreManager;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindManager;
import com.hazelcast.map.impl.mapstore.writethrough.WriteThroughManager;

public final class MapStoreManagers {
    private MapStoreManagers() {
    }

    public static MapStoreManager createWriteThroughManager(MapStoreContext mapStoreContext) {
        return new WriteThroughManager(mapStoreContext);
    }

    public static MapStoreManager createWriteBehindManager(MapStoreContext mapStoreContext) {
        return new WriteBehindManager(mapStoreContext);
    }

    public static MapStoreManager emptyMapStoreManager() {
        return EmptyHolder.EMPTY;
    }

    private static MapStoreManager createEmptyManager() {
        return new MapStoreManager(){

            @Override
            public void start() {
            }

            @Override
            public void stop() {
            }

            @Override
            public MapDataStore getMapDataStore(String mapName, int partitionId) {
                return MapDataStores.emptyStore();
            }
        };
    }

    static /* synthetic */ MapStoreManager access$000() {
        return MapStoreManagers.createEmptyManager();
    }

    private static class EmptyHolder {
        static final MapStoreManager EMPTY = MapStoreManagers.access$000();

        private EmptyHolder() {
        }
    }
}

