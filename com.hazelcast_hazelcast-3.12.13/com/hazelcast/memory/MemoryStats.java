/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.memory.GarbageCollectorStats;

public interface MemoryStats {
    @Probe(level=ProbeLevel.MANDATORY)
    public long getTotalPhysical();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getFreePhysical();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getMaxHeap();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getCommittedHeap();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getUsedHeap();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getFreeHeap();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getMaxNative();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getCommittedNative();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getUsedNative();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getFreeNative();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getMaxMetadata();

    @Probe(level=ProbeLevel.MANDATORY)
    public long getUsedMetadata();

    public GarbageCollectorStats getGCStats();
}

