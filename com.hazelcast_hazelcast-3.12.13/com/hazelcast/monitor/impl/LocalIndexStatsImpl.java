/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalIndexStats;
import com.hazelcast.monitor.impl.OnDemandIndexStats;

public class LocalIndexStatsImpl
implements LocalIndexStats {
    @Probe
    private volatile long creationTime;
    @Probe
    private volatile long queryCount;
    @Probe
    private volatile long hitCount;
    @Probe
    private volatile long averageHitLatency;
    @Probe
    private volatile double averageHitSelectivity;
    @Probe
    private volatile long insertCount;
    @Probe
    private volatile long totalInsertLatency;
    @Probe
    private volatile long updateCount;
    @Probe
    private volatile long totalUpdateLatency;
    @Probe
    private volatile long removeCount;
    @Probe
    private volatile long totalRemoveLatency;
    @Probe
    private volatile long memoryCost;

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public long getQueryCount() {
        return this.queryCount;
    }

    public void setQueryCount(long queryCount) {
        this.queryCount = queryCount;
    }

    @Override
    public long getHitCount() {
        return this.hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    @Override
    public long getAverageHitLatency() {
        return this.averageHitLatency;
    }

    public void setAverageHitLatency(long averageHitLatency) {
        this.averageHitLatency = averageHitLatency;
    }

    @Override
    public double getAverageHitSelectivity() {
        return this.averageHitSelectivity;
    }

    public void setAverageHitSelectivity(double averageHitSelectivity) {
        this.averageHitSelectivity = averageHitSelectivity;
    }

    @Override
    public long getInsertCount() {
        return this.insertCount;
    }

    public void setInsertCount(long insertCount) {
        this.insertCount = insertCount;
    }

    @Override
    public long getTotalInsertLatency() {
        return this.totalInsertLatency;
    }

    public void setTotalInsertLatency(long totalInsertLatency) {
        this.totalInsertLatency = totalInsertLatency;
    }

    @Override
    public long getUpdateCount() {
        return this.updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public long getTotalUpdateLatency() {
        return this.totalUpdateLatency;
    }

    public void setTotalUpdateLatency(long totalUpdateLatency) {
        this.totalUpdateLatency = totalUpdateLatency;
    }

    @Override
    public long getRemoveCount() {
        return this.removeCount;
    }

    public void setRemoveCount(long removeCount) {
        this.removeCount = removeCount;
    }

    @Override
    public long getTotalRemoveLatency() {
        return this.totalRemoveLatency;
    }

    public void setTotalRemoveLatency(long totalRemoveLatency) {
        this.totalRemoveLatency = totalRemoveLatency;
    }

    @Override
    public long getMemoryCost() {
        return this.memoryCost;
    }

    public void setMemoryCost(long memoryCost) {
        this.memoryCost = memoryCost;
    }

    public void setAllFrom(OnDemandIndexStats onDemandStats) {
        this.creationTime = onDemandStats.getCreationTime();
        this.hitCount = onDemandStats.getHitCount();
        this.queryCount = onDemandStats.getQueryCount();
        this.averageHitSelectivity = onDemandStats.getAverageHitSelectivity();
        this.averageHitLatency = onDemandStats.getAverageHitLatency();
        this.insertCount = onDemandStats.getInsertCount();
        this.totalInsertLatency = onDemandStats.getTotalInsertLatency();
        this.updateCount = onDemandStats.getUpdateCount();
        this.totalUpdateLatency = onDemandStats.getTotalUpdateLatency();
        this.removeCount = onDemandStats.getRemoveCount();
        this.totalRemoveLatency = onDemandStats.getTotalRemoveLatency();
        this.memoryCost = onDemandStats.getMemoryCost();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("creationTime", this.creationTime);
        json.add("hitCount", this.hitCount);
        json.add("queryCount", this.queryCount);
        json.add("averageHitSelectivity", this.averageHitSelectivity);
        json.add("averageHitLatency", this.averageHitLatency);
        json.add("insertCount", this.insertCount);
        json.add("totalInsertLatency", this.totalInsertLatency);
        json.add("updateCount", this.updateCount);
        json.add("totalUpdateLatency", this.totalUpdateLatency);
        json.add("removeCount", this.removeCount);
        json.add("totalRemoveLatency", this.totalRemoveLatency);
        json.add("memoryCost", this.memoryCost);
        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = json.getLong("creationTime", -1L);
        this.hitCount = json.getLong("hitCount", -1L);
        this.queryCount = json.getLong("queryCount", -1L);
        this.averageHitSelectivity = json.getDouble("averageHitSelectivity", -1.0);
        this.averageHitLatency = json.getLong("averageHitLatency", -1L);
        this.insertCount = json.getLong("insertCount", -1L);
        this.totalInsertLatency = json.getLong("totalInsertLatency", -1L);
        this.updateCount = json.getLong("updateCount", -1L);
        this.totalUpdateLatency = json.getLong("totalUpdateLatency", -1L);
        this.removeCount = json.getLong("removeCount", -1L);
        this.totalRemoveLatency = json.getLong("totalRemoveLatency", -1L);
        this.memoryCost = json.getLong("memoryCost", -1L);
    }

    public String toString() {
        return "LocalIndexStatsImpl{creationTime=" + this.creationTime + ", hitCount=" + this.hitCount + ", queryCount=" + this.queryCount + ", averageHitSelectivity=" + this.averageHitSelectivity + ", averageHitLatency=" + this.averageHitLatency + ", insertCount=" + this.insertCount + ", totalInsertLatency=" + this.totalInsertLatency + ", updateCount=" + this.updateCount + ", totalUpdateLatency=" + this.totalUpdateLatency + ", removeCount=" + this.removeCount + ", totalRemoveLatency=" + this.totalRemoveLatency + ", memoryCost=" + this.memoryCost + '}';
    }
}

