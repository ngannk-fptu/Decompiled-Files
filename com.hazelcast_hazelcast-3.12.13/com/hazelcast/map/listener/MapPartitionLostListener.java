/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.map.MapPartitionLostEvent;
import com.hazelcast.map.listener.MapListener;

public interface MapPartitionLostListener
extends MapListener {
    public void partitionLost(MapPartitionLostEvent var1);
}

