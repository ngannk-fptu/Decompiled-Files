/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.spi.partition.IPartition;

public interface InternalPartition
extends IPartition {
    public static final int MAX_REPLICA_COUNT = 7;

    public PartitionReplica getOwnerReplicaOrNull();

    public int getReplicaIndex(PartitionReplica var1);

    public PartitionReplica getReplica(int var1);
}

