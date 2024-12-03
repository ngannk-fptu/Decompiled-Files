/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.writebehind.IPredicate;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindStore;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.Clock;
import com.hazelcast.util.CollectionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StoreWorker
implements Runnable {
    private final String mapName;
    private final MapServiceContext mapServiceContext;
    private final IPartitionService partitionService;
    private final ExecutionService executionService;
    private final WriteBehindProcessor writeBehindProcessor;
    private final long backupDelayMillis;
    private final long writeDelayMillis;
    private final int partitionCount;
    private long lastHighestStoreTime;
    private volatile boolean running;

    public StoreWorker(MapStoreContext mapStoreContext, WriteBehindProcessor writeBehindProcessor) {
        this.mapName = mapStoreContext.getMapName();
        this.mapServiceContext = mapStoreContext.getMapServiceContext();
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        this.partitionService = nodeEngine.getPartitionService();
        this.executionService = nodeEngine.getExecutionService();
        this.writeBehindProcessor = writeBehindProcessor;
        this.backupDelayMillis = this.getReplicaWaitTimeMillis();
        this.lastHighestStoreTime = Clock.currentTimeMillis();
        this.writeDelayMillis = TimeUnit.SECONDS.toMillis(StoreWorker.getWriteDelaySeconds(mapStoreContext));
        this.partitionCount = this.partitionService.getPartitionCount();
    }

    public synchronized void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.schedule();
    }

    public synchronized void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        try {
            this.runInternal();
        }
        finally {
            if (this.running) {
                this.schedule();
            }
        }
    }

    private void schedule() {
        this.executionService.schedule(this, 1L, TimeUnit.SECONDS);
    }

    private void runInternal() {
        long now = Clock.currentTimeMillis();
        long ownerHighestStoreTime = this.calculateHighestStoreTime(this.lastHighestStoreTime, now);
        long backupHighestStoreTime = ownerHighestStoreTime - this.backupDelayMillis;
        this.lastHighestStoreTime = ownerHighestStoreTime;
        List<DelayedEntry> ownersList = null;
        List<DelayedEntry> backupsList = null;
        for (int partitionId = 0; partitionId < this.partitionCount && !Thread.currentThread().isInterrupted(); ++partitionId) {
            RecordStore recordStore = this.getRecordStoreOrNull(this.mapName, partitionId);
            if (!this.hasEntryInWriteBehindQueue(recordStore)) continue;
            boolean localPartition = this.isPartitionLocal(partitionId);
            if (!localPartition) {
                backupsList = StoreWorker.initListIfNull(backupsList, this.partitionCount);
                this.selectEntriesToStore(recordStore, backupsList, backupHighestStoreTime);
                continue;
            }
            ownersList = StoreWorker.initListIfNull(ownersList, this.partitionCount);
            this.selectEntriesToStore(recordStore, ownersList, ownerHighestStoreTime);
        }
        if (!CollectionUtil.isEmpty(ownersList)) {
            Map<Integer, List<DelayedEntry>> failuresPerPartition = this.writeBehindProcessor.process(ownersList);
            this.removeFinishedStoreOperationsFromQueues(this.mapName, ownersList);
            this.reAddFailedStoreOperationsToQueues(this.mapName, failuresPerPartition);
        }
        if (!CollectionUtil.isEmpty(backupsList)) {
            this.doInBackup(backupsList);
        }
        this.notifyFlush();
    }

    private static List<DelayedEntry> initListIfNull(List<DelayedEntry> list, int capacity) {
        if (list == null) {
            list = new ArrayList<DelayedEntry>(capacity);
        }
        return list;
    }

    private long calculateHighestStoreTime(long lastHighestStoreTime, long now) {
        return now >= lastHighestStoreTime + this.writeDelayMillis ? now : lastHighestStoreTime;
    }

    private boolean hasEntryInWriteBehindQueue(RecordStore recordStore) {
        if (recordStore == null) {
            return false;
        }
        MapDataStore<Data, Object> mapDataStore = recordStore.getMapDataStore();
        WriteBehindStore dataStore = (WriteBehindStore)mapDataStore;
        WriteBehindQueue<DelayedEntry> writeBehindQueue = dataStore.getWriteBehindQueue();
        return writeBehindQueue.size() != 0;
    }

    private void notifyFlush() {
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            RecordStore recordStore = this.getRecordStoreOrNull(this.mapName, partitionId);
            if (recordStore == null) continue;
            WriteBehindStore mapDataStore = (WriteBehindStore)recordStore.getMapDataStore();
            mapDataStore.notifyFlush();
        }
    }

    private boolean isPartitionLocal(int partitionId) {
        IPartition partition = this.partitionService.getPartition(partitionId, false);
        return partition.isLocal();
    }

    private void selectEntriesToStore(RecordStore recordStore, List<DelayedEntry> entries, long highestStoreTime) {
        WriteBehindQueue<DelayedEntry> queue = this.getWriteBehindQueue(recordStore);
        long nextSequenceToFlush = this.getSequenceToFlush(recordStore);
        this.filterWriteBehindQueue(highestStoreTime, nextSequenceToFlush, entries, queue);
    }

    private void filterWriteBehindQueue(final long highestStoreTime, final long sequence, Collection<DelayedEntry> collection, WriteBehindQueue<DelayedEntry> queue) {
        if (sequence > 0L) {
            queue.filter(new IPredicate<DelayedEntry>(){

                @Override
                public boolean test(DelayedEntry delayedEntry) {
                    return delayedEntry.getSequence() <= sequence;
                }
            }, collection);
        } else {
            queue.filter(new IPredicate<DelayedEntry>(){

                @Override
                public boolean test(DelayedEntry delayedEntry) {
                    return delayedEntry.getStoreTime() <= highestStoreTime;
                }
            }, collection);
        }
    }

    private void removeFinishedStoreOperationsFromQueues(String mapName, List<DelayedEntry> entries) {
        for (DelayedEntry entry : entries) {
            RecordStore recordStore = this.getRecordStoreOrNull(mapName, entry.getPartitionId());
            if (recordStore == null) continue;
            this.getWriteBehindQueue(recordStore).removeFirstOccurrence(entry);
        }
    }

    private void reAddFailedStoreOperationsToQueues(String mapName, Map<Integer, List<DelayedEntry>> failuresPerPartition) {
        if (failuresPerPartition.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, List<DelayedEntry>> entry : failuresPerPartition.entrySet()) {
            RecordStore recordStore;
            Integer partitionId = entry.getKey();
            List<DelayedEntry> failures = failuresPerPartition.get(partitionId);
            if (CollectionUtil.isEmpty(failures) || (recordStore = this.getRecordStoreOrNull(mapName, partitionId)) == null) continue;
            WriteBehindQueue<DelayedEntry> queue = this.getWriteBehindQueue(recordStore);
            queue.addFirst(failures);
        }
    }

    private void doInBackup(List<DelayedEntry> delayedEntries) {
        this.writeBehindProcessor.callBeforeStoreListeners(delayedEntries);
        this.removeFinishedStoreOperationsFromQueues(this.mapName, delayedEntries);
        this.writeBehindProcessor.callAfterStoreListeners(delayedEntries);
    }

    private long getReplicaWaitTimeMillis() {
        HazelcastProperties hazelcastProperties = this.mapServiceContext.getNodeEngine().getProperties();
        return hazelcastProperties.getMillis(GroupProperty.MAP_REPLICA_SCHEDULED_TASK_DELAY_SECONDS);
    }

    private RecordStore getRecordStoreOrNull(String mapName, int partitionId) {
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(partitionId);
        return partitionContainer.getExistingRecordStore(mapName);
    }

    private WriteBehindQueue<DelayedEntry> getWriteBehindQueue(RecordStore recordStore) {
        WriteBehindStore writeBehindStore = (WriteBehindStore)recordStore.getMapDataStore();
        return writeBehindStore.getWriteBehindQueue();
    }

    private long getSequenceToFlush(RecordStore recordStore) {
        WriteBehindStore writeBehindStore = (WriteBehindStore)recordStore.getMapDataStore();
        return writeBehindStore.getSequenceToFlush();
    }

    private static int getWriteDelaySeconds(MapStoreContext mapStoreContext) {
        MapStoreConfig mapStoreConfig = mapStoreContext.getMapStoreConfig();
        return mapStoreConfig.getWriteDelaySeconds();
    }

    public String toString() {
        return "StoreWorker{mapName='" + this.mapName + "'}";
    }
}

