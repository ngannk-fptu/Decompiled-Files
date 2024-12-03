/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.MigrationManager;
import com.hazelcast.logging.ILogger;

class PublishPartitionRuntimeStateTask
implements Runnable {
    private final Node node;
    private final InternalPartitionServiceImpl partitionService;
    private final ILogger logger;

    PublishPartitionRuntimeStateTask(Node node, InternalPartitionServiceImpl partitionService) {
        this.node = node;
        this.partitionService = partitionService;
        this.logger = node.getLogger(InternalPartitionService.class);
    }

    @Override
    public void run() {
        if (this.partitionService.isLocalMemberMaster()) {
            boolean migrationAllowed;
            MigrationManager migrationManager = this.partitionService.getMigrationManager();
            boolean bl = migrationAllowed = migrationManager.areMigrationTasksAllowed() && !this.partitionService.isFetchMostRecentPartitionTableTaskRequired();
            if (!migrationAllowed) {
                this.logger.fine("Not publishing partition runtime state since migration is not allowed.");
                return;
            }
            if (migrationManager.hasOnGoingMigration()) {
                this.logger.info("Remaining migration tasks in queue => " + this.partitionService.getMigrationQueueSize() + ". (" + migrationManager.getStats().formatToString(this.logger.isFineEnabled()) + ")");
            } else if (this.node.getState() == NodeState.ACTIVE) {
                if (this.node.getClusterService().getClusterVersion().isGreaterOrEqual(Versions.V3_12)) {
                    this.partitionService.checkClusterPartitionRuntimeStates();
                } else {
                    this.partitionService.publishPartitionRuntimeState();
                }
            }
        }
    }
}

