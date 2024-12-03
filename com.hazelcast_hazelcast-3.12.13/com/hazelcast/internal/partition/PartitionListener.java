/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.partition.impl.PartitionReplicaChangeEvent;

public interface PartitionListener {
    public void replicaChanged(PartitionReplicaChangeEvent var1);
}

