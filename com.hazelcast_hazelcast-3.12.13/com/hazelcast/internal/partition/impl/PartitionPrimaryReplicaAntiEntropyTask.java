/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.AbstractPartitionPrimaryReplicaAntiEntropyTask;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.util.Collection;

final class PartitionPrimaryReplicaAntiEntropyTask
extends AbstractPartitionPrimaryReplicaAntiEntropyTask {
    PartitionPrimaryReplicaAntiEntropyTask(NodeEngineImpl nodeEngine, int partitionId) {
        super(nodeEngine, partitionId);
    }

    @Override
    public void run() {
        InternalPartition partition = this.partitionService.getPartition(this.partitionId, false);
        if (!partition.isLocal() || partition.isMigrating()) {
            return;
        }
        Collection<ServiceNamespace> namespaces = this.retainAndGetNamespaces();
        for (int index = 1; index < 7; ++index) {
            PartitionReplica replica = partition.getReplica(index);
            if (replica == null) continue;
            this.invokePartitionBackupReplicaAntiEntropyOp(index, replica, namespaces, null);
        }
    }
}

