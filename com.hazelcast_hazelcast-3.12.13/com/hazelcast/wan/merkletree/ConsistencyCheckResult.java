/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

public class ConsistencyCheckResult {
    private final int lastCheckedPartitionCount;
    private final int lastDiffPartitionCount;
    private final int lastCheckedLeafCount;
    private final int lastDiffLeafCount;
    private final int lastEntriesToSync;

    public ConsistencyCheckResult() {
        this.lastCheckedPartitionCount = 0;
        this.lastDiffPartitionCount = 0;
        this.lastCheckedLeafCount = 0;
        this.lastDiffLeafCount = 0;
        this.lastEntriesToSync = 0;
    }

    public ConsistencyCheckResult(int lastCheckedPartitionCount, int lastDiffPartitionCount, int lastCheckedLeafCount, int lastDiffLeafCount, int lastEntriesToSync) {
        this.lastCheckedPartitionCount = lastCheckedPartitionCount;
        this.lastDiffPartitionCount = lastDiffPartitionCount;
        this.lastCheckedLeafCount = lastCheckedLeafCount;
        this.lastDiffLeafCount = lastDiffLeafCount;
        this.lastEntriesToSync = lastEntriesToSync;
    }

    public int getLastCheckedPartitionCount() {
        return this.lastCheckedPartitionCount;
    }

    public int getLastDiffPartitionCount() {
        return this.lastDiffPartitionCount;
    }

    public int getLastCheckedLeafCount() {
        return this.lastCheckedLeafCount;
    }

    public int getLastDiffLeafCount() {
        return this.lastDiffLeafCount;
    }

    public int getLastEntriesToSync() {
        return this.lastEntriesToSync;
    }

    public boolean isRunning() {
        return this.lastCheckedPartitionCount == -1 && this.lastDiffPartitionCount == -1 && this.lastCheckedLeafCount == -1 && this.lastDiffLeafCount == -1 && this.lastEntriesToSync == -1;
    }

    public float getDiffPercentage() {
        return this.lastCheckedPartitionCount != 0 ? (float)this.lastDiffLeafCount / (float)this.lastCheckedLeafCount * 100.0f : 0.0f;
    }

    public boolean isDone() {
        return this.lastCheckedPartitionCount > 0 && this.lastDiffPartitionCount >= 0 && this.lastCheckedLeafCount > 0 && this.lastDiffLeafCount >= 0 && this.lastEntriesToSync >= 0;
    }
}

