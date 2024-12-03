/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;
import org.hibernate.cache.spi.ExtendedStatisticsSupport;
import org.hibernate.cache.spi.Region;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;

public class CacheRegionStatisticsImpl
implements CacheRegionStatistics,
SecondLevelCacheStatistics,
Serializable {
    private final transient Region region;
    private final LongAdder hitCount = new LongAdder();
    private final LongAdder missCount = new LongAdder();
    private final LongAdder putCount = new LongAdder();

    CacheRegionStatisticsImpl(Region region) {
        this.region = region;
    }

    @Override
    public String getRegionName() {
        return this.region.getName();
    }

    @Override
    public long getHitCount() {
        return this.hitCount.sum();
    }

    @Override
    public long getMissCount() {
        return this.missCount.sum();
    }

    @Override
    public long getPutCount() {
        return this.putCount.sum();
    }

    @Override
    public long getElementCountInMemory() {
        if (this.region instanceof ExtendedStatisticsSupport) {
            return ((ExtendedStatisticsSupport)((Object)this.region)).getElementCountInMemory();
        }
        return Long.MIN_VALUE;
    }

    @Override
    public long getElementCountOnDisk() {
        if (this.region instanceof ExtendedStatisticsSupport) {
            return ((ExtendedStatisticsSupport)((Object)this.region)).getElementCountOnDisk();
        }
        return Long.MIN_VALUE;
    }

    @Override
    public long getSizeInMemory() {
        if (this.region instanceof ExtendedStatisticsSupport) {
            return ((ExtendedStatisticsSupport)((Object)this.region)).getSizeInMemory();
        }
        return Long.MIN_VALUE;
    }

    void incrementHitCount() {
        this.hitCount.increment();
    }

    void incrementMissCount() {
        this.missCount.increment();
    }

    void incrementPutCount() {
        this.putCount.increment();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append("CacheRegionStatistics").append("[region=").append(this.region.getName()).append(",hitCount=").append(this.hitCount).append(",missCount=").append(this.missCount).append(",putCount=").append(this.putCount).append(",elementCountInMemory=").append(this.getElementCountInMemory()).append(",elementCountOnDisk=").append(this.getElementCountOnDisk()).append(",sizeInMemory=").append(this.getSizeInMemory()).append(']');
        return buf.toString();
    }
}

