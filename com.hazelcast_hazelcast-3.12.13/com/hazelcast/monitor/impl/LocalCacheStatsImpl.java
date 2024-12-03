/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.LocalCacheStats;
import com.hazelcast.util.JsonUtil;

public class LocalCacheStatsImpl
implements LocalCacheStats {
    private long creationTime;
    private long lastAccessTime;
    private long lastUpdateTime;
    private long ownedEntryCount;
    private long cacheHits;
    private float cacheHitPercentage;
    private long cacheMisses;
    private float cacheMissPercentage;
    private long cacheGets;
    private long cachePuts;
    private long cacheRemovals;
    private long cacheEvictions;
    private float averageGetTime;
    private float averagePutTime;
    private float averageRemoveTime;

    public LocalCacheStatsImpl() {
    }

    public LocalCacheStatsImpl(CacheStatistics cacheStatistics) {
        this.creationTime = cacheStatistics.getCreationTime();
        this.lastAccessTime = cacheStatistics.getLastAccessTime();
        this.lastUpdateTime = cacheStatistics.getLastUpdateTime();
        this.ownedEntryCount = cacheStatistics.getOwnedEntryCount();
        this.cacheHits = cacheStatistics.getCacheHits();
        this.cacheHitPercentage = cacheStatistics.getCacheHitPercentage();
        this.cacheMisses = cacheStatistics.getCacheMisses();
        this.cacheMissPercentage = cacheStatistics.getCacheMissPercentage();
        this.cacheGets = cacheStatistics.getCacheGets();
        this.cachePuts = cacheStatistics.getCachePuts();
        this.cacheRemovals = cacheStatistics.getCacheRemovals();
        this.cacheEvictions = cacheStatistics.getCacheEvictions();
        this.averageGetTime = cacheStatistics.getAverageGetTime();
        this.averagePutTime = cacheStatistics.getAveragePutTime();
        this.averageRemoveTime = cacheStatistics.getAverageRemoveTime();
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    @Override
    public long getOwnedEntryCount() {
        return this.ownedEntryCount;
    }

    @Override
    public long getCacheHits() {
        return this.cacheHits;
    }

    @Override
    public float getCacheHitPercentage() {
        return this.cacheHitPercentage;
    }

    @Override
    public long getCacheMisses() {
        return this.cacheMisses;
    }

    @Override
    public float getCacheMissPercentage() {
        return this.cacheMissPercentage;
    }

    @Override
    public long getCacheGets() {
        return this.cacheGets;
    }

    @Override
    public long getCachePuts() {
        return this.cachePuts;
    }

    @Override
    public long getCacheRemovals() {
        return this.cacheRemovals;
    }

    @Override
    public long getCacheEvictions() {
        return this.cacheEvictions;
    }

    @Override
    public float getAverageGetTime() {
        return this.averageGetTime;
    }

    @Override
    public float getAveragePutTime() {
        return this.averagePutTime;
    }

    @Override
    public float getAverageRemoveTime() {
        return this.averageRemoveTime;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("lastAccessTime", this.lastAccessTime);
        root.add("lastUpdateTime", this.lastUpdateTime);
        root.add("ownedEntryCount", this.ownedEntryCount);
        root.add("cacheHits", this.cacheHits);
        root.add("cacheHitPercentage", this.cacheHitPercentage);
        root.add("cacheMisses", this.cacheMisses);
        root.add("cacheMissPercentage", this.cacheMissPercentage);
        root.add("cacheGets", this.cacheGets);
        root.add("cachePuts", this.cachePuts);
        root.add("cacheRemovals", this.cacheRemovals);
        root.add("cacheEvictions", this.cacheEvictions);
        root.add("averageGetTime", this.averageGetTime);
        root.add("averagePutTime", this.averagePutTime);
        root.add("averageRemoveTime", this.averageRemoveTime);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.lastAccessTime = JsonUtil.getLong(json, "lastAccessTime", -1L);
        this.lastUpdateTime = JsonUtil.getLong(json, "lastUpdateTime", -1L);
        this.ownedEntryCount = JsonUtil.getLong(json, "ownedEntryCount", -1L);
        this.cacheHits = JsonUtil.getLong(json, "cacheHits", -1L);
        this.cacheHitPercentage = JsonUtil.getFloat(json, "cacheHitPercentage", -1.0f);
        this.cacheMisses = JsonUtil.getLong(json, "cacheMisses", -1L);
        this.cacheMissPercentage = JsonUtil.getFloat(json, "cacheMissPercentage", -1.0f);
        this.cacheGets = JsonUtil.getLong(json, "cacheGets", -1L);
        this.cachePuts = JsonUtil.getLong(json, "cachePuts", -1L);
        this.cacheRemovals = JsonUtil.getLong(json, "cacheRemovals", -1L);
        this.cacheEvictions = JsonUtil.getLong(json, "cacheEvictions", -1L);
        this.averageGetTime = JsonUtil.getFloat(json, "averageGetTime", -1.0f);
        this.averagePutTime = JsonUtil.getFloat(json, "averagePutTime", -1.0f);
        this.averageRemoveTime = JsonUtil.getFloat(json, "averageRemoveTime", -1.0f);
    }

    public String toString() {
        return "LocalCacheStatsImpl{creationTime=" + this.creationTime + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", ownedEntryCount=" + this.ownedEntryCount + ", cacheHits=" + this.cacheHits + ", cacheHitPercentage=" + this.cacheHitPercentage + ", cacheMisses=" + this.cacheMisses + ", cacheMissPercentage=" + this.cacheMissPercentage + ", cacheGets=" + this.cacheGets + ", cachePuts=" + this.cachePuts + ", cacheRemovals=" + this.cacheRemovals + ", cacheEvictions=" + this.cacheEvictions + ", averageGetTime=" + this.averageGetTime + ", averagePutTime=" + this.averagePutTime + ", averageRemoveTime=" + this.averageRemoveTime + '}';
    }
}

