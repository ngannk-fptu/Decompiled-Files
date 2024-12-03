/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.CheckPartitionReplicaVersionTask;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.MigrationManager;
import com.hazelcast.internal.partition.impl.PartitionServiceState;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.HasOngoingMigration;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.Clock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class PartitionReplicaStateChecker {
    private static final int DEFAULT_PAUSE_MILLIS = 1000;
    private static final int REPLICA_SYNC_CHECK_TIMEOUT_SECONDS = 10;
    private static final int INVOCATION_TRY_COUNT = 10;
    private static final int INVOCATION_TRY_PAUSE_MILLIS = 100;
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final InternalPartitionServiceImpl partitionService;
    private final ILogger logger;
    private final PartitionStateManager partitionStateManager;
    private final MigrationManager migrationManager;

    PartitionReplicaStateChecker(Node node, InternalPartitionServiceImpl partitionService) {
        this.node = node;
        this.nodeEngine = node.getNodeEngine();
        this.partitionService = partitionService;
        this.logger = node.getLogger(this.getClass());
        this.partitionStateManager = partitionService.getPartitionStateManager();
        this.migrationManager = partitionService.getMigrationManager();
    }

    public PartitionServiceState getPartitionServiceState() {
        if (this.partitionService.isFetchMostRecentPartitionTableTaskRequired()) {
            return PartitionServiceState.FETCHING_PARTITION_TABLE;
        }
        if (this.hasMissingReplicaOwners()) {
            return PartitionServiceState.REPLICA_NOT_OWNED;
        }
        if (this.migrationManager.hasOnGoingMigration()) {
            return PartitionServiceState.MIGRATION_LOCAL;
        }
        if (!this.partitionService.isLocalMemberMaster() && this.hasOnGoingMigrationMaster(Level.OFF)) {
            return PartitionServiceState.MIGRATION_ON_MASTER;
        }
        if (!this.checkAndTriggerReplicaSync()) {
            return PartitionServiceState.REPLICA_NOT_SYNC;
        }
        return PartitionServiceState.SAFE;
    }

    public boolean triggerAndWaitForReplicaSync(long timeout, TimeUnit unit) {
        return this.triggerAndWaitForReplicaSync(timeout, unit, 1000L);
    }

    boolean triggerAndWaitForReplicaSync(long timeout, TimeUnit unit, long sleepMillis) {
        long timeoutInMillis = unit.toMillis(timeout);
        while (timeoutInMillis > 0L && (timeoutInMillis = this.waitForMissingReplicaOwners(Level.FINE, timeoutInMillis, sleepMillis)) > 0L && (timeoutInMillis = this.waitForOngoingMigrations(Level.FINE, timeoutInMillis, sleepMillis)) > 0L) {
            long start = Clock.currentTimeMillis();
            boolean syncResult = this.checkAndTriggerReplicaSync();
            timeoutInMillis -= Clock.currentTimeMillis() - start;
            if (syncResult) {
                this.logger.finest("Replica sync state is OK");
                return true;
            }
            if (timeoutInMillis <= 0L) break;
            this.logger.info("Some backup replicas are inconsistent with primary, waiting for synchronization. Timeout: " + timeoutInMillis + "ms");
            timeoutInMillis = this.sleepWithBusyWait(timeoutInMillis, sleepMillis);
        }
        return false;
    }

    private long waitForMissingReplicaOwners(Level level, long timeoutInMillis, long sleep) {
        long timeout = timeoutInMillis;
        while (timeout > 0L && this.hasMissingReplicaOwners()) {
            if (this.logger.isLoggable(level)) {
                this.logger.log(level, "Waiting for ownership assignments of missing replica owners...");
            }
            timeout = this.sleepWithBusyWait(timeout, sleep);
        }
        return timeout;
    }

    private boolean hasMissingReplicaOwners() {
        if (!this.needsReplicaStateCheck()) {
            return false;
        }
        int memberGroupsSize = this.partitionStateManager.getMemberGroupsSize();
        int replicaCount = Math.min(7, memberGroupsSize);
        ClusterServiceImpl clusterService = this.node.getClusterService();
        ClusterState clusterState = clusterService.getClusterState();
        for (InternalPartition partition : this.partitionStateManager.getPartitions()) {
            for (int index = 0; index < replicaCount; ++index) {
                PartitionReplica replica = partition.getReplica(index);
                if (replica == null) {
                    if (this.logger.isFinestEnabled()) {
                        this.logger.finest("Missing replica=" + index + " for partitionId=" + partition.getPartitionId());
                    }
                    return true;
                }
                if (clusterService.getMember(replica.address(), replica.uuid()) != null || !clusterState.isJoinAllowed() && clusterService.isMissingMember(replica.address(), replica.uuid())) continue;
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Unknown replica owner= " + replica + ", partitionId=" + partition.getPartitionId() + ", replica=" + index);
                }
                return true;
            }
        }
        return false;
    }

    private long waitForOngoingMigrations(Level level, long timeoutInMillis, long sleep) {
        long timeout = timeoutInMillis;
        while (timeout > 0L && (this.migrationManager.hasOnGoingMigration() || this.hasOnGoingMigrationMaster(level))) {
            if (this.logger.isLoggable(level)) {
                this.logger.log(level, "Waiting for the master node to complete remaining migrations...");
            }
            timeout = this.sleepWithBusyWait(timeout, sleep);
        }
        return timeout;
    }

    private long sleepWithBusyWait(long timeoutInMillis, long sleep) {
        try {
            Thread.sleep(sleep);
        }
        catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            this.logger.finest("Busy wait interrupted", ie);
        }
        return timeoutInMillis - sleep;
    }

    private boolean checkAndTriggerReplicaSync() {
        if (!this.needsReplicaStateCheck()) {
            return true;
        }
        Semaphore semaphore = new Semaphore(0);
        AtomicBoolean ok = new AtomicBoolean(true);
        int maxBackupCount = this.partitionService.getMaxAllowedBackupCount();
        int ownedPartitionCount = this.invokeReplicaSyncOperations(maxBackupCount, semaphore, ok);
        try {
            if (!ok.get()) {
                return false;
            }
            int permits = ownedPartitionCount * maxBackupCount;
            boolean receivedAllResponses = semaphore.tryAcquire(permits, 10L, TimeUnit.SECONDS);
            return receivedAllResponses && ok.get();
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private int invokeReplicaSyncOperations(int maxBackupCount, Semaphore semaphore, AtomicBoolean result) {
        MemberImpl localMember = this.node.getLocalMember();
        ReplicaSyncResponseCallback callback = new ReplicaSyncResponseCallback(result, semaphore);
        ClusterServiceImpl clusterService = this.node.getClusterService();
        ClusterState clusterState = clusterService.getClusterState();
        int ownedCount = 0;
        for (InternalPartition partition : this.partitionStateManager.getPartitions()) {
            PartitionReplica owner = partition.getOwnerReplicaOrNull();
            if (owner == null) {
                result.set(false);
                continue;
            }
            if (!owner.isIdentical(localMember)) continue;
            ++ownedCount;
            if (maxBackupCount == 0) {
                if (!partition.isMigrating()) continue;
                result.set(false);
                continue;
            }
            for (int index = 1; index <= maxBackupCount; ++index) {
                PartitionReplica replicaOwner = partition.getReplica(index);
                if (replicaOwner == null) {
                    result.set(false);
                    semaphore.release();
                    continue;
                }
                if (!clusterState.isJoinAllowed() && clusterService.isMissingMember(replicaOwner.address(), replicaOwner.uuid())) {
                    semaphore.release();
                    continue;
                }
                int partitionId = partition.getPartitionId();
                CheckPartitionReplicaVersionTask task = new CheckPartitionReplicaVersionTask(this.nodeEngine, partitionId, index, callback);
                this.nodeEngine.getOperationService().execute(task);
            }
        }
        return ownedCount;
    }

    private boolean needsReplicaStateCheck() {
        return this.partitionStateManager.isInitialized() && this.partitionStateManager.getMemberGroupsSize() > 0;
    }

    boolean hasOnGoingMigrationMaster(Level level) {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        Address masterAddress = clusterService.getMasterAddress();
        if (masterAddress == null) {
            return clusterService.isJoined();
        }
        HasOngoingMigration operation = new HasOngoingMigration();
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        InternalCompletableFuture future = operationService.createInvocationBuilder("hz:core:partitionService", (Operation)operation, masterAddress).setTryCount(10).setTryPauseMillis(100L).invoke();
        try {
            return (Boolean)future.join();
        }
        catch (Exception e) {
            this.logger.log(level, "Could not get a response from master about migrations! -> " + e.toString());
            return false;
        }
    }

    private static class ReplicaSyncResponseCallback
    implements ExecutionCallback<Object> {
        private final AtomicBoolean result;
        private final Semaphore semaphore;

        ReplicaSyncResponseCallback(AtomicBoolean result, Semaphore semaphore) {
            this.result = result;
            this.semaphore = semaphore;
        }

        @Override
        public void onResponse(Object response) {
            if (Boolean.FALSE.equals(response)) {
                this.result.set(false);
            }
            this.semaphore.release();
        }

        @Override
        public void onFailure(Throwable t) {
            this.result.set(false);
        }
    }
}

