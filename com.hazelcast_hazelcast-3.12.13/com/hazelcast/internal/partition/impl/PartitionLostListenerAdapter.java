/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.partition.PartitionEventListener;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
class PartitionLostListenerAdapter
implements PartitionEventListener<PartitionLostEvent> {
    private final PartitionLostListener listener;

    public PartitionLostListenerAdapter(PartitionLostListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEvent(PartitionLostEvent event) {
        this.listener.partitionLost(event);
    }
}

