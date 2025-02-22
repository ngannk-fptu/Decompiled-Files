/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.spi.ServiceNamespace;

public final class ReplicaFragmentSyncInfo {
    final int partitionId;
    final ServiceNamespace namespace;
    final int replicaIndex;
    final PartitionReplica target;

    ReplicaFragmentSyncInfo(int partitionId, ServiceNamespace namespace, int replicaIndex, PartitionReplica target) {
        this.partitionId = partitionId;
        this.namespace = namespace;
        this.replicaIndex = replicaIndex;
        this.target = target;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReplicaFragmentSyncInfo that = (ReplicaFragmentSyncInfo)o;
        return this.partitionId == that.partitionId && this.replicaIndex == that.replicaIndex && this.namespace.equals(that.namespace);
    }

    public int hashCode() {
        int result = this.partitionId;
        result = 31 * result + this.namespace.hashCode();
        result = 31 * result + this.replicaIndex;
        return result;
    }

    public String toString() {
        return "ReplicaFragmentSyncInfo{partitionId=" + this.partitionId + ", namespace=" + this.namespace + ", replicaIndex=" + this.replicaIndex + ", target=" + this.target + '}';
    }
}

