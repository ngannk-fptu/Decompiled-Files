/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.memory.GarbageCollectorStats;
import com.hazelcast.monitor.LocalGCStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;

public class LocalGCStatsImpl
implements LocalGCStats {
    private long creationTime;
    private long minorCount;
    private long minorTime;
    private long majorCount;
    private long majorTime;
    private long unknownCount;
    private long unknownTime;

    public LocalGCStatsImpl() {
        this.creationTime = Clock.currentTimeMillis();
    }

    public LocalGCStatsImpl(GarbageCollectorStats gcStats) {
        this.setMajorCount(gcStats.getMajorCollectionCount());
        this.setMajorTime(gcStats.getMajorCollectionTime());
        this.setMinorCount(gcStats.getMinorCollectionCount());
        this.setMinorTime(gcStats.getMinorCollectionTime());
        this.setUnknownCount(gcStats.getUnknownCollectionCount());
        this.setUnknownTime(gcStats.getUnknownCollectionTime());
    }

    @Override
    public long getMajorCollectionCount() {
        return this.majorCount;
    }

    @Override
    public long getMajorCollectionTime() {
        return this.majorTime;
    }

    @Override
    public long getMinorCollectionCount() {
        return this.minorCount;
    }

    @Override
    public long getMinorCollectionTime() {
        return this.minorTime;
    }

    @Override
    public long getUnknownCollectionCount() {
        return this.unknownCount;
    }

    @Override
    public long getUnknownCollectionTime() {
        return this.unknownTime;
    }

    public void setMinorCount(long minorCount) {
        this.minorCount = minorCount;
    }

    public void setMinorTime(long minorTime) {
        this.minorTime = minorTime;
    }

    public void setMajorCount(long majorCount) {
        this.majorCount = majorCount;
    }

    public void setMajorTime(long majorTime) {
        this.majorTime = majorTime;
    }

    public void setUnknownCount(long unknownCount) {
        this.unknownCount = unknownCount;
    }

    public void setUnknownTime(long unknownTime) {
        this.unknownTime = unknownTime;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("minorCount", this.minorCount);
        root.add("minorTime", this.minorTime);
        root.add("majorCount", this.majorCount);
        root.add("majorTime", this.majorTime);
        root.add("unknownCount", this.unknownCount);
        root.add("unknownTime", this.unknownTime);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.minorCount = JsonUtil.getLong(json, "minorCount", -1L);
        this.minorTime = JsonUtil.getLong(json, "minorTime", -1L);
        this.majorCount = JsonUtil.getLong(json, "majorCount", -1L);
        this.majorTime = JsonUtil.getLong(json, "majorTime", -1L);
        this.unknownCount = JsonUtil.getLong(json, "unknownCount", -1L);
        this.unknownTime = JsonUtil.getLong(json, "unknownTime", -1L);
    }

    public String toString() {
        return "LocalGCStats{creationTime=" + this.creationTime + ", minorCount=" + this.minorCount + ", minorTime=" + this.minorTime + ", majorCount=" + this.majorCount + ", majorTime=" + this.majorTime + ", unknownCount=" + this.unknownCount + ", unknownTime=" + this.unknownTime + '}';
    }
}

