/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition;

import com.hazelcast.partition.PartitionLostEvent;
import java.util.EventListener;

public interface PartitionLostListener
extends EventListener {
    public void partitionLost(PartitionLostEvent var1);
}

