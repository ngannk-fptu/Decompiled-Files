/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalIndexStats;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.monitor.impl.NearCacheStatsImpl;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.JsonUtil;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalReplicatedMapStatsImpl
implements LocalReplicatedMapStats {
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> LAST_ACCESS_TIME = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "lastAccessTime");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> LAST_UPDATE_TIME = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "lastUpdateTime");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> HITS = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "hits");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> NUMBER_OF_OTHER_OPERATIONS = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "numberOfOtherOperations");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> NUMBER_OF_EVENTS = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "numberOfEvents");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> GET_COUNT = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "getCount");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> PUT_COUNT = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "putCount");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> REMOVE_COUNT = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "removeCount");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> TOTAL_GET_LATENCIES = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "totalGetLatencies");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> TOTAL_PUT_LATENCIES = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "totalPutLatencies");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> TOTAL_REMOVE_LATENCIES = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "totalRemoveLatencies");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> MAX_GET_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "maxGetLatency");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> MAX_PUT_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "maxPutLatency");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> MAX_REMOVE_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "maxRemoveLatency");
    private static final AtomicLongFieldUpdater<LocalReplicatedMapStatsImpl> OWNED_ENTRY_MEMORY_COST = AtomicLongFieldUpdater.newUpdater(LocalReplicatedMapStatsImpl.class, "ownedEntryMemoryCost");
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
    @Probe
    private volatile long totalGetLatencies;
    @Probe
    private volatile long totalPutLatencies;
    @Probe
    private volatile long totalRemoveLatencies;
    @Probe
    private volatile long maxGetLatency;
    @Probe
    private volatile long maxPutLatency;
    @Probe
    private volatile long maxRemoveLatency;
    @Probe
    private volatile long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long ownedEntryCount;
    @Probe
    private volatile long ownedEntryMemoryCost;

    @Override
    public long getOwnedEntryCount() {
        return this.ownedEntryCount;
    }

    public void setOwnedEntryCount(long ownedEntryCount) {
        this.ownedEntryCount = ownedEntryCount;
    }

    @Override
    public long getBackupEntryCount() {
        return 0L;
    }

    public void setBackupEntryCount(long backupEntryCount) {
    }

    @Override
    public int getBackupCount() {
        return 0;
    }

    public void setBackupCount(int backupCount) {
    }

    @Override
    public long getOwnedEntryMemoryCost() {
        return this.ownedEntryMemoryCost;
    }

    public void setOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
        OWNED_ENTRY_MEMORY_COST.set(this, ownedEntryMemoryCost);
    }

    @Override
    public long getBackupEntryMemoryCost() {
        return 0L;
    }

    public void setBackupEntryMemoryCost(long backupEntryMemoryCost) {
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
        HITS.set(this, hits);
    }

    @Override
    public long getLockedEntryCount() {
        return 0L;
    }

    public void setLockedEntryCount(long lockedEntryCount) {
    }

    @Override
    public long getDirtyEntryCount() {
        return 0L;
    }

    public void setDirtyEntryCount(long dirtyEntryCount) {
    }

    @Override
    @Probe
    public long total() {
        return this.putCount + this.getCount + this.removeCount + this.numberOfOtherOperations;
    }

    @Override
    public long getPutOperationCount() {
        return this.putCount;
    }

    public void incrementPuts(long latency) {
        PUT_COUNT.incrementAndGet(this);
        TOTAL_PUT_LATENCIES.addAndGet(this, latency);
        ConcurrencyUtil.setMax(this, MAX_PUT_LATENCY, latency);
    }

    @Override
    public long getGetOperationCount() {
        return this.getCount;
    }

    public void incrementGets(long latency) {
        GET_COUNT.incrementAndGet(this);
        TOTAL_GET_LATENCIES.addAndGet(this, latency);
        ConcurrencyUtil.setMax(this, MAX_GET_LATENCY, latency);
    }

    @Override
    public long getRemoveOperationCount() {
        return this.removeCount;
    }

    public void incrementRemoves(long latency) {
        REMOVE_COUNT.incrementAndGet(this);
        TOTAL_REMOVE_LATENCIES.addAndGet(this, latency);
        ConcurrencyUtil.setMax(this, MAX_REMOVE_LATENCY, latency);
    }

    @Override
    public long getTotalPutLatency() {
        return this.totalPutLatencies;
    }

    @Override
    public long getTotalGetLatency() {
        return this.totalGetLatencies;
    }

    @Override
    public long getTotalRemoveLatency() {
        return this.totalRemoveLatencies;
    }

    @Override
    public long getMaxPutLatency() {
        return this.maxPutLatency;
    }

    @Override
    public long getMaxGetLatency() {
        return this.maxGetLatency;
    }

    @Override
    public long getMaxRemoveLatency() {
        return this.maxRemoveLatency;
    }

    @Override
    public long getOtherOperationCount() {
        return this.numberOfOtherOperations;
    }

    public void incrementOtherOperations() {
        NUMBER_OF_OTHER_OPERATIONS.incrementAndGet(this);
    }

    @Override
    public long getEventOperationCount() {
        return this.numberOfEvents;
    }

    public void incrementReceivedEvents() {
        NUMBER_OF_EVENTS.incrementAndGet(this);
    }

    @Override
    public long getHeapCost() {
        return 0L;
    }

    public void setHeapCost(long heapCost) {
    }

    @Override
    public long getMerkleTreesCost() {
        return 0L;
    }

    public void setMerkleTreesCost(long merkleTreesCost) {
    }

    @Override
    @Probe
    public long getReplicationEventCount() {
        return 0L;
    }

    @Override
    public NearCacheStatsImpl getNearCacheStats() {
        throw new UnsupportedOperationException("Replicated map has no Near Cache!");
    }

    @Override
    public long getQueryCount() {
        throw new UnsupportedOperationException("Queries on replicated maps are not supported.");
    }

    @Override
    public long getIndexedQueryCount() {
        throw new UnsupportedOperationException("Queries on replicated maps are not supported.");
    }

    @Override
    public Map<String, LocalIndexStats> getIndexStats() {
        throw new UnsupportedOperationException("Queries on replicated maps are not supported.");
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
        root.add("ownedEntryMemoryCost", this.ownedEntryMemoryCost);
        root.add("creationTime", this.creationTime);
        root.add("totalGetLatencies", this.totalGetLatencies);
        root.add("totalPutLatencies", this.totalPutLatencies);
        root.add("totalRemoveLatencies", this.totalRemoveLatencies);
        root.add("maxGetLatency", this.maxGetLatency);
        root.add("maxPutLatency", this.maxPutLatency);
        root.add("maxRemoveLatency", this.maxRemoveLatency);
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
        this.hits = JsonUtil.getLong(json, "hits", -1L);
        this.ownedEntryCount = JsonUtil.getLong(json, "ownedEntryCount", -1L);
        this.ownedEntryMemoryCost = JsonUtil.getLong(json, "ownedEntryMemoryCost", -1L);
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.totalGetLatencies = JsonUtil.getLong(json, "totalGetLatencies", -1L);
        this.totalPutLatencies = JsonUtil.getLong(json, "totalPutLatencies", -1L);
        this.totalRemoveLatencies = JsonUtil.getLong(json, "totalRemoveLatencies", -1L);
        this.maxGetLatency = JsonUtil.getLong(json, "maxGetLatency", -1L);
        this.maxPutLatency = JsonUtil.getLong(json, "maxPutLatency", -1L);
        this.maxRemoveLatency = JsonUtil.getLong(json, "maxRemoveLatency", -1L);
    }

    public String toString() {
        return "LocalReplicatedMapStatsImpl{lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", hits=" + this.hits + ", numberOfOtherOperations=" + this.numberOfOtherOperations + ", numberOfEvents=" + this.numberOfEvents + ", getCount=" + this.getCount + ", putCount=" + this.putCount + ", removeCount=" + this.removeCount + ", totalGetLatencies=" + this.totalGetLatencies + ", totalPutLatencies=" + this.totalPutLatencies + ", totalRemoveLatencies=" + this.totalRemoveLatencies + ", ownedEntryCount=" + this.ownedEntryCount + ", ownedEntryMemoryCost=" + this.ownedEntryMemoryCost + ", creationTime=" + this.creationTime + '}';
    }
}

