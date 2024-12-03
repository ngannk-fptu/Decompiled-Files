/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;

public interface ReplicationEventObject {
    public void incrementEventCount(DistributedServiceWanEventCounters var1);

    public Data getKey();
}

