/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;
import org.hibernate.cache.spi.Region;
import org.hibernate.stat.CacheableDataStatistics;

public abstract class AbstractCacheableDataStatistics
implements CacheableDataStatistics {
    private final String cacheRegionName;
    private final LongAdder cacheHitCount;
    private final LongAdder cacheMissCount;
    private final LongAdder cachePutCount;

    public AbstractCacheableDataStatistics(Supplier<Region> regionSupplier) {
        Region region = regionSupplier.get();
        if (region == null) {
            this.cacheRegionName = null;
            this.cacheHitCount = null;
            this.cacheMissCount = null;
            this.cachePutCount = null;
        } else {
            this.cacheRegionName = region.getName();
            this.cacheHitCount = new LongAdder();
            this.cacheMissCount = new LongAdder();
            this.cachePutCount = new LongAdder();
        }
    }

    @Override
    public String getCacheRegionName() {
        return this.cacheRegionName;
    }

    @Override
    public long getCacheHitCount() {
        if (this.cacheRegionName == null) {
            return Long.MIN_VALUE;
        }
        return this.cacheHitCount.sum();
    }

    @Override
    public long getCachePutCount() {
        if (this.cacheRegionName == null) {
            return Long.MIN_VALUE;
        }
        return this.cachePutCount.sum();
    }

    @Override
    public long getCacheMissCount() {
        if (this.cacheRegionName == null) {
            return Long.MIN_VALUE;
        }
        return this.cacheMissCount.sum();
    }

    public void incrementCacheHitCount() {
        if (this.cacheRegionName == null) {
            throw new IllegalStateException("Illegal attempt to increment cache hit count for non-cached data");
        }
        this.cacheHitCount.increment();
    }

    public void incrementCacheMissCount() {
        if (this.cacheRegionName == null) {
            throw new IllegalStateException("Illegal attempt to increment cache miss count for non-cached data");
        }
        this.cacheMissCount.increment();
    }

    public void incrementCachePutCount() {
        if (this.cacheRegionName == null) {
            throw new IllegalStateException("Illegal attempt to increment cache put count for non-cached data");
        }
        this.cachePutCount.increment();
    }

    protected void appendCacheStats(StringBuilder buf) {
        buf.append(",cacheRegion=").append(this.cacheRegionName);
        if (this.cacheRegionName == null) {
            return;
        }
        buf.append(",cacheHitCount=").append(this.getCacheHitCount()).append(",cacheMissCount=").append(this.getCacheMissCount()).append(",cachePutCount=").append(this.getCachePutCount());
    }
}

