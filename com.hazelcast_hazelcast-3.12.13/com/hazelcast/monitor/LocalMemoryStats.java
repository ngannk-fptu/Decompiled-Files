/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.memory.MemoryStats;
import com.hazelcast.monitor.LocalGCStats;
import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalMemoryStats
extends MemoryStats,
LocalInstanceStats {
    @Override
    public LocalGCStats getGCStats();
}

