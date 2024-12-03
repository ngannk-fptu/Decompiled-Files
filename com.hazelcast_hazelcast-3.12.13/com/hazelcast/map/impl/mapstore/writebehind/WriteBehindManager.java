/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapDataStores;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreManager;
import com.hazelcast.map.impl.mapstore.writebehind.StoreEvent;
import com.hazelcast.map.impl.mapstore.writebehind.StoreListener;
import com.hazelcast.map.impl.mapstore.writebehind.StoreWorker;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessors;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindStore;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.recordstore.RecordStore;

public class WriteBehindManager
implements MapStoreManager {
    private final WriteBehindProcessor writeBehindProcessor;
    private final StoreWorker storeWorker;
    private final MapStoreContext mapStoreContext;

    public WriteBehindManager(MapStoreContext mapStoreContext) {
        this.mapStoreContext = mapStoreContext;
        this.writeBehindProcessor = this.newWriteBehindProcessor(mapStoreContext);
        this.storeWorker = new StoreWorker(mapStoreContext, this.writeBehindProcessor);
    }

    @Override
    public void start() {
        this.storeWorker.start();
    }

    @Override
    public void stop() {
        this.storeWorker.stop();
    }

    @Override
    public MapDataStore getMapDataStore(String mapName, int partitionId) {
        return MapDataStores.createWriteBehindStore(this.mapStoreContext, partitionId, this.writeBehindProcessor);
    }

    private WriteBehindProcessor newWriteBehindProcessor(MapStoreContext mapStoreContext) {
        WriteBehindProcessor writeBehindProcessor = WriteBehindProcessors.createWriteBehindProcessor(mapStoreContext);
        InternalStoreListener storeListener = new InternalStoreListener(mapStoreContext);
        writeBehindProcessor.addStoreListener(storeListener);
        return writeBehindProcessor;
    }

    private static class InternalStoreListener
    implements StoreListener<DelayedEntry> {
        private final MapStoreContext mapStoreContext;

        InternalStoreListener(MapStoreContext mapStoreContext) {
            this.mapStoreContext = mapStoreContext;
        }

        @Override
        public void beforeStore(StoreEvent<DelayedEntry> storeEvent) {
        }

        @Override
        public void afterStore(StoreEvent<DelayedEntry> storeEvent) {
            DelayedEntry delayedEntry = storeEvent.getSource();
            int partitionId = delayedEntry.getPartitionId();
            WriteBehindStore writeBehindStore = this.getWriteBehindStoreOrNull(partitionId);
            if (writeBehindStore == null) {
                return;
            }
            writeBehindStore.removeFromStagingArea(delayedEntry);
        }

        private WriteBehindStore getWriteBehindStoreOrNull(int partitionId) {
            MapStoreContext mapStoreContext = this.mapStoreContext;
            MapServiceContext mapServiceContext = mapStoreContext.getMapServiceContext();
            PartitionContainer partitionContainer = mapServiceContext.getPartitionContainer(partitionId);
            RecordStore recordStore = partitionContainer.getExistingRecordStore(mapStoreContext.getMapName());
            if (recordStore == null) {
                return null;
            }
            return (WriteBehindStore)recordStore.getMapDataStore();
        }
    }
}

