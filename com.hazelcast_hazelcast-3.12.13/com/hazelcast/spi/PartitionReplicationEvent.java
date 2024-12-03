/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import java.util.EventObject;

public class PartitionReplicationEvent
extends EventObject {
    private final int partitionId;
    private final int replicaIndex;

    public PartitionReplicationEvent(int partitionId, int replicaIndex) {
        super(partitionId);
        this.partitionId = partitionId;
        this.replicaIndex = replicaIndex;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getReplicaIndex() {
        return this.replicaIndex;
    }

    @Override
    public String toString() {
        return "PartitionReplicationEvent{partitionId=" + this.partitionId + ", replicaIndex=" + this.replicaIndex + '}';
    }
}

