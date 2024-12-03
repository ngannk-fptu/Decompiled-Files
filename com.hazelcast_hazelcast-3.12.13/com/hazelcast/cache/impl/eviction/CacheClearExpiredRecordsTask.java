/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.eviction;

import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.operation.CacheClearExpiredOperation;
import com.hazelcast.cache.impl.operation.CacheExpireBatchBackupOperation;
import com.hazelcast.core.IBiFunction;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.eviction.ClearExpiredRecordsTask;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheClearExpiredRecordsTask
extends ClearExpiredRecordsTask<CachePartitionSegment, ICacheRecordStore> {
    public static final String PROP_CLEANUP_PERCENTAGE = "hazelcast.internal.cache.expiration.cleanup.percentage";
    public static final String PROP_TASK_PERIOD_SECONDS = "hazelcast.internal.cache.expiration.task.period.seconds";
    public static final String PROP_CLEANUP_OPERATION_COUNT = "hazelcast.internal.cache.expiration.cleanup.operation.count";
    private static final int DEFAULT_TASK_PERIOD_SECONDS = 5;
    private static final int DEFAULT_CLEANUP_PERCENTAGE = 10;
    private static final HazelcastProperty TASK_PERIOD_SECONDS = new HazelcastProperty("hazelcast.internal.cache.expiration.task.period.seconds", 5, TimeUnit.SECONDS);
    private static final HazelcastProperty CLEANUP_PERCENTAGE = new HazelcastProperty("hazelcast.internal.cache.expiration.cleanup.percentage", 10);
    private static final HazelcastProperty CLEANUP_OPERATION_COUNT = new HazelcastProperty("hazelcast.internal.cache.expiration.cleanup.operation.count");
    private final Comparator<CachePartitionSegment> partitionSegmentComparator = new Comparator<CachePartitionSegment>(){

        @Override
        public int compare(CachePartitionSegment o1, CachePartitionSegment o2) {
            long s2;
            long s1 = o1.getLastCleanupTimeBeforeSorting();
            return s1 < (s2 = o2.getLastCleanupTimeBeforeSorting()) ? -1 : (s1 == s2 ? 0 : 1);
        }
    };

    public CacheClearExpiredRecordsTask(CachePartitionSegment[] containers, NodeEngine nodeEngine) {
        super("hz:impl:cacheService", containers, CLEANUP_OPERATION_COUNT, CLEANUP_PERCENTAGE, TASK_PERIOD_SECONDS, nodeEngine);
    }

    @Override
    public void tryToSendBackupExpiryOp(ICacheRecordStore store, boolean sendIfAtBatchSize) {
        InvalidationQueue<ExpiredKey> expiredKeys = store.getExpiredKeysQueue();
        int totalBackupCount = store.getConfig().getTotalBackupCount();
        int partitionId = store.getPartitionId();
        this.toBackupSender.trySendExpiryOp(store, expiredKeys, totalBackupCount, partitionId, sendIfAtBatchSize);
    }

    @Override
    public Iterator<ICacheRecordStore> storeIterator(CachePartitionSegment container) {
        return container.recordStoreIterator();
    }

    @Override
    protected Operation newPrimaryExpiryOp(int expirationPercentage, CachePartitionSegment container) {
        return new CacheClearExpiredOperation(expirationPercentage).setNodeEngine(this.nodeEngine).setCallerUuid(this.nodeEngine.getLocalMember().getUuid()).setPartitionId(container.getPartitionId()).setValidateTarget(false).setServiceName("hz:impl:cacheService");
    }

    @Override
    protected Operation newBackupExpiryOp(ICacheRecordStore store, Collection<ExpiredKey> expiredKeys) {
        return new CacheExpireBatchBackupOperation(store.getName(), expiredKeys, store.size());
    }

    @Override
    protected IBiFunction<Integer, Integer, Boolean> newBackupExpiryOpFilter() {
        return new IBiFunction<Integer, Integer, Boolean>(){

            @Override
            public Boolean apply(Integer partitionId, Integer replicaIndex) {
                IBiFunction filter = CacheClearExpiredRecordsTask.super.newBackupExpiryOpFilter();
                if (!((Boolean)filter.apply(partitionId, replicaIndex)).booleanValue()) {
                    return false;
                }
                IPartition partition = CacheClearExpiredRecordsTask.this.partitionService.getPartition(partitionId);
                Address replicaAddress = partition.getReplicaAddress(replicaIndex);
                MemberImpl member = CacheClearExpiredRecordsTask.this.nodeEngine.getClusterService().getMember(replicaAddress);
                if (member == null) {
                    return false;
                }
                return member.getVersion().asVersion().isGreaterOrEqual(Versions.V3_11);
            }
        };
    }

    @Override
    protected void equalizeBackupSizeWithPrimary(CachePartitionSegment container) {
        Iterator<ICacheRecordStore> iterator = container.recordStoreIterator();
        while (iterator.hasNext()) {
            ICacheRecordStore recordStore = iterator.next();
            int totalBackupCount = recordStore.getConfig().getTotalBackupCount();
            int partitionId = recordStore.getPartitionId();
            this.toBackupSender.invokeBackupExpiryOperation(Collections.emptyList(), totalBackupCount, partitionId, recordStore);
        }
    }

    @Override
    protected boolean hasExpiredKeyToSendBackup(CachePartitionSegment container) {
        Iterator<ICacheRecordStore> iterator = container.recordStoreIterator();
        while (iterator.hasNext()) {
            ICacheRecordStore store = iterator.next();
            if (store.getExpiredKeysQueue().size() <= 0) continue;
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasRunningCleanup(CachePartitionSegment container) {
        return container.hasRunningCleanupOperation();
    }

    @Override
    protected void setHasRunningCleanup(CachePartitionSegment container) {
        container.setRunningCleanupOperation(true);
    }

    @Override
    protected boolean isContainerEmpty(CachePartitionSegment container) {
        Iterator<ICacheRecordStore> iterator = container.recordStoreIterator();
        while (iterator.hasNext()) {
            ICacheRecordStore store = iterator.next();
            if (store.size() <= 0) continue;
            return false;
        }
        return true;
    }

    @Override
    protected boolean notHaveAnyExpirableRecord(CachePartitionSegment container) {
        Iterator<ICacheRecordStore> iterator = container.recordStoreIterator();
        while (iterator.hasNext()) {
            ICacheRecordStore store = iterator.next();
            if (!store.isExpirable()) continue;
            return false;
        }
        return true;
    }

    @Override
    protected long getLastCleanupTime(CachePartitionSegment container) {
        return container.getLastCleanupTime();
    }

    @Override
    protected void sortPartitionContainers(List<CachePartitionSegment> containers) {
        for (CachePartitionSegment segment : containers) {
            segment.storeLastCleanupTime();
        }
        Collections.sort(containers, this.partitionSegmentComparator);
    }

    @Override
    protected ClearExpiredRecordsTask.ProcessablePartitionType getProcessablePartitionType() {
        return ClearExpiredRecordsTask.ProcessablePartitionType.PRIMARY_PARTITION;
    }

    public String toString() {
        return CacheClearExpiredRecordsTask.class.getName();
    }
}

