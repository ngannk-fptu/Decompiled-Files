/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.core.IBiFunction;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.eviction.ToBackupSender;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.Clock;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressFBWarnings(value={"URF_UNREAD_FIELD"})
public abstract class ClearExpiredRecordsTask<T, S>
implements Runnable {
    private static final int DIFFERENCE_BETWEEN_TWO_SUBSEQUENT_PARTITION_CLEANUP_MILLIS = 1000;
    protected final T[] containers;
    protected final NodeEngine nodeEngine;
    protected final ToBackupSender<S> toBackupSender;
    protected final IPartitionService partitionService;
    private final int partitionCount;
    private final int taskPeriodSeconds;
    private final int cleanupPercentage;
    private final int cleanupOperationCount;
    private final Address thisAddress;
    private final InternalOperationService operationService;
    private final AtomicBoolean singleRunPermit = new AtomicBoolean(false);
    private final AtomicInteger lostPartitionCounter = new AtomicInteger();
    private final AtomicInteger nextExpiryQueueToScanIndex = new AtomicInteger();
    private volatile int lastKnownLostPartitionCount;
    private int runningCleanupOperationsCount;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    protected ClearExpiredRecordsTask(String serviceName, T[] containers, HazelcastProperty cleanupOpProperty, HazelcastProperty cleanupPercentageProperty, HazelcastProperty taskPeriodProperty, NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.containers = containers;
        this.operationService = (InternalOperationService)nodeEngine.getOperationService();
        this.partitionService = nodeEngine.getPartitionService();
        this.partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.thisAddress = nodeEngine.getThisAddress();
        HazelcastProperties properties = nodeEngine.getProperties();
        this.cleanupOperationCount = ClearExpiredRecordsTask.calculateCleanupOperationCount(properties, cleanupOpProperty, this.partitionCount, this.operationService.getPartitionThreadCount());
        Preconditions.checkPositive(this.cleanupOperationCount, "cleanupOperationCount should be a positive number");
        this.cleanupPercentage = properties.getInteger(cleanupPercentageProperty);
        Preconditions.checkTrue(this.cleanupPercentage > 0 && this.cleanupPercentage <= 100, "cleanupPercentage should be in range (0,100]");
        this.taskPeriodSeconds = properties.getSeconds(taskPeriodProperty);
        this.toBackupSender = ToBackupSender.newToBackupSender(serviceName, this.newBackupExpiryOpSupplier(), this.newBackupExpiryOpFilter(), nodeEngine);
    }

    protected IBiFunction<Integer, Integer, Boolean> newBackupExpiryOpFilter() {
        return new IBiFunction<Integer, Integer, Boolean>(){

            @Override
            public Boolean apply(Integer partitionId, Integer replicaIndex) {
                IPartition partition = ClearExpiredRecordsTask.this.partitionService.getPartition(partitionId);
                return partition.getReplicaAddress(replicaIndex) != null;
            }
        };
    }

    @Override
    public void run() {
        try {
            if (!this.singleRunPermit.compareAndSet(false, true)) {
                return;
            }
            this.runInternal();
        }
        finally {
            this.singleRunPermit.set(false);
        }
    }

    private void runInternal() {
        this.runningCleanupOperationsCount = 0;
        long nowInMillis = ClearExpiredRecordsTask.nowInMillis();
        boolean lostPartitionDetected = this.lostPartitionDetected();
        List<T> containersToProcess = null;
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            T container = this.containers[partitionId];
            IPartition partition = this.partitionService.getPartition(partitionId, false);
            if (partition.isMigrating()) continue;
            if (partition.isLocal() && lostPartitionDetected) {
                this.equalizeBackupSizeWithPrimary(container);
            }
            if (!this.canProcessContainer(container, partition, nowInMillis)) continue;
            containersToProcess = this.addContainerTo(containersToProcess, container);
        }
        if (!CollectionUtil.isEmpty(containersToProcess)) {
            this.sortPartitionContainers(containersToProcess);
            this.sendCleanupOperations(containersToProcess);
        }
        this.sendExpiryQueuesToBackupIncrementally();
    }

    private void sendExpiryQueuesToBackupIncrementally() {
        int scanned = 0;
        for (int partitionId = this.nextExpiryQueueToScanIndex.get(); partitionId < this.partitionCount; ++partitionId) {
            this.sendQueuedExpiredKeys(this.containers[partitionId]);
            this.nextExpiryQueueToScanIndex.incrementAndGet();
            if (++scanned % this.cleanupOperationCount == 0) break;
        }
        if (this.nextExpiryQueueToScanIndex.get() == this.partitionCount) {
            this.nextExpiryQueueToScanIndex.set(0);
        }
    }

    private boolean canProcessContainer(T container, IPartition partition, long nowInMillis) {
        if (!this.getProcessablePartitionType().isProcessable(partition, this.thisAddress)) {
            return false;
        }
        if (this.isContainerEmpty(container) && !this.hasExpiredKeyToSendBackup(container)) {
            return false;
        }
        if (this.hasRunningCleanup(container)) {
            ++this.runningCleanupOperationsCount;
            return false;
        }
        return this.runningCleanupOperationsCount <= this.cleanupOperationCount && !this.notInProcessableTimeWindow(container, nowInMillis) && !this.notHaveAnyExpirableRecord(container);
    }

    public final void partitionLost(PartitionLostEvent ignored) {
        this.lostPartitionCounter.incrementAndGet();
    }

    private static long nowInMillis() {
        return Clock.currentTimeMillis();
    }

    private boolean lostPartitionDetected() {
        int currentLostPartitionCount = this.lostPartitionCounter.get();
        if (currentLostPartitionCount == this.lastKnownLostPartitionCount) {
            return false;
        }
        this.lastKnownLostPartitionCount = currentLostPartitionCount;
        return true;
    }

    private static int calculateCleanupOperationCount(HazelcastProperties properties, HazelcastProperty cleanupOpCountProperty, int partitionCount, int partitionThreadCount) {
        String stringValue = properties.getString(cleanupOpCountProperty);
        if (stringValue != null) {
            return Integer.parseInt(stringValue);
        }
        double scanPercentage = 0.1;
        int opCountFromPartitionCount = (int)((double)partitionCount * 0.1);
        int inflationFactor = 3;
        int opCountFromThreadCount = partitionThreadCount * 3;
        if (opCountFromPartitionCount == 0) {
            return opCountFromThreadCount;
        }
        return Math.min(opCountFromPartitionCount, opCountFromThreadCount);
    }

    private boolean notInProcessableTimeWindow(T container, long now) {
        return now - this.getLastCleanupTime(container) < 1000L;
    }

    private List<T> addContainerTo(List<T> containersToProcess, T container) {
        if (containersToProcess == null) {
            containersToProcess = new ArrayList<T>();
        }
        containersToProcess.add(container);
        return containersToProcess;
    }

    private void sendCleanupOperations(List<T> partitionContainers) {
        boolean start = false;
        int end = this.cleanupOperationCount;
        if (end > partitionContainers.size()) {
            end = partitionContainers.size();
        }
        List<T> partitionIds = partitionContainers.subList(0, end);
        for (T container : partitionIds) {
            this.setHasRunningCleanup(container);
            Operation operation = this.newPrimaryExpiryOp(this.cleanupPercentage, container);
            this.operationService.execute(operation);
        }
    }

    private IBiFunction<S, Collection<ExpiredKey>, Operation> newBackupExpiryOpSupplier() {
        return new IBiFunction<S, Collection<ExpiredKey>, Operation>(){

            @Override
            public Operation apply(S recordStore, Collection<ExpiredKey> expiredKeys) {
                return ClearExpiredRecordsTask.this.newBackupExpiryOp(recordStore, expiredKeys);
            }
        };
    }

    public final void sendQueuedExpiredKeys(T container) {
        Iterator<S> storeIterator = this.storeIterator(container);
        while (storeIterator.hasNext()) {
            this.tryToSendBackupExpiryOp(storeIterator.next(), false);
        }
    }

    int getCleanupPercentage() {
        return this.cleanupPercentage;
    }

    int getTaskPeriodSeconds() {
        return this.taskPeriodSeconds;
    }

    int getCleanupOperationCount() {
        return this.cleanupOperationCount;
    }

    protected abstract boolean isContainerEmpty(T var1);

    protected abstract boolean hasRunningCleanup(T var1);

    protected abstract long getLastCleanupTime(T var1);

    protected abstract void equalizeBackupSizeWithPrimary(T var1);

    protected abstract boolean hasExpiredKeyToSendBackup(T var1);

    protected abstract boolean notHaveAnyExpirableRecord(T var1);

    protected abstract void sortPartitionContainers(List<T> var1);

    protected abstract void setHasRunningCleanup(T var1);

    protected abstract ProcessablePartitionType getProcessablePartitionType();

    protected abstract Operation newPrimaryExpiryOp(int var1, T var2);

    protected abstract Operation newBackupExpiryOp(S var1, Collection<ExpiredKey> var2);

    public abstract void tryToSendBackupExpiryOp(S var1, boolean var2);

    public abstract Iterator<S> storeIterator(T var1);

    protected static enum ProcessablePartitionType {
        PRIMARY_PARTITION{

            @Override
            boolean isProcessable(IPartition partition, Address address) {
                return partition.isLocal();
            }
        }
        ,
        PRIMARY_OR_BACKUP_PARTITION{

            @Override
            boolean isProcessable(IPartition partition, Address address) {
                return partition.isOwnerOrBackup(address);
            }
        };


        abstract boolean isProcessable(IPartition var1, Address var2);
    }
}

