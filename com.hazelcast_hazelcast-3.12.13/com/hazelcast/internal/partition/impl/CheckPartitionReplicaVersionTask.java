/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.AbstractPartitionPrimaryReplicaAntiEntropyTask;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.Preconditions;
import java.util.Collection;

final class CheckPartitionReplicaVersionTask
extends AbstractPartitionPrimaryReplicaAntiEntropyTask {
    private final int replicaIndex;
    private final ExecutionCallback callback;

    CheckPartitionReplicaVersionTask(NodeEngineImpl nodeEngine, int partitionId, int replicaIndex, ExecutionCallback callback) {
        super(nodeEngine, partitionId);
        if (replicaIndex < 1 || replicaIndex > 6) {
            throw new IllegalArgumentException("Replica index should be in range [1-6]");
        }
        this.replicaIndex = replicaIndex;
        Preconditions.checkNotNull(callback);
        this.callback = callback;
    }

    @Override
    public void run() {
        InternalPartition partition = this.partitionService.getPartition(this.partitionId);
        if (!partition.isLocal() || partition.isMigrating()) {
            this.callback.onResponse(false);
            return;
        }
        Collection<ServiceNamespace> namespaces = this.retainAndGetNamespaces();
        PartitionReplica target = partition.getReplica(this.replicaIndex);
        if (target == null) {
            this.callback.onResponse(false);
            return;
        }
        this.invokePartitionBackupReplicaAntiEntropyOp(this.replicaIndex, target, namespaces, this.callback);
    }
}

