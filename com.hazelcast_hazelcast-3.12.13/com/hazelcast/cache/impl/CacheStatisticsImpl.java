/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.impl.CacheEntryCountResolver;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.util.ConcurrencyUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class CacheStatisticsImpl
implements CacheStatistics {
    protected static final float FLOAT_HUNDRED = 100.0f;
    protected static final long NANOSECONDS_IN_A_MICROSECOND = 1000L;
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> LAST_ACCESS_TIME = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "lastAccessTime");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> LAST_UPDATE_TIME = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "lastUpdateTime");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> REMOVALS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "removals");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> EXPIRIES = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "expiries");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> PUTS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "puts");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> HITS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "hits");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> MISSES = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "misses");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> EVICTIONS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "evictions");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> PUT_TIME_TAKEN_NANOS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "putTimeTakenNanos");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> GET_CACHE_TIME_TAKEN_NANOS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "getCacheTimeTakenNanos");
    protected static final AtomicLongFieldUpdater<CacheStatisticsImpl> REMOVE_TIME_TAKEN_NANOS = AtomicLongFieldUpdater.newUpdater(CacheStatisticsImpl.class, "removeTimeTakenNanos");
    protected long creationTime;
    protected volatile long lastAccessTime;
    protected volatile long lastUpdateTime;
    protected volatile long removals;
    protected volatile long expiries;
    protected volatile long puts;
    protected volatile long hits;
    protected volatile long misses;
    protected volatile long evictions;
    protected volatile long putTimeTakenNanos;
    protected volatile long getCacheTimeTakenNanos;
    protected volatile long removeTimeTakenNanos;
    protected final CacheEntryCountResolver cacheEntryCountResolver;

    public CacheStatisticsImpl(long creationTime) {
        this(creationTime, null);
    }

    public CacheStatisticsImpl(long creationTime, CacheEntryCountResolver cacheEntryCountResolver) {
        this.creationTime = creationTime;
        this.cacheEntryCountResolver = cacheEntryCountResolver;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
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
        if (this.cacheEntryCountResolver != null) {
            return this.cacheEntryCountResolver.getEntryCount();
        }
        return 0L;
    }

    @Override
    public long getCacheRemovals() {
        return this.removals;
    }

    public long getCacheExpiries() {
        return this.expiries;
    }

    @Override
    public long getCacheGets() {
        return this.getCacheHits() + this.getCacheMisses();
    }

    @Override
    public long getCachePuts() {
        return this.puts;
    }

    @Override
    public long getCacheHits() {
        return this.hits;
    }

    @Override
    public long getCacheMisses() {
        return this.misses;
    }

    @Override
    public long getCacheEvictions() {
        return this.evictions;
    }

    public long getCachePutTimeTakenNanos() {
        return this.putTimeTakenNanos;
    }

    public long getCacheGetTimeTakenNanos() {
        return this.getCacheTimeTakenNanos;
    }

    public long getCacheRemoveTimeTakenNanos() {
        return this.removeTimeTakenNanos;
    }

    @Override
    public float getCacheHitPercentage() {
        long cacheHits = this.getCacheHits();
        long cacheGets = this.getCacheGets();
        if (cacheHits == 0L || cacheGets == 0L) {
            return 0.0f;
        }
        return (float)cacheHits / (float)cacheGets * 100.0f;
    }

    @Override
    public float getCacheMissPercentage() {
        long cacheMisses = this.getCacheMisses();
        long cacheGets = this.getCacheGets();
        if (cacheMisses == 0L || cacheGets == 0L) {
            return 0.0f;
        }
        return (float)cacheMisses / (float)cacheGets * 100.0f;
    }

    @Override
    public float getAverageGetTime() {
        long cacheGetTimeTakenNanos = this.getCacheGetTimeTakenNanos();
        long cacheGets = this.getCacheGets();
        if (cacheGetTimeTakenNanos == 0L || cacheGets == 0L) {
            return 0.0f;
        }
        return 1.0f * (float)cacheGetTimeTakenNanos / (float)cacheGets / 1000.0f;
    }

    @Override
    public float getAveragePutTime() {
        long cachePutTimeTakenNanos = this.getCachePutTimeTakenNanos();
        long cachePuts = this.getCachePuts();
        if (cachePutTimeTakenNanos == 0L || cachePuts == 0L) {
            return 0.0f;
        }
        return 1.0f * (float)cachePutTimeTakenNanos / (float)cachePuts / 1000.0f;
    }

    @Override
    public float getAverageRemoveTime() {
        long cacheRemoveTimeTakenNanos = this.getCacheRemoveTimeTakenNanos();
        long cacheRemoves = this.getCacheRemovals();
        if (cacheRemoveTimeTakenNanos == 0L || cacheRemoves == 0L) {
            return 0.0f;
        }
        return 1.0f * (float)cacheRemoveTimeTakenNanos / (float)cacheRemoves / 1000.0f;
    }

    public void clear() {
        this.puts = 0L;
        this.misses = 0L;
        this.removals = 0L;
        this.expiries = 0L;
        this.hits = 0L;
        this.evictions = 0L;
        this.getCacheTimeTakenNanos = 0L;
        this.putTimeTakenNanos = 0L;
        this.removeTimeTakenNanos = 0L;
    }

    public void setLastAccessTime(long time) {
        ConcurrencyUtil.setMax(this, LAST_ACCESS_TIME, time);
    }

    public void setLastUpdateTime(long time) {
        ConcurrencyUtil.setMax(this, LAST_UPDATE_TIME, time);
    }

    public void increaseCacheRemovals() {
        REMOVALS.incrementAndGet(this);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCacheRemovals(long number) {
        REMOVALS.addAndGet(this, number);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCacheExpiries() {
        EXPIRIES.incrementAndGet(this);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCacheExpiries(long number) {
        EXPIRIES.addAndGet(this, number);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCachePuts() {
        PUTS.incrementAndGet(this);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCachePuts(long number) {
        PUTS.addAndGet(this, number);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCacheHits() {
        HITS.incrementAndGet(this);
        this.setLastAccessTime(System.currentTimeMillis());
    }

    public void increaseCacheHits(long number) {
        HITS.addAndGet(this, number);
        this.setLastAccessTime(System.currentTimeMillis());
    }

    public void increaseCacheMisses() {
        MISSES.incrementAndGet(this);
        this.setLastAccessTime(System.currentTimeMillis());
    }

    public void increaseCacheMisses(long number) {
        MISSES.addAndGet(this, number);
        this.setLastAccessTime(System.currentTimeMillis());
    }

    public void increaseCacheEvictions() {
        EVICTIONS.incrementAndGet(this);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void increaseCacheEvictions(long number) {
        EVICTIONS.addAndGet(this, number);
        this.setLastUpdateTime(System.currentTimeMillis());
    }

    public void addGetTimeNanos(long duration) {
        while (true) {
            long nanos;
            if ((nanos = this.getCacheTimeTakenNanos) <= Long.MAX_VALUE - duration) {
                if (!GET_CACHE_TIME_TAKEN_NANOS.compareAndSet(this, nanos, nanos + duration)) continue;
                return;
            }
            if (GET_CACHE_TIME_TAKEN_NANOS.compareAndSet(this, nanos, duration)) break;
        }
        this.clear();
    }

    public void addPutTimeNanos(long duration) {
        while (true) {
            long nanos;
            if ((nanos = this.putTimeTakenNanos) <= Long.MAX_VALUE - duration) {
                if (!PUT_TIME_TAKEN_NANOS.compareAndSet(this, nanos, nanos + duration)) continue;
                return;
            }
            if (PUT_TIME_TAKEN_NANOS.compareAndSet(this, nanos, duration)) break;
        }
        this.clear();
    }

    public void addRemoveTimeNanos(long duration) {
        while (true) {
            long nanos;
            if ((nanos = this.removeTimeTakenNanos) <= Long.MAX_VALUE - duration) {
                if (!REMOVE_TIME_TAKEN_NANOS.compareAndSet(this, nanos, nanos + duration)) continue;
                return;
            }
            if (REMOVE_TIME_TAKEN_NANOS.compareAndSet(this, nanos, duration)) break;
        }
        this.clear();
    }

    @Override
    public NearCacheStats getNearCacheStatistics() {
        throw new UnsupportedOperationException("Near Cache is not supported at server");
    }

    public String toString() {
        return "CacheStatisticsImpl{creationTime=" + this.creationTime + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", ownedEntryCount=" + this.getOwnedEntryCount() + ", removals=" + this.removals + ", expiries=" + this.expiries + ", puts=" + this.puts + ", hits=" + this.hits + ", misses=" + this.misses + ", evictions=" + this.evictions + ", putTimeTakenNanos=" + this.putTimeTakenNanos + ", getCacheTimeTakenNanos=" + this.getCacheTimeTakenNanos + ", removeTimeTakenNanos=" + this.removeTimeTakenNanos + '}';
    }
}

