/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalIndexStats;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.monitor.impl.LocalIndexStatsImpl;
import com.hazelcast.monitor.impl.NearCacheStatsImpl;
import com.hazelcast.monitor.impl.OnDemandIndexStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.JsonUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalMapStatsImpl
implements LocalMapStats {
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> LAST_ACCESS_TIME = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "lastAccessTime");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> LAST_UPDATE_TIME = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "lastUpdateTime");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> NUMBER_OF_OTHER_OPERATIONS = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "numberOfOtherOperations");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> NUMBER_OF_EVENTS = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "numberOfEvents");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> GET_COUNT = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "getCount");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> PUT_COUNT = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "putCount");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> REMOVE_COUNT = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "removeCount");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> TOTAL_GET_LATENCIES = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "totalGetLatenciesNanos");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> TOTAL_PUT_LATENCIES = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "totalPutLatenciesNanos");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> TOTAL_REMOVE_LATENCIES = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "totalRemoveLatenciesNanos");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> MAX_GET_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "maxGetLatency");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> MAX_PUT_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "maxPutLatency");
    private static final AtomicLongFieldUpdater<LocalMapStatsImpl> MAX_REMOVE_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalMapStatsImpl.class, "maxRemoveLatency");
    private final ConcurrentMap<String, LocalIndexStatsImpl> mutableIndexStats = new ConcurrentHashMap<String, LocalIndexStatsImpl>();
    private final Map<String, LocalIndexStats> indexStats = Collections.unmodifiableMap(this.mutableIndexStats);
    @Probe
    private volatile long lastAccessTime;
    @Probe
    private volatile long lastUpdateTime;
    @Probe
    private volatile long hits;
    @Probe
    private volatile long numberOfOtherOperations;
    @Probe
    private volatile long numberOfEvents;
    @Probe
    private volatile long getCount;
    @Probe
    private volatile long putCount;
    @Probe
    private volatile long removeCount;
    private volatile long totalGetLatenciesNanos;
    private volatile long totalPutLatenciesNanos;
    private volatile long totalRemoveLatenciesNanos;
    private volatile long maxGetLatency;
    private volatile long maxPutLatency;
    private volatile long maxRemoveLatency;
    @Probe
    private volatile long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long ownedEntryCount;
    @Probe
    private volatile long backupEntryCount;
    @Probe
    private volatile long ownedEntryMemoryCost;
    @Probe
    private volatile long backupEntryMemoryCost;
    @Probe
    private volatile long heapCost;
    @Probe
    private volatile long merkleTreesCost;
    @Probe
    private volatile long lockedEntryCount;
    @Probe
    private volatile long dirtyEntryCount;
    @Probe
    private volatile int backupCount;
    private volatile NearCacheStats nearCacheStats;
    @Probe
    private volatile long queryCount;
    @Probe
    private volatile long indexedQueryCount;

    @Override
    public long getOwnedEntryCount() {
        return this.ownedEntryCount;
    }

    public void setOwnedEntryCount(long ownedEntryCount) {
        this.ownedEntryCount = ownedEntryCount;
    }

    @Override
    public long getBackupEntryCount() {
        return this.backupEntryCount;
    }

    public void setBackupEntryCount(long backupEntryCount) {
        this.backupEntryCount = backupEntryCount;
    }

    @Override
    public int getBackupCount() {
        return this.backupCount;
    }

    public void setBackupCount(int backupCount) {
        this.backupCount = backupCount;
    }

    @Override
    public long getOwnedEntryMemoryCost() {
        return this.ownedEntryMemoryCost;
    }

    public void setOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
        this.ownedEntryMemoryCost = ownedEntryMemoryCost;
    }

    @Override
    public long getBackupEntryMemoryCost() {
        return this.backupEntryMemoryCost;
    }

    public void setBackupEntryMemoryCost(long backupEntryMemoryCost) {
        this.backupEntryMemoryCost = backupEntryMemoryCost;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        ConcurrencyUtil.setMax(this, LAST_ACCESS_TIME, lastAccessTime);
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        ConcurrencyUtil.setMax(this, LAST_UPDATE_TIME, lastUpdateTime);
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    @Override
    public long getLockedEntryCount() {
        return this.lockedEntryCount;
    }

    public void setLockedEntryCount(long lockedEntryCount) {
        this.lockedEntryCount = lockedEntryCount;
    }

    @Override
    public long getDirtyEntryCount() {
        return this.dirtyEntryCount;
    }

    public void setDirtyEntryCount(long dirtyEntryCount) {
        this.dirtyEntryCount = dirtyEntryCount;
    }

    @Override
    public long getPutOperationCount() {
        return this.putCount;
    }

    @Override
    public long getGetOperationCount() {
        return this.getCount;
    }

    @Override
    public long getRemoveOperationCount() {
        return this.removeCount;
    }

    @Override
    @Probe
    public long getTotalPutLatency() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalPutLatenciesNanos);
    }

    @Override
    @Probe
    public long getTotalGetLatency() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalGetLatenciesNanos);
    }

    @Override
    @Probe
    public long getTotalRemoveLatency() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalRemoveLatenciesNanos);
    }

    @Override
    @Probe
    public long getMaxPutLatency() {
        return TimeUnit.NANOSECONDS.toMillis(this.maxPutLatency);
    }

    @Override
    @Probe
    public long getMaxGetLatency() {
        return TimeUnit.NANOSECONDS.toMillis(this.maxGetLatency);
    }

    @Override
    @Probe
    public long getMaxRemoveLatency() {
        return TimeUnit.NANOSECONDS.toMillis(this.maxRemoveLatency);
    }

    @Override
    public long getEventOperationCount() {
        return this.numberOfEvents;
    }

    @Override
    public long getOtherOperationCount() {
        return this.numberOfOtherOperations;
    }

    @Override
    public long total() {
        return this.putCount + this.getCount + this.removeCount + this.numberOfOtherOperations;
    }

    @Override
    public long getHeapCost() {
        return this.heapCost;
    }

    public void setHeapCost(long heapCost) {
        this.heapCost = heapCost;
    }

    @Override
    public long getMerkleTreesCost() {
        return this.merkleTreesCost;
    }

    public void setMerkleTreesCost(long merkleTreeCost) {
        this.merkleTreesCost = merkleTreeCost;
    }

    @Override
    public NearCacheStats getNearCacheStats() {
        return this.nearCacheStats;
    }

    public void setNearCacheStats(NearCacheStats nearCacheStats) {
        this.nearCacheStats = nearCacheStats;
    }

    @Override
    public long getQueryCount() {
        return this.queryCount;
    }

    public void setQueryCount(long queryCount) {
        this.queryCount = queryCount;
    }

    @Override
    public long getIndexedQueryCount() {
        return this.indexedQueryCount;
    }

    public void setIndexedQueryCount(long indexedQueryCount) {
        this.indexedQueryCount = indexedQueryCount;
    }

    @Override
    public Map<String, LocalIndexStats> getIndexStats() {
        return this.indexStats;
    }

    public void setIndexStats(Map<String, LocalIndexStatsImpl> indexStats) {
        this.mutableIndexStats.clear();
        if (indexStats != null) {
            this.mutableIndexStats.putAll(indexStats);
        }
    }

    public void incrementPutLatencyNanos(long latencyNanos) {
        this.incrementPutLatencyNanos(1L, latencyNanos);
    }

    public void incrementPutLatencyNanos(long delta, long latencyNanos) {
        PUT_COUNT.addAndGet(this, delta);
        TOTAL_PUT_LATENCIES.addAndGet(this, latencyNanos);
        ConcurrencyUtil.setMax(this, MAX_PUT_LATENCY, latencyNanos);
    }

    public void incrementGetLatencyNanos(long latencyNanos) {
        this.incrementGetLatencyNanos(1L, latencyNanos);
    }

    public void incrementGetLatencyNanos(long delta, long latencyNanos) {
        GET_COUNT.addAndGet(this, delta);
        TOTAL_GET_LATENCIES.addAndGet(this, latencyNanos);
        ConcurrencyUtil.setMax(this, MAX_GET_LATENCY, latencyNanos);
    }

    public void incrementRemoveLatencyNanos(long latencyNanos) {
        REMOVE_COUNT.incrementAndGet(this);
        TOTAL_REMOVE_LATENCIES.addAndGet(this, latencyNanos);
        ConcurrencyUtil.setMax(this, MAX_REMOVE_LATENCY, latencyNanos);
    }

    public void incrementOtherOperations() {
        NUMBER_OF_OTHER_OPERATIONS.incrementAndGet(this);
    }

    public void incrementReceivedEvents() {
        NUMBER_OF_EVENTS.incrementAndGet(this);
    }

    public void updateIndexStats(Map<String, OnDemandIndexStats> freshIndexStats) {
        if (freshIndexStats == null) {
            return;
        }
        for (Map.Entry<String, OnDemandIndexStats> freshIndexEntry : freshIndexStats.entrySet()) {
            String indexName = freshIndexEntry.getKey();
            LocalIndexStatsImpl indexStats = (LocalIndexStatsImpl)this.mutableIndexStats.get(indexName);
            if (indexStats == null) {
                indexStats = new LocalIndexStatsImpl();
                indexStats.setAllFrom(freshIndexEntry.getValue());
                this.mutableIndexStats.putIfAbsent(indexName, indexStats);
                continue;
            }
            indexStats.setAllFrom(freshIndexEntry.getValue());
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("getCount", this.getCount);
        root.add("putCount", this.putCount);
        root.add("removeCount", this.removeCount);
        root.add("numberOfOtherOperations", this.numberOfOtherOperations);
        root.add("numberOfEvents", this.numberOfEvents);
        root.add("lastAccessTime", this.lastAccessTime);
        root.add("lastUpdateTime", this.lastUpdateTime);
        root.add("hits", this.hits);
        root.add("ownedEntryCount", this.ownedEntryCount);
        root.add("backupEntryCount", this.backupEntryCount);
        root.add("backupCount", this.backupCount);
        root.add("ownedEntryMemoryCost", this.ownedEntryMemoryCost);
        root.add("backupEntryMemoryCost", this.backupEntryMemoryCost);
        root.add("creationTime", this.creationTime);
        root.add("lockedEntryCount", this.lockedEntryCount);
        root.add("dirtyEntryCount", this.dirtyEntryCount);
        root.add("totalGetLatencies", TimeUnit.NANOSECONDS.toMillis(this.totalGetLatenciesNanos));
        root.add("totalPutLatencies", TimeUnit.NANOSECONDS.toMillis(this.totalPutLatenciesNanos));
        root.add("totalRemoveLatencies", TimeUnit.NANOSECONDS.toMillis(this.totalRemoveLatenciesNanos));
        root.add("maxGetLatency", TimeUnit.NANOSECONDS.toMillis(this.maxGetLatency));
        root.add("maxPutLatency", TimeUnit.NANOSECONDS.toMillis(this.maxPutLatency));
        root.add("maxRemoveLatency", TimeUnit.NANOSECONDS.toMillis(this.maxRemoveLatency));
        root.add("heapCost", this.heapCost);
        root.add("merkleTreesCost", this.merkleTreesCost);
        if (this.nearCacheStats != null) {
            root.add("nearCacheStats", this.nearCacheStats.toJson());
        }
        root.add("queryCount", this.queryCount);
        root.add("indexedQueryCount", this.indexedQueryCount);
        Map<String, LocalIndexStats> localIndexStats = this.indexStats;
        if (!localIndexStats.isEmpty()) {
            JsonObject indexes = new JsonObject();
            for (Map.Entry<String, LocalIndexStats> indexEntry : localIndexStats.entrySet()) {
                indexes.add(indexEntry.getKey(), indexEntry.getValue().toJson());
            }
            root.add("indexStats", indexes);
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.getCount = JsonUtil.getLong(json, "getCount", -1L);
        this.putCount = JsonUtil.getLong(json, "putCount", -1L);
        this.removeCount = JsonUtil.getLong(json, "removeCount", -1L);
        this.numberOfOtherOperations = JsonUtil.getLong(json, "numberOfOtherOperations", -1L);
        this.numberOfEvents = JsonUtil.getLong(json, "numberOfEvents", -1L);
        this.lastAccessTime = JsonUtil.getLong(json, "lastAccessTime", -1L);
        this.lastUpdateTime = JsonUtil.getLong(json, "lastUpdateTime", -1L);
        this.totalGetLatenciesNanos = TimeUnit.MILLISECONDS.toNanos(JsonUtil.getLong(json, "totalGetLatencies", -1L));
        this.totalPutLatenciesNanos = TimeUnit.MILLISECONDS.toNanos(JsonUtil.getLong(json, "totalPutLatencies", -1L));
        this.totalRemoveLatenciesNanos = TimeUnit.MILLISECONDS.toNanos(JsonUtil.getLong(json, "totalRemoveLatencies", -1L));
        this.maxGetLatency = TimeUnit.MILLISECONDS.toNanos(JsonUtil.getLong(json, "maxGetLatency", -1L));
        this.maxPutLatency = TimeUnit.MILLISECONDS.toNanos(JsonUtil.getLong(json, "maxPutLatency", -1L));
        this.maxRemoveLatency = TimeUnit.MILLISECONDS.toNanos(JsonUtil.getLong(json, "maxRemoveLatency", -1L));
        this.hits = JsonUtil.getLong(json, "hits", -1L);
        this.ownedEntryCount = JsonUtil.getLong(json, "ownedEntryCount", -1L);
        this.backupEntryCount = JsonUtil.getLong(json, "backupEntryCount", -1L);
        this.backupCount = JsonUtil.getInt(json, "backupCount", -1);
        this.ownedEntryMemoryCost = JsonUtil.getLong(json, "ownedEntryMemoryCost", -1L);
        this.backupEntryMemoryCost = JsonUtil.getLong(json, "backupEntryMemoryCost", -1L);
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.lockedEntryCount = JsonUtil.getLong(json, "lockedEntryCount", -1L);
        this.dirtyEntryCount = JsonUtil.getLong(json, "dirtyEntryCount", -1L);
        this.heapCost = JsonUtil.getLong(json, "heapCost", -1L);
        this.merkleTreesCost = JsonUtil.getLong(json, "merkleTreesCost", -1L);
        JsonValue jsonNearCacheStats = json.get("nearCacheStats");
        if (jsonNearCacheStats != null) {
            this.nearCacheStats = new NearCacheStatsImpl();
            this.nearCacheStats.fromJson(jsonNearCacheStats.asObject());
        }
        this.queryCount = JsonUtil.getLong(json, "queryCount", -1L);
        this.indexedQueryCount = JsonUtil.getLong(json, "indexedQueryCount", -1L);
        JsonObject indexes = JsonUtil.getObject(json, "indexStats", null);
        if (indexes != null && !indexes.isEmpty()) {
            HashMap<String, LocalIndexStatsImpl> localIndexStats = new HashMap<String, LocalIndexStatsImpl>();
            for (JsonObject.Member member : indexes) {
                LocalIndexStatsImpl indexStats = new LocalIndexStatsImpl();
                indexStats.fromJson(member.getValue().asObject());
                localIndexStats.put(member.getName(), indexStats);
            }
            this.setIndexStats(localIndexStats);
        } else {
            this.setIndexStats(null);
        }
    }

    public String toString() {
        return "LocalMapStatsImpl{lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", hits=" + this.hits + ", numberOfOtherOperations=" + this.numberOfOtherOperations + ", numberOfEvents=" + this.numberOfEvents + ", getCount=" + this.getCount + ", putCount=" + this.putCount + ", removeCount=" + this.removeCount + ", totalGetLatencies=" + TimeUnit.NANOSECONDS.toMillis(this.totalGetLatenciesNanos) + ", totalPutLatencies=" + TimeUnit.NANOSECONDS.toMillis(this.totalPutLatenciesNanos) + ", totalRemoveLatencies=" + TimeUnit.NANOSECONDS.toMillis(this.totalRemoveLatenciesNanos) + ", maxGetLatency=" + TimeUnit.NANOSECONDS.toMillis(this.maxGetLatency) + ", maxPutLatency=" + TimeUnit.NANOSECONDS.toMillis(this.maxPutLatency) + ", maxRemoveLatency=" + TimeUnit.NANOSECONDS.toMillis(this.maxRemoveLatency) + ", ownedEntryCount=" + this.ownedEntryCount + ", backupEntryCount=" + this.backupEntryCount + ", backupCount=" + this.backupCount + ", ownedEntryMemoryCost=" + this.ownedEntryMemoryCost + ", backupEntryMemoryCost=" + this.backupEntryMemoryCost + ", creationTime=" + this.creationTime + ", lockedEntryCount=" + this.lockedEntryCount + ", dirtyEntryCount=" + this.dirtyEntryCount + ", heapCost=" + this.heapCost + ", merkleTreesCost=" + this.merkleTreesCost + ", nearCacheStats=" + (this.nearCacheStats != null ? this.nearCacheStats : "") + ", queryCount=" + this.queryCount + ", indexedQueryCount=" + this.indexedQueryCount + ", indexStats=" + this.indexStats + '}';
    }
}

