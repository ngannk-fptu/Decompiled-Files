/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.memory.DefaultGarbageCollectorStats;
import com.hazelcast.memory.GCStatsSupport;
import com.hazelcast.memory.GarbageCollectorStats;
import com.hazelcast.memory.MemorySize;
import com.hazelcast.memory.MemoryStats;
import com.hazelcast.memory.MemoryStatsSupport;

public class DefaultMemoryStats
implements MemoryStats {
    private final Runtime runtime = Runtime.getRuntime();
    private final DefaultGarbageCollectorStats gcStats = new DefaultGarbageCollectorStats();

    @Override
    public final long getTotalPhysical() {
        return MemoryStatsSupport.totalPhysicalMemory();
    }

    @Override
    public final long getFreePhysical() {
        return MemoryStatsSupport.freePhysicalMemory();
    }

    @Override
    public final long getMaxHeap() {
        return this.runtime.maxMemory();
    }

    @Override
    public final long getCommittedHeap() {
        return this.runtime.totalMemory();
    }

    @Override
    public final long getUsedHeap() {
        return this.runtime.totalMemory() - this.runtime.freeMemory();
    }

    @Override
    public final long getFreeHeap() {
        return this.runtime.freeMemory();
    }

    @Override
    public long getMaxNative() {
        return 0L;
    }

    @Override
    public long getCommittedNative() {
        return 0L;
    }

    @Override
    public long getUsedNative() {
        return 0L;
    }

    @Override
    public long getFreeNative() {
        return 0L;
    }

    @Override
    public long getMaxMetadata() {
        return 0L;
    }

    @Override
    public long getUsedMetadata() {
        return 0L;
    }

    @Override
    public GarbageCollectorStats getGCStats() {
        GCStatsSupport.fill(this.gcStats);
        return this.gcStats;
    }

    public String toString() {
        return "MemoryStats{Total Physical: " + MemorySize.toPrettyString(this.getTotalPhysical()) + ", Free Physical: " + MemorySize.toPrettyString(this.getFreePhysical()) + ", Max Heap: " + MemorySize.toPrettyString(this.getMaxHeap()) + ", Committed Heap: " + MemorySize.toPrettyString(this.getCommittedHeap()) + ", Used Heap: " + MemorySize.toPrettyString(this.getUsedHeap()) + ", Free Heap: " + MemorySize.toPrettyString(this.getFreeHeap()) + ", " + this.getGCStats() + '}';
    }
}

