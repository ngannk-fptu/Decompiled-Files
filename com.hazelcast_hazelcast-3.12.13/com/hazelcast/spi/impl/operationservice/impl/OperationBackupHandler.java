/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.spi.impl.operationservice.impl.BackpressureRegulator;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.operationservice.impl.OutboundOperationHandler;
import com.hazelcast.spi.impl.operationservice.impl.operations.Backup;

final class OperationBackupHandler {
    private static final boolean ASSERTION_ENABLED = OperationBackupHandler.class.desiredAssertionStatus();
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final BackpressureRegulator backpressureRegulator;
    private final OutboundOperationHandler outboundOperationHandler;
    private final ILogger logger;

    OperationBackupHandler(OperationServiceImpl operationService, OutboundOperationHandler outboundOperationHandler) {
        this.outboundOperationHandler = outboundOperationHandler;
        this.node = operationService.node;
        this.nodeEngine = operationService.nodeEngine;
        this.backpressureRegulator = operationService.backpressureRegulator;
        this.logger = this.node.getLogger(this.getClass());
    }

    int sendBackups(Operation op) throws Exception {
        if (!(op instanceof BackupAwareOperation)) {
            return 0;
        }
        int backupAcks = 0;
        BackupAwareOperation backupAwareOp = (BackupAwareOperation)((Object)op);
        if (backupAwareOp.shouldBackup()) {
            backupAcks = this.sendBackups0(backupAwareOp);
        }
        return backupAcks;
    }

    int sendBackups0(BackupAwareOperation backupAwareOp) throws Exception {
        int requestedSyncBackups = this.requestedSyncBackups(backupAwareOp);
        int requestedAsyncBackups = this.requestedAsyncBackups(backupAwareOp);
        int requestedTotalBackups = this.requestedTotalBackups(backupAwareOp);
        if (requestedTotalBackups == 0) {
            return 0;
        }
        Operation op = (Operation)((Object)backupAwareOp);
        PartitionReplicaVersionManager versionManager = this.node.getPartitionService().getPartitionReplicaVersionManager();
        ServiceNamespace namespace = versionManager.getServiceNamespace(op);
        long[] replicaVersions = versionManager.incrementPartitionReplicaVersions(op.getPartitionId(), namespace, requestedTotalBackups);
        boolean syncForced = this.backpressureRegulator.isSyncForced(backupAwareOp);
        int syncBackups = this.syncBackups(requestedSyncBackups, requestedAsyncBackups, syncForced);
        int asyncBackups = this.asyncBackups(requestedSyncBackups, requestedAsyncBackups, syncForced);
        if (!op.returnsResponse()) {
            asyncBackups += syncBackups;
            syncBackups = 0;
        }
        if (syncBackups + asyncBackups == 0) {
            return 0;
        }
        return this.makeBackups(backupAwareOp, op.getPartitionId(), replicaVersions, syncBackups, asyncBackups);
    }

    int syncBackups(int requestedSyncBackups, int requestedAsyncBackups, boolean syncForced) {
        if (syncForced) {
            requestedSyncBackups += requestedAsyncBackups;
        }
        InternalPartitionService partitionService = this.node.getPartitionService();
        int maxBackupCount = partitionService.getMaxAllowedBackupCount();
        return Math.min(maxBackupCount, requestedSyncBackups);
    }

    int asyncBackups(int requestedSyncBackups, int requestedAsyncBackups, boolean syncForced) {
        if (syncForced || requestedAsyncBackups == 0) {
            return 0;
        }
        InternalPartitionService partitionService = this.node.getPartitionService();
        int maxBackupCount = partitionService.getMaxAllowedBackupCount();
        return Math.min(maxBackupCount - requestedSyncBackups, requestedAsyncBackups);
    }

