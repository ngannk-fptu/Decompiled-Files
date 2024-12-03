/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.ExtendedStatisticsSupport;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.stat.NaturalIdCacheStatistics;

@Deprecated
public class DeprecatedNaturalIdCacheStatisticsImpl
implements NaturalIdCacheStatistics,
Serializable {
    private final String regionName;
    private final transient Set<NaturalIdDataAccess> accessStrategies;
    private final AtomicLong executionCount = new AtomicLong();
    private final AtomicLong executionMaxTime = new AtomicLong();
    private final AtomicLong executionMinTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong totalExecutionTime = new AtomicLong();
    private final AtomicLong cacheHitCount = new AtomicLong();
    private final AtomicLong cacheMissCount = new AtomicLong();
    private final AtomicLong cachePutCount = new AtomicLong();
    private final Lock readLock;
    private final Lock writeLock;

    DeprecatedNaturalIdCacheStatisticsImpl(String regionName, Set<NaturalIdDataAccess> accessStrategies) {
        this.regionName = regionName;
        this.accessStrategies = accessStrategies;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public long getExecutionCount() {
        return this.executionCount.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getExecutionAvgTime() {
        this.writeLock.lock();
        try {
            long avgExecutionTime = 0L;
            if (this.executionCount.get() > 0L) {
                avgExecutionTime = this.totalExecutionTime.get() / this.executionCount.get();
            }
            long l = avgExecutionTime;
            return l;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public long getExecutionMaxTime() {
        return this.executionMaxTime.get();
    }

    @Override
    public long getExecutionMinTime() {
        return this.executionMinTime.get();
    }

    @Override
    public long getHitCount() {
        return this.cacheHitCount.get();
    }

    @Override
    public long getMissCount() {
        return this.cacheMissCount.get();
    }

    @Override
    public long getPutCount() {
        return this.cachePutCount.get();
    }

    @Override
    public long getElementCountInMemory() {
        long count = 0L;
        HashSet<DomainDataRegion> processedRegions = null;
        for (NaturalIdDataAccess accessStrategy : this.accessStrategies) {
            DomainDataRegion region = accessStrategy.getRegion();
            if (ExtendedStatisticsSupport.class.isInstance(region)) {
                // empty if block
            }
            if (!(region instanceof ExtendedStatisticsSupport)) continue;
            if (processedRegions == null) {
                processedRegions = new HashSet<DomainDataRegion>();
            }
            if (!processedRegions.add(region)) continue;
            count += ((ExtendedStatisticsSupport)((Object)region)).getElementCountInMemory();
        }
        if (count == 0L) {
            return Long.MIN_VALUE;
        }
        return count;
    }

    @Override
    public long getElementCountOnDisk() {
        long count = 0L;
        HashSet<DomainDataRegion> processedRegions = null;
        for (NaturalIdDataAccess accessStrategy : this.accessStrategies) {
            DomainDataRegion region = accessStrategy.getRegion();
            if (ExtendedStatisticsSupport.class.isInstance(region)) {
                // empty if block
            }
            if (!(region instanceof ExtendedStatisticsSupport)) continue;
            if (processedRegions == null) {
                processedRegions = new HashSet<DomainDataRegion>();
            }
            if (!processedRegions.add(region)) continue;
            count += ((ExtendedStatisticsSupport)((Object)region)).getElementCountOnDisk();
        }
        if (count == 0L) {
            return Long.MIN_VALUE;
        }
        return count;
    }

    @Override
    public long getSizeInMemory() {
        long count = 0L;
        HashSet<DomainDataRegion> processedRegions = null;
        for (NaturalIdDataAccess accessStrategy : this.accessStrategies) {
            DomainDataRegion region = accessStrategy.getRegion();
            if (ExtendedStatisticsSupport.class.isInstance(region)) {
                // empty if block
            }
            if (!(region instanceof ExtendedStatisticsSupport)) continue;
            if (processedRegions == null) {
                processedRegions = new HashSet<DomainDataRegion>();
            }
            if (!processedRegions.add(region)) continue;
            count += ((ExtendedStatisticsSupport)((Object)region)).getElementCountOnDisk();
        }
        if (count == 0L) {
            return Long.MIN_VALUE;
        }
        return count;
    }

    void incrementHitCount() {
        this.cacheHitCount.getAndIncrement();
    }

    void incrementMissCount() {
        this.cacheMissCount.getAndIncrement();
    }

    void incrementPutCount() {
        this.cachePutCount.getAndIncrement();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void queryExecuted(long time) {
        this.readLock.lock();
        try {
            long old = this.executionMinTime.get();
            while (time < old && !this.executionMinTime.compareAndSet(old, time)) {
                old = this.executionMinTime.get();
            }
            old = this.executionMaxTime.get();
            while (time > old && !this.executionMaxTime.compareAndSet(old, time)) {
                old = this.executionMaxTime.get();
            }
            this.executionCount.getAndIncrement();
            this.totalExecutionTime.addAndGet(time);
        }
        finally {
            this.readLock.unlock();
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append("NaturalIdCacheStatistics(deprecated)").append("[regionName=").append(this.regionName).append(",executionCount=").append(this.getExecutionCount()).append(",executionAvgTime=").append(this.getExecutionAvgTime()).append(",executionMinTime=").append(this.getExecutionMinTime()).append(",executionMaxTime=").append(this.getExecutionMaxTime());
        buf.append(",hitCount=").append(this.getHitCount()).append(",missCount=").append(this.getMissCount()).append(",putCount=").append(this.getPutCount()).append(",elementCountInMemory=").append(this.getElementCountInMemory()).append(",elementCountOnDisk=").append(this.getElementCountOnDisk()).append(",sizeInMemory=").append(this.getSizeInMemory());
        return buf.append(']').toString();
    }
}

