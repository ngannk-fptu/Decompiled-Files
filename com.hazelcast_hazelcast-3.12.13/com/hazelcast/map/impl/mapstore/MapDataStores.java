/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.mapstore.EmptyMapDataStore;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueues;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindStore;
import com.hazelcast.map.impl.mapstore.writethrough.WriteThroughStore;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.concurrent.atomic.AtomicInteger;

public final class MapDataStores {
    public static final MapDataStore EMPTY_MAP_DATA_STORE = new EmptyMapDataStore();

    private MapDataStores() {
    }

    public static <K, V> MapDataStore<K, V> createWriteBehindStore(MapStoreContext mapStoreContext, int partitionId, WriteBehindProcessor writeBehindProcessor) {
        MapServiceContext mapServiceContext = mapStoreContext.getMapServiceContext();
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        MapStoreConfig mapStoreConfig = mapStoreContext.getMapStoreConfig();
        InternalSerializationService serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        WriteBehindStore mapDataStore = new WriteBehindStore(mapStoreContext, partitionId, serializationService);
        mapDataStore.setWriteBehindQueue(MapDataStores.newWriteBehindQueue(mapServiceContext, mapStoreConfig.isWriteCoalescing()));
        mapDataStore.setWriteBehindProcessor(writeBehindProcessor);
        return mapDataStore;
    }

    private static WriteBehindQueue newWriteBehindQueue(MapServiceContext mapServiceContext, boolean writeCoalescing) {
        HazelcastProperties hazelcastProperties = mapServiceContext.getNodeEngine().getProperties();
        int capacity = hazelcastProperties.getInteger(GroupProperty.MAP_WRITE_BEHIND_QUEUE_CAPACITY);
        AtomicInteger counter = mapServiceContext.getWriteBehindQueueItemCounter();
        return writeCoalescing ? WriteBehindQueues.createDefaultWriteBehindQueue() : WriteBehindQueues.createBoundedWriteBehindQueue(capacity, counter);
    }

    public static <K, V> MapDataStore<K, V> createWriteThroughStore(MapStoreContext mapStoreContext) {
        MapStoreWrapper store = mapStoreContext.getMapStoreWrapper();
        MapServiceContext mapServiceContext = mapStoreContext.getMapServiceContext();
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        InternalSerializationService serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        return new WriteThroughStore(store, serializationService);
    }

    public static <K, V> MapDataStore<K, V> emptyStore() {
        return EMPTY_MAP_DATA_STORE;
    }
}