    private int requestedSyncBackups(BackupAwareOperation op) {
        int backups = op.getSyncBackupCount();
        if (backups < 0) {
            throw new IllegalArgumentException("Can't create backup for " + op + ", sync backup count can't be smaller than 0, but found: " + backups);
        }
        if (backups > 6) {
            throw new IllegalArgumentException("Can't create backup for " + op + ", sync backup count can't be larger than " + 6 + ", but found: " + backups);
        }
        return backups;
    }

    private int requestedAsyncBackups(BackupAwareOperation op) {
        int backups = op.getAsyncBackupCount();
        if (backups < 0) {
            throw new IllegalArgumentException("Can't create backup for " + op + ", async backup count can't be smaller than 0, but found: " + backups);
        }
        if (backups > 6) {
            throw new IllegalArgumentException("Can't create backup for " + op + ", async backup count can't be larger than " + 6 + ", but found: " + backups);
        }
        return backups;
    }

    private int requestedTotalBackups(BackupAwareOperation op) {
        int backups = op.getSyncBackupCount() + op.getAsyncBackupCount();
        if (backups > 6) {
            throw new IllegalArgumentException("Can't create backup for " + op + ", the sum of async and sync backups is larger than " + 6 + ", sync backup count is " + op.getSyncBackupCount() + ", async backup count is " + op.getAsyncBackupCount());
        }
        return backups;
    }

    private int makeBackups(BackupAwareOperation backupAwareOp, int partitionId, long[] replicaVersions, int syncBackups, int asyncBackups) {
        int totalBackups = syncBackups + asyncBackups;
        InternalPartitionService partitionService = this.node.getPartitionService();
        InternalPartition partition = partitionService.getPartition(partitionId);
        int sendSyncBackups = totalBackups == 1 ? this.sendSingleBackup(backupAwareOp, partition, replicaVersions, syncBackups) : this.sendMultipleBackups(backupAwareOp, partition, replicaVersions, syncBackups, totalBackups);
        return sendSyncBackups;
    }

    private int sendSingleBackup(BackupAwareOperation backupAwareOp, InternalPartition partition, long[] replicaVersions, int syncBackups) {
        return this.sendSingleBackup(backupAwareOp, partition, replicaVersions, syncBackups, 1);
    }

    private int sendMultipleBackups(BackupAwareOperation backupAwareOp, InternalPartition partition, long[] replicaVersions, int syncBackups, int totalBackups) {
        int sendSyncBackups = 0;
        Operation backupOp = this.getBackupOperation(backupAwareOp);
        if (!(backupOp instanceof TargetAware)) {
            Object backupOpData = this.nodeEngine.getSerializationService().toData(backupOp);
            for (int replicaIndex = 1; replicaIndex <= totalBackups; ++replicaIndex) {
                PartitionReplica target = partition.getReplica(replicaIndex);
                if (target == null || this.skipSendingBackupToTarget(partition, target)) continue;
                boolean isSyncBackup = replicaIndex <= syncBackups;
                Backup backup = OperationBackupHandler.newBackup(backupAwareOp, backupOpData, replicaVersions, replicaIndex, isSyncBackup);
                this.outboundOperationHandler.send((Operation)backup, target.address());
                if (!isSyncBackup) continue;
                ++sendSyncBackups;
            }
        } else {
            for (int replicaIndex = 1; replicaIndex <= totalBackups; ++replicaIndex) {
                int syncBackupSent = this.sendSingleBackup(backupAwareOp, partition, replicaVersions, syncBackups, replicaIndex);
                sendSyncBackups += syncBackupSent;
            }
        }
        return sendSyncBackups;
    }

