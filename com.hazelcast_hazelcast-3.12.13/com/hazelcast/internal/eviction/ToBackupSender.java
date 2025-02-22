/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.core.IBiFunction;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.CollectionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

public final class ToBackupSender<RS> {
    private static final int TARGET_BATCH_SIZE = 100;
    private final String serviceName;
    private final OperationService operationService;
    private final IBiFunction<Integer, Integer, Boolean> backupOpFilter;
    private final IBiFunction<RS, Collection<ExpiredKey>, Operation> backupOpSupplier;

    private ToBackupSender(String serviceName, IBiFunction<RS, Collection<ExpiredKey>, Operation> backupOpSupplier, IBiFunction<Integer, Integer, Boolean> backupOpFilter, NodeEngine nodeEngine) {
        this.serviceName = serviceName;
        this.backupOpFilter = backupOpFilter;
        this.backupOpSupplier = backupOpSupplier;
        this.operationService = nodeEngine.getOperationService();
    }

    static <S> ToBackupSender<S> newToBackupSender(String serviceName, IBiFunction<S, Collection<ExpiredKey>, Operation> operationSupplier, IBiFunction<Integer, Integer, Boolean> backupOpFilter, NodeEngine nodeEngine) {
        return new ToBackupSender<S>(serviceName, operationSupplier, backupOpFilter, nodeEngine);
    }

    private static Collection<ExpiredKey> pollExpiredKeys(Queue<ExpiredKey> expiredKeys) {
        ExpiredKey expiredKey;
        ArrayList<ExpiredKey> polledKeys = new ArrayList<ExpiredKey>(expiredKeys.size());
        while ((expiredKey = expiredKeys.poll()) != null) {
            polledKeys.add(expiredKey);
        }
        return polledKeys;
    }

    public void trySendExpiryOp(RS recordStore, InvalidationQueue expiredKeyQueue, int backupReplicaCount, int partitionId, boolean sendIfAtBatchSize) {
        if (sendIfAtBatchSize && expiredKeyQueue.size() < 100) {
            return;
        }
        Collection<ExpiredKey> expiredKeys = ToBackupSender.pollExpiredKeys(expiredKeyQueue);
        if (CollectionUtil.isEmpty(expiredKeys)) {
            return;
        }
        this.invokeBackupExpiryOperation(expiredKeys, backupReplicaCount, partitionId, recordStore);
    }

    public void invokeBackupExpiryOperation(Collection<ExpiredKey> expiredKeys, int backupReplicaCount, int partitionId, RS recordStore) {
        for (int replicaIndex = 1; replicaIndex < backupReplicaCount + 1; ++replicaIndex) {
            if (!this.backupOpFilter.apply(partitionId, replicaIndex).booleanValue()) continue;
            Operation operation = this.backupOpSupplier.apply(recordStore, expiredKeys);
            this.operationService.createInvocationBuilder(this.serviceName, operation, partitionId).setReplicaIndex(replicaIndex).invoke();
        }
    }
}

