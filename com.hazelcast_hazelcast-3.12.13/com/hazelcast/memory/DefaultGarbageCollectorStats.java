/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.memory.GarbageCollectorStats;

public class DefaultGarbageCollectorStats
implements GarbageCollectorStats {
    private volatile long minorCount;
    private volatile long minorTime;
    private volatile long majorCount;
    private volatile long majorTime;
    private volatile long unknownCount;
    private volatile long unknownTime;

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

    void setMinorCount(long minorCount) {
        this.minorCount = minorCount;
    }

    void setMinorTime(long minorTime) {
        this.minorTime = minorTime;
    }

    void setMajorCount(long majorCount) {
        this.majorCount = majorCount;
    }

    void setMajorTime(long majorTime) {
        this.majorTime = majorTime;
    }

    void setUnknownCount(long unknownCount) {
        this.unknownCount = unknownCount;
    }

    void setUnknownTime(long unknownTime) {
        this.unknownTime = unknownTime;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GarbageCollectorStats{");
        sb.append("MinorGC -> Count: ").append(this.minorCount).append(", Time (ms): ").append(this.minorTime).append(", MajorGC -> Count: ").append(this.majorCount).append(", Time (ms): ").append(this.majorTime);
        if (this.unknownCount > 0L) {
            sb.append(", UnknownGC -> Count: ").append(this.unknownCount).append(", Time (ms): ").append(this.unknownTime);
        }
        sb.append('}');
        return sb.toString();
    }
}

