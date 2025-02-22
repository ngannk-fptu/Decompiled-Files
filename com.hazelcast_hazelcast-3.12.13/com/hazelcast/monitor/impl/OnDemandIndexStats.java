/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

public class OnDemandIndexStats {
    private long creationTime;
    private long entryCount;
    private long queryCount;
    private long hitCount;
    private long averageHitLatency;
    private double averageHitSelectivity;
    private long insertCount;
    private long totalInsertLatency;
    private long updateCount;
    private long totalUpdateLatency;
    private long removeCount;
    private long totalRemoveLatency;
    private long memoryCost;
    private long totalHitCount;

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getQueryCount() {
        return this.queryCount;
    }

    public void setQueryCount(long queryCount) {
        this.queryCount = queryCount;
    }

    public long getHitCount() {
        return this.hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public long getAverageHitLatency() {
        return this.averageHitLatency;
    }

    public void setAverageHitLatency(long averageHitLatency) {
        this.averageHitLatency = averageHitLatency;
    }

    public double getAverageHitSelectivity() {
        return this.averageHitSelectivity;
    }

    public void setAverageHitSelectivity(double averageHitSelectivity) {
        this.averageHitSelectivity = averageHitSelectivity;
    }

    public long getInsertCount() {
        return this.insertCount;
    }

    public void setInsertCount(long insertCount) {
        this.insertCount = insertCount;
    }

    public long getTotalInsertLatency() {
        return this.totalInsertLatency;
    }

    public void setTotalInsertLatency(long totalInsertLatency) {
        this.totalInsertLatency = totalInsertLatency;
    }

    public long getUpdateCount() {
        return this.updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    public long getTotalUpdateLatency() {
        return this.totalUpdateLatency;
    }

    public void setTotalUpdateLatency(long totalUpdateLatency) {
        this.totalUpdateLatency = totalUpdateLatency;
    }

    public long getRemoveCount() {
        return this.removeCount;
    }

    public void setRemoveCount(long removeCount) {
        this.removeCount = removeCount;
    }

    public long getTotalRemoveLatency() {
        return this.totalRemoveLatency;
    }

    public void setTotalRemoveLatency(long totalRemoveLatency) {
        this.totalRemoveLatency = totalRemoveLatency;
    }

    public long getMemoryCost() {
        return this.memoryCost;
    }

    public void setMemoryCost(long memoryCost) {
        this.memoryCost = memoryCost;
    }

    public long getTotalHitCount() {
        return this.totalHitCount;
    }

    public void setTotalHitCount(long totalHitCount) {
        this.totalHitCount = totalHitCount;
    }

    public String toString() {
        return "LocalIndexStatsImpl{creationTime=" + this.creationTime + ", hitCount=" + this.hitCount + ", entryCount=" + this.entryCount + ", queryCount=" + this.queryCount + ", averageHitSelectivity=" + this.averageHitSelectivity + ", averageHitLatency=" + this.averageHitLatency + ", insertCount=" + this.insertCount + ", totalInsertLatency=" + this.totalInsertLatency + ", updateCount=" + this.updateCount + ", totalUpdateLatency=" + this.totalUpdateLatency + ", removeCount=" + this.removeCount + ", totalRemoveLatency=" + this.totalRemoveLatency + ", memoryCost=" + this.memoryCost + ", totalHitCount=" + this.totalHitCount + '}';
    }
}

