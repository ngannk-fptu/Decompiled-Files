/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.memory.MemoryStats;
import com.hazelcast.monitor.LocalGCStats;
import com.hazelcast.monitor.LocalMemoryStats;
import com.hazelcast.monitor.impl.LocalGCStatsImpl;
import com.hazelcast.util.JsonUtil;

public class LocalMemoryStatsImpl
implements LocalMemoryStats {
    public static final String JSON_CREATION_TIME = "creationTime";
    public static final String JSON_TOTAL_PHYSICAL = "totalPhysical";
    public static final String JSON_FREE_PHYSICAL = "freePhysical";
    public static final String JSON_MAX_NATIVE_MEMORY = "maxNativeMemory";
    public static final String JSON_COMMITTED_NATIVE_MEMORY = "committedNativeMemory";
    public static final String JSON_USED_NATIVE_MEMORY = "usedNativeMemory";
    public static final String JSON_FREE_NATIVE_MEMORY = "freeNativeMemory";
    public static final String JSON_MAX_HEAP = "maxHeap";
    public static final String JSON_COMMITTED_HEAP = "committedHeap";
    public static final String JSON_USED_HEAP = "usedHeap";
    public static final String JSON_GC_STATS = "gcStats";
    private long creationTime;
    private long totalPhysical;
    private long freePhysical;
    private long maxNativeMemory;
    private long committedNativeMemory;
    private long usedNativeMemory;
    private long freeNativeMemory;
    private long maxMetadata;
    private long usedMetadata;
    private long maxHeap;
    private long committedHeap;
    private long usedHeap;
    private LocalGCStats gcStats;

    public LocalMemoryStatsImpl() {
    }

    public LocalMemoryStatsImpl(MemoryStats memoryStats) {
        this.setTotalPhysical(memoryStats.getTotalPhysical());
        this.setFreePhysical(memoryStats.getFreePhysical());
        this.setMaxNativeMemory(memoryStats.getMaxNative());
        this.setCommittedNativeMemory(memoryStats.getCommittedNative());
        this.setUsedNativeMemory(memoryStats.getUsedNative());
        this.setFreeNativeMemory(memoryStats.getFreeNative());
        this.setMaxMetadata(memoryStats.getMaxMetadata());
        this.setUsedMetadata(memoryStats.getUsedMetadata());
        this.setMaxHeap(memoryStats.getMaxHeap());
        this.setCommittedHeap(memoryStats.getCommittedHeap());
        this.setUsedHeap(memoryStats.getUsedHeap());
        this.setGcStats(new LocalGCStatsImpl(memoryStats.getGCStats()));
    }

    @Override
    public long getTotalPhysical() {
        return this.totalPhysical;
    }

    public void setTotalPhysical(long totalPhysical) {
        this.totalPhysical = totalPhysical;
    }

    @Override
    public long getFreePhysical() {
        return this.freePhysical;
    }

    public void setFreePhysical(long freePhysical) {
        this.freePhysical = freePhysical;
    }

    @Override
    public long getMaxNative() {
        return this.maxNativeMemory;
    }

    public void setMaxNativeMemory(long maxNativeMemory) {
        this.maxNativeMemory = maxNativeMemory;
    }

    @Override
    public long getCommittedNative() {
        return this.committedNativeMemory;
    }

    public void setCommittedNativeMemory(long committed) {
        this.committedNativeMemory = committed;
    }

    @Override
    public long getUsedNative() {
        return this.usedNativeMemory;
    }

    public void setUsedNativeMemory(long used) {
        this.usedNativeMemory = used;
    }

    @Override
    public long getFreeNative() {
        return this.freeNativeMemory;
    }

    public void setFreeNativeMemory(long freeNativeMemory) {
        this.freeNativeMemory = freeNativeMemory;
    }

    @Override
    public long getMaxMetadata() {
        return this.maxMetadata;
    }

    public void setMaxMetadata(long maxMetadata) {
        this.maxMetadata = maxMetadata;
    }

    @Override
    public long getUsedMetadata() {
        return this.usedMetadata;
    }

    public void setUsedMetadata(long usedMetadata) {
        this.usedMetadata = usedMetadata;
    }

    @Override
    public long getMaxHeap() {
        return this.maxHeap;
    }

    @Override
    public long getCommittedHeap() {
        return this.committedHeap;
    }

    @Override
    public long getUsedHeap() {
        return this.usedHeap;
    }

    public void setMaxHeap(long maxHeap) {
        this.maxHeap = maxHeap;
    }

    public void setCommittedHeap(long committedHeap) {
        this.committedHeap = committedHeap;
    }

    public void setUsedHeap(long usedHeap) {
        this.usedHeap = usedHeap;
    }

    @Override
    public long getFreeHeap() {
        return this.maxHeap - this.usedHeap;
    }

    @Override
    public LocalGCStats getGCStats() {
        return this.gcStats;
    }

    public void setGcStats(LocalGCStats gcStats) {
        this.gcStats = gcStats;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add(JSON_CREATION_TIME, this.creationTime);
        root.add(JSON_TOTAL_PHYSICAL, this.totalPhysical);
        root.add(JSON_FREE_PHYSICAL, this.freePhysical);
        root.add(JSON_MAX_NATIVE_MEMORY, this.maxNativeMemory);
        root.add(JSON_COMMITTED_NATIVE_MEMORY, this.committedNativeMemory);
        root.add(JSON_USED_NATIVE_MEMORY, this.usedNativeMemory);
        root.add(JSON_FREE_NATIVE_MEMORY, this.freeNativeMemory);
        root.add(JSON_MAX_HEAP, this.maxHeap);
        root.add(JSON_COMMITTED_HEAP, this.committedHeap);
        root.add(JSON_USED_HEAP, this.usedHeap);
        if (this.gcStats == null) {
            this.gcStats = new LocalGCStatsImpl();
        }
        root.add(JSON_GC_STATS, this.gcStats.toJson());
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, JSON_CREATION_TIME, -1L);
        this.totalPhysical = JsonUtil.getLong(json, JSON_TOTAL_PHYSICAL, -1L);
        this.freePhysical = JsonUtil.getLong(json, JSON_FREE_PHYSICAL, -1L);
        this.maxNativeMemory = JsonUtil.getLong(json, JSON_MAX_NATIVE_MEMORY, -1L);
        this.committedNativeMemory = JsonUtil.getLong(json, JSON_COMMITTED_NATIVE_MEMORY, -1L);
        this.usedNativeMemory = JsonUtil.getLong(json, JSON_USED_NATIVE_MEMORY, -1L);
        this.freeNativeMemory = JsonUtil.getLong(json, JSON_FREE_NATIVE_MEMORY, -1L);
        this.maxHeap = JsonUtil.getLong(json, JSON_MAX_HEAP, -1L);
        this.committedHeap = JsonUtil.getLong(json, JSON_COMMITTED_HEAP, -1L);
        this.usedHeap = JsonUtil.getLong(json, JSON_USED_HEAP, -1L);
        this.gcStats = new LocalGCStatsImpl();
        if (json.get(JSON_GC_STATS) != null) {
            this.gcStats.fromJson(JsonUtil.getObject(json, JSON_GC_STATS));
        }
    }

    public String toString() {
        return "LocalMemoryStats{totalPhysical=" + this.totalPhysical + ", freePhysical=" + this.freePhysical + ", maxNativeMemory=" + this.maxNativeMemory + ", committedNativeMemory=" + this.committedNativeMemory + ", usedNativeMemory=" + this.usedNativeMemory + ", maxMetadata=" + this.maxMetadata + ", usedUsedMetadata=" + this.usedMetadata + ", maxHeap=" + this.maxHeap + ", committedHeap=" + this.committedHeap + ", usedHeap=" + this.usedHeap + ", gcStats=" + this.gcStats + '}';
    }
}

