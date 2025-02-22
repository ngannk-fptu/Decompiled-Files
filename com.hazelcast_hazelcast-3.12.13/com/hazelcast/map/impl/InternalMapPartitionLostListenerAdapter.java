/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.MapPartitionLostEvent;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
class InternalMapPartitionLostListenerAdapter
implements ListenerAdapter {
    private final MapPartitionLostListener partitionLostListener;

    public InternalMapPartitionLostListenerAdapter(MapPartitionLostListener partitionLostListener) {
        this.partitionLostListener = partitionLostListener;
    }

    public void onEvent(Object event) {
        this.partitionLostListener.partitionLost((MapPartitionLostEvent)event);
    }
}

