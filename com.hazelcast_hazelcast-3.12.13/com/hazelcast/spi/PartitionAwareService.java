/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.partition.IPartitionLostEvent;

public interface PartitionAwareService {
    public void onPartitionLost(IPartitionLostEvent var1);
}

