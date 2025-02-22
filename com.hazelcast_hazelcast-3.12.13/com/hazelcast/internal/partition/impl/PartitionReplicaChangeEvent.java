/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.nio.Address;

public class PartitionReplicaChangeEvent {
    private final int partitionId;
    private final int replicaIndex;
    private final Address oldAddress;
    private final Address newAddress;

    public PartitionReplicaChangeEvent(int partitionId, int replicaIndex, Address oldAddress, Address newAddress) {
        this.partitionId = partitionId;
        this.replicaIndex = replicaIndex;
        this.oldAddress = oldAddress;
        this.newAddress = newAddress;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getReplicaIndex() {
        return this.replicaIndex;
    }

    public Address getOldAddress() {
        return this.oldAddress;
    }

    public Address getNewAddress() {
        return this.newAddress;
    }

    public String toString() {
        return this.getClass().getName() + "{partitionId=" + this.partitionId + ", replicaIndex=" + this.replicaIndex + ", oldAddress=" + this.oldAddress + ", newAddress=" + this.newAddress + '}';
    }
}

