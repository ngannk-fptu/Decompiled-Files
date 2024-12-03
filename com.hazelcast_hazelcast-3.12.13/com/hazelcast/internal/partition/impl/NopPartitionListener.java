/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.impl.PartitionReplicaChangeEvent;

public final class NopPartitionListener
implements PartitionListener {
    @Override
    public void replicaChanged(PartitionReplicaChangeEvent event) {
    }
}

