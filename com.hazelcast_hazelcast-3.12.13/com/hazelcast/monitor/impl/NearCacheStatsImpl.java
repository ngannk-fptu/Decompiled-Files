/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class NearCacheStatsImpl
implements NearCacheStats {
    private static final double PERCENTAGE = 100.0;
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> OWNED_ENTRY_COUNT = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "ownedEntryCount");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> OWNED_ENTRY_MEMORY_COST = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "ownedEntryMemoryCost");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> HITS = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "hits");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> MISSES = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "misses");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> EVICTIONS = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "evictions");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> EXPIRATIONS = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "expirations");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> INVALIDATIONS = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "invalidations");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> INVALIDATION_REQUESTS = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "invalidationRequests");
    private static final AtomicLongFieldUpdater<NearCacheStatsImpl> PERSISTENCE_COUNT = AtomicLongFieldUpdater.newUpdater(NearCacheStatsImpl.class, "persistenceCount");
    @Probe
    private volatile long creationTime;
    @Probe
    private volatile long ownedEntryCount;
    @Probe
    private volatile long ownedEntryMemoryCost;
    @Probe
    private volatile long hits;
    @Probe
    private volatile long misses;
    @Probe
    private volatile long evictions;
    @Probe
    private volatile long expirations;
    @Probe
    private volatile long invalidations;
    @Probe
    private volatile long invalidationRequests;
    @Probe
    private volatile long persistenceCount;
    @Probe
    private volatile long lastPersistenceTime;
    @Probe
    private volatile long lastPersistenceDuration;
    @Probe
    private volatile long lastPersistenceWrittenBytes;
    @Probe
    private volatile long lastPersistenceKeyCount;
    private volatile String lastPersistenceFailure = "";

    public NearCacheStatsImpl() {
        this.creationTime = NearCacheStatsImpl.getNowInMillis();
    }

    public NearCacheStatsImpl(NearCacheStats nearCacheStats) {
        NearCacheStatsImpl stats = (NearCacheStatsImpl)nearCacheStats;
        this.creationTime = stats.creationTime;
        this.ownedEntryCount = stats.ownedEntryCount;
        this.ownedEntryMemoryCost = stats.ownedEntryMemoryCost;
        this.hits = stats.hits;
        this.misses = stats.misses;
        this.evictions = stats.evictions;
        this.expirations = stats.expirations;
        this.invalidations = stats.invalidations;
        this.invalidationRequests = stats.invalidationRequests;
        this.persistenceCount = stats.persistenceCount;
        this.lastPersistenceTime = stats.lastPersistenceTime;
        this.lastPersistenceDuration = stats.lastPersistenceDuration;
        this.lastPersistenceWrittenBytes = stats.lastPersistenceWrittenBytes;
        this.lastPersistenceKeyCount = stats.lastPersistenceKeyCount;
        this.lastPersistenceFailure = stats.lastPersistenceFailure;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getOwnedEntryCount() {
        return this.ownedEntryCount;
    }

    public void setOwnedEntryCount(long ownedEntryCount) {
        OWNED_ENTRY_COUNT.set(this, ownedEntryCount);
    }

    public void incrementOwnedEntryCount() {
        OWNED_ENTRY_COUNT.incrementAndGet(this);
    }

    public void decrementOwnedEntryCount() {
        OWNED_ENTRY_COUNT.decrementAndGet(this);
    }

    @Override
    public long getOwnedEntryMemoryCost() {
        return this.ownedEntryMemoryCost;
    }

    public void setOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
        OWNED_ENTRY_MEMORY_COST.set(this, ownedEntryMemoryCost);
    }

    public void incrementOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
        OWNED_ENTRY_MEMORY_COST.addAndGet(this, ownedEntryMemoryCost);
    }

    public void decrementOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
        OWNED_ENTRY_MEMORY_COST.addAndGet(this, -ownedEntryMemoryCost);
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    void setHits(long hits) {
        HITS.set(this, hits);
    }

    public void incrementHits() {
        HITS.incrementAndGet(this);
    }

    @Override
    public long getMisses() {
        return this.misses;
    }

    void setMisses(long misses) {
        MISSES.set(this, misses);
    }

    public void incrementMisses() {
        MISSES.incrementAndGet(this);
    }

    @Override
    public double getRatio() {
        if (this.misses == 0L) {
            if (this.hits == 0L) {
                return Double.NaN;
            }
            return Double.POSITIVE_INFINITY;
        }
        return (double)this.hits / (double)this.misses * 100.0;
    }

    @Override
    public long getEvictions() {
        return this.evictions;
    }

    public void incrementEvictions() {
        EVICTIONS.incrementAndGet(this);
    }

    @Override
    public long getExpirations() {
        return this.expirations;
    }

    public void incrementExpirations() {
        EXPIRATIONS.incrementAndGet(this);
    }

    @Override
    public long getInvalidations() {
        return this.invalidations;
    }

    public void incrementInvalidations() {
        INVALIDATIONS.incrementAndGet(this);
    }

    public void incrementInvalidations(long delta) {
        INVALIDATIONS.addAndGet(this, delta);
    }

    public long getInvalidationRequests() {
        return this.invalidationRequests;
    }

    public void incrementInvalidationRequests() {
        INVALIDATION_REQUESTS.incrementAndGet(this);
    }

    public void resetInvalidationEvents() {
        INVALIDATION_REQUESTS.set(this, 0L);
    }

    @Override
    public long getPersistenceCount() {
        return this.persistenceCount;
    }

    public void addPersistence(long duration, int writtenBytes, int keyCount) {
        PERSISTENCE_COUNT.incrementAndGet(this);
        this.lastPersistenceTime = NearCacheStatsImpl.getNowInMillis();
        this.lastPersistenceDuration = duration;
        this.lastPersistenceWrittenBytes = writtenBytes;
        this.lastPersistenceKeyCount = keyCount;
        this.lastPersistenceFailure = "";
    }

    public void addPersistenceFailure(Throwable t) {
        PERSISTENCE_COUNT.incrementAndGet(this);
        this.lastPersistenceTime = NearCacheStatsImpl.getNowInMillis();
        this.lastPersistenceDuration = 0L;
        this.lastPersistenceWrittenBytes = 0L;
        this.lastPersistenceKeyCount = 0L;
        this.lastPersistenceFailure = t.getClass().getSimpleName() + ": " + t.getMessage();
    }

    private static long getNowInMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long getLastPersistenceTime() {
        return this.lastPersistenceTime;
    }

    @Override
    public long getLastPersistenceDuration() {
        return this.lastPersistenceDuration;
    }

    @Override
    public long getLastPersistenceWrittenBytes() {
        return this.lastPersistenceWrittenBytes;
    }

    @Override
    public long getLastPersistenceKeyCount() {
        return this.lastPersistenceKeyCount;
    }

    @Override
    public String getLastPersistenceFailure() {
        return this.lastPersistenceFailure;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("ownedEntryCount", this.ownedEntryCount);
        root.add("ownedEntryMemoryCost", this.ownedEntryMemoryCost);
        root.add("creationTime", this.creationTime);
        root.add("hits", this.hits);
        root.add("misses", this.misses);
        root.add("evictions", this.evictions);
        root.add("expirations", this.expirations);
        root.add("invalidations", this.invalidations);
        root.add("invalidationEvents", this.invalidationRequests);
        root.add("persistenceCount", this.persistenceCount);
        root.add("lastPersistenceTime", this.lastPersistenceTime);
        root.add("lastPersistenceDuration", this.lastPersistenceDuration);
        root.add("lastPersistenceWrittenBytes", this.lastPersistenceWrittenBytes);
        root.add("lastPersistenceKeyCount", this.lastPersistenceKeyCount);
        root.add("lastPersistenceFailure", this.lastPersistenceFailure);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.ownedEntryCount = JsonUtil.getLong(json, "ownedEntryCount", -1L);
        this.ownedEntryMemoryCost = JsonUtil.getLong(json, "ownedEntryMemoryCost", -1L);
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.hits = JsonUtil.getLong(json, "hits", -1L);
        this.misses = JsonUtil.getLong(json, "misses", -1L);
        this.evictions = JsonUtil.getLong(json, "evictions", -1L);
        this.expirations = JsonUtil.getLong(json, "expirations", -1L);
        this.invalidations = JsonUtil.getLong(json, "invalidations", -1L);
        this.invalidationRequests = JsonUtil.getLong(json, "invalidationEvents", -1L);
        this.persistenceCount = JsonUtil.getLong(json, "persistenceCount", -1L);
        this.lastPersistenceTime = JsonUtil.getLong(json, "lastPersistenceTime", -1L);
        this.lastPersistenceDuration = JsonUtil.getLong(json, "lastPersistenceDuration", -1L);
        this.lastPersistenceWrittenBytes = JsonUtil.getLong(json, "lastPersistenceWrittenBytes", -1L);
        this.lastPersistenceKeyCount = JsonUtil.getLong(json, "lastPersistenceKeyCount", -1L);
        this.lastPersistenceFailure = JsonUtil.getString(json, "lastPersistenceFailure", "");
    }

    public String toString() {
        return "NearCacheStatsImpl{ownedEntryCount=" + this.ownedEntryCount + ", ownedEntryMemoryCost=" + this.ownedEntryMemoryCost + ", creationTime=" + this.creationTime + ", hits=" + this.hits + ", misses=" + this.misses + ", ratio=" + String.format("%.1f%%", this.getRatio()) + ", evictions=" + this.evictions + ", expirations=" + this.expirations + ", invalidations=" + this.invalidations + ", lastPersistenceTime=" + this.lastPersistenceTime + ", persistenceCount=" + this.persistenceCount + ", lastPersistenceDuration=" + this.lastPersistenceDuration + ", lastPersistenceWrittenBytes=" + this.lastPersistenceWrittenBytes + ", lastPersistenceKeyCount=" + this.lastPersistenceKeyCount + ", lastPersistenceFailure='" + this.lastPersistenceFailure + "'" + '}';
    }
}

