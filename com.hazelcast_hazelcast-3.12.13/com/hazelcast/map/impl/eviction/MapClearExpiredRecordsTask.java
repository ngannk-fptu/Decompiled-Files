/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.eviction;

import com.hazelcast.internal.eviction.ClearExpiredRecordsTask;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.operation.EvictBatchBackupOperation;
import com.hazelcast.map.impl.operation.MapClearExpiredOperation;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class MapClearExpiredRecordsTask
extends ClearExpiredRecordsTask<PartitionContainer, RecordStore> {
    public static final String PROP_PRIMARY_DRIVES_BACKUP = "hazelcast.internal.map.expiration.primary.drives_backup";
    public static final String PROP_CLEANUP_PERCENTAGE = "hazelcast.internal.map.expiration.cleanup.percentage";
    public static final String PROP_CLEANUP_OPERATION_COUNT = "hazelcast.internal.map.expiration.cleanup.operation.count";
    public static final String PROP_TASK_PERIOD_SECONDS = "hazelcast.internal.map.expiration.task.period.seconds";
    private static final boolean DEFAULT_PRIMARY_DRIVES_BACKUP = true;
    private static final int DEFAULT_TASK_PERIOD_SECONDS = 5;
    private static final int DEFAULT_CLEANUP_PERCENTAGE = 10;
    private static final HazelcastProperty PRIMARY_DRIVES_BACKUP = new HazelcastProperty("hazelcast.internal.map.expiration.primary.drives_backup", true);
    private static final HazelcastProperty TASK_PERIOD_SECONDS = new HazelcastProperty("hazelcast.internal.map.expiration.task.period.seconds", 5, TimeUnit.SECONDS);
    private static final HazelcastProperty CLEANUP_PERCENTAGE = new HazelcastProperty("hazelcast.internal.map.expiration.cleanup.percentage", 10);
    private static final HazelcastProperty CLEANUP_OPERATION_COUNT = new HazelcastProperty("hazelcast.internal.map.expiration.cleanup.operation.count");
    private final boolean primaryDrivesEviction;
    private final Comparator<PartitionContainer> partitionContainerComparator = new Comparator<PartitionContainer>(){

        @Override
        public int compare(PartitionContainer o1, PartitionContainer o2) {
            long s2;
            long s1 = o1.getLastCleanupTimeCopy();
            return s1 < (s2 = o2.getLastCleanupTimeCopy()) ? -1 : (s1 == s2 ? 0 : 1);
        }
    };

    public MapClearExpiredRecordsTask(PartitionContainer[] containers, NodeEngine nodeEngine) {
        super("hz:impl:mapService", containers, CLEANUP_OPERATION_COUNT, CLEANUP_PERCENTAGE, TASK_PERIOD_SECONDS, nodeEngine);
        this.primaryDrivesEviction = nodeEngine.getProperties().getBoolean(PRIMARY_DRIVES_BACKUP);
    }

    public boolean canPrimaryDriveExpiration() {
        return this.primaryDrivesEviction;
    }

    @Override
    public void tryToSendBackupExpiryOp(RecordStore store, boolean sendIfAtBatchSize) {
        if (!this.canPrimaryDriveExpiration()) {
            return;
        }
        InvalidationQueue<ExpiredKey> expiredKeys = store.getExpiredKeysQueue();
        int totalBackupCount = store.getMapContainer().getTotalBackupCount();
        int partitionId = store.getPartitionId();
        this.toBackupSender.trySendExpiryOp(store, expiredKeys, totalBackupCount, partitionId, sendIfAtBatchSize);
    }

    @Override
    public Iterator<RecordStore> storeIterator(PartitionContainer container) {
        return container.getMaps().values().iterator();
    }

    @Override
    protected Operation newPrimaryExpiryOp(int expirationPercentage, PartitionContainer container) {
        int partitionId = container.getPartitionId();
        return new MapClearExpiredOperation(expirationPercentage).setNodeEngine(this.nodeEngine).setCallerUuid(this.nodeEngine.getLocalMember().getUuid()).setPartitionId(partitionId).setValidateTarget(false).setServiceName("hz:impl:mapService");
    }

    @Override
    protected Operation newBackupExpiryOp(RecordStore store, Collection<ExpiredKey> expiredKeys) {
        return new EvictBatchBackupOperation(store.getName(), expiredKeys, store.size());
    }

    @Override
    protected void sortPartitionContainers(List<PartitionContainer> partitionContainers) {
        for (PartitionContainer partitionContainer : partitionContainers) {
            partitionContainer.setLastCleanupTimeCopy(partitionContainer.getLastCleanupTime());
        }
        Collections.sort(partitionContainers, this.partitionContainerComparator);
    }

    @Override
    protected ClearExpiredRecordsTask.ProcessablePartitionType getProcessablePartitionType() {
        return ClearExpiredRecordsTask.ProcessablePartitionType.PRIMARY_OR_BACKUP_PARTITION;
    }

    @Override
    protected void equalizeBackupSizeWithPrimary(PartitionContainer container) {
        if (!this.canPrimaryDriveExpiration()) {
            return;
        }
        ConcurrentMap<String, RecordStore> maps = container.getMaps();
        for (RecordStore recordStore : maps.values()) {
            int totalBackupCount = recordStore.getMapContainer().getTotalBackupCount();
            this.toBackupSender.invokeBackupExpiryOperation(Collections.emptyList(), totalBackupCount, recordStore.getPartitionId(), recordStore);
        }
    }

    @Override
    protected boolean hasExpiredKeyToSendBackup(PartitionContainer container) {
        long size = 0L;
        ConcurrentMap<String, RecordStore> maps = container.getMaps();
        for (RecordStore store : maps.values()) {
            if ((size += (long)store.getExpiredKeysQueue().size()) <= 0L) continue;
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasRunningCleanup(PartitionContainer container) {
        return container.hasRunningCleanup();
    }

    @Override
    protected void setHasRunningCleanup(PartitionContainer container) {
        container.setHasRunningCleanup(true);
    }

    @Override
    protected boolean notHaveAnyExpirableRecord(PartitionContainer partitionContainer) {
        boolean notExist = true;
        ConcurrentMap<String, RecordStore> maps = partitionContainer.getMaps();
        for (RecordStore store : maps.values()) {
            if (!store.isExpirable()) continue;
            notExist = false;
            break;
        }
        return notExist;
    }

    @Override
    protected boolean isContainerEmpty(PartitionContainer container) {
        long size = 0L;
        ConcurrentMap<String, RecordStore> maps = container.getMaps();
        for (RecordStore store : maps.values()) {
            if ((size += (long)store.size()) <= 0L) continue;
            return false;
        }
        return true;
    }

    @Override
    protected long getLastCleanupTime(PartitionContainer container) {
        return container.getLastCleanupTime();
    }

    public String toString() {
        return MapClearExpiredRecordsTask.class.getName();
    }
}