    private int sendSingleBackup(BackupAwareOperation backupAwareOp, InternalPartition partition, long[] replicaVersions, int syncBackups, int replica) {
        Operation backupOp = this.getBackupOperation(backupAwareOp);
        PartitionReplica target = partition.getReplica(replica);
        if (target != null) {
            if (this.skipSendingBackupToTarget(partition, target)) {
                return 0;
            }
            if (backupOp instanceof TargetAware) {
                ((TargetAware)((Object)backupOp)).setTarget(target.address());
            }
            boolean isSyncBackup = syncBackups == 1;
            Backup backup = OperationBackupHandler.newBackup(backupAwareOp, backupOp, replicaVersions, 1, isSyncBackup);
            this.outboundOperationHandler.send((Operation)backup, target.address());
            if (isSyncBackup) {
                return 1;
            }
        }
        return 0;
    }

    private Operation getBackupOperation(BackupAwareOperation backupAwareOp) {
        Operation backupOp = backupAwareOp.getBackupOperation();
        if (backupOp == null) {
            throw new IllegalArgumentException("Backup operation should not be null! " + backupAwareOp);
        }
        if (ASSERTION_ENABLED) {
            this.checkServiceNamespaces(backupAwareOp, backupOp);
        }
        Operation op = (Operation)((Object)backupAwareOp);
        backupOp.setServiceName(op.getServiceName());
        backupOp.setNodeEngine(this.nodeEngine);
        return backupOp;
    }

    private void checkServiceNamespaces(BackupAwareOperation backupAwareOp, Operation backupOp) {
        Object service;
        Operation op = (Operation)((Object)backupAwareOp);
        try {
            service = op.getService();
        }
        catch (Exception ignored) {
            return;
        }
        if (service instanceof FragmentedMigrationAwareService) {
            assert (backupAwareOp instanceof ServiceNamespaceAware) : service + " is instance of FragmentedMigrationAwareService, " + backupAwareOp + " should implement ServiceNamespaceAware!";
            assert (backupOp instanceof ServiceNamespaceAware) : service + " is instance of FragmentedMigrationAwareService, " + backupOp + " should implement ServiceNamespaceAware!";
        } else {
            assert (!(backupAwareOp instanceof ServiceNamespaceAware)) : service + " is NOT instance of FragmentedMigrationAwareService, " + backupAwareOp + " should NOT implement ServiceNamespaceAware!";
            assert (!(backupOp instanceof ServiceNamespaceAware)) : service + " is NOT instance of FragmentedMigrationAwareService, " + backupOp + " should NOT implement ServiceNamespaceAware!";
        }
    }

    private static Backup newBackup(BackupAwareOperation backupAwareOp, Object backupOp, long[] replicaVersions, int replicaIndex, boolean respondBack) {
        Backup backup;
        Operation op = (Operation)((Object)backupAwareOp);
        if (backupOp instanceof Operation) {
            backup = new Backup((Operation)backupOp, op.getCallerAddress(), replicaVersions, respondBack);
        } else if (backupOp instanceof Data) {
            backup = new Backup((Data)backupOp, op.getCallerAddress(), replicaVersions, respondBack);
        } else {
            throw new IllegalArgumentException("Only 'Data' or 'Operation' typed backup operation is supported!");
        }
        backup.setPartitionId(op.getPartitionId()).setReplicaIndex(replicaIndex);
        if (OperationAccessor.hasActiveInvocation(op)) {
            OperationAccessor.setCallId(backup, op.getCallId());
        }
        return backup;
    }

    private boolean skipSendingBackupToTarget(InternalPartition partition, PartitionReplica target) {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        assert (!target.isIdentical(this.nodeEngine.getLocalMember())) : "Could not send backup operation, because " + target + " is local member itself! Local-member: " + clusterService.getLocalMember() + ", " + partition;
        if (clusterService.getMember(target.address(), target.uuid()) == null) {
            if (this.logger.isFinestEnabled()) {
                if (clusterService.isMissingMember(target.address(), target.uuid())) {
                    this.logger.finest("Could not send backup operation, because " + target + " is a missing member. " + partition);
                } else {
                    this.logger.finest("Could not send backup operation, because " + target + " is not a known member. " + partition);
                }
            }
            return true;
        }
        return false;
    }
}

