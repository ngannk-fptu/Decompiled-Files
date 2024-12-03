/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalMapStats;

public interface LocalReplicatedMapStats
extends LocalMapStats {
    @Deprecated
    public long getReplicationEventCount();
}

