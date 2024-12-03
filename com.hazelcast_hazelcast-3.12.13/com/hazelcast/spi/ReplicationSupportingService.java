/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.wan.WanReplicationEvent;

public interface ReplicationSupportingService {
    public void onReplicationEvent(WanReplicationEvent var1);
}

