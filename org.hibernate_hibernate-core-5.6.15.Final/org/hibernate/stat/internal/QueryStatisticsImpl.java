/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hibernate.stat.QueryStatistics;

public class QueryStatisticsImpl
implements QueryStatistics {
    private final String query;
    private final LongAdder cacheHitCount = new LongAdder();
    private final LongAdder cacheMissCount = new LongAdder();
    private final LongAdder cachePutCount = new LongAdder();
    private final LongAdder executionCount = new LongAdder();
    private final LongAdder executionRowCount = new LongAdder();
    private final AtomicLong executionMaxTime = new AtomicLong();
    private final AtomicLong executionMinTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong totalExecutionTime = new AtomicLong();
    private final LongAdder planCacheHitCount = new LongAdder();
    private final LongAdder planCacheMissCount = new LongAdder();
    private final AtomicLong planCompilationTotalMicroseconds = new AtomicLong();
    private final Lock readLock;
    private final Lock writeLock;

    QueryStatisticsImpl(String query) {
        this.query = query;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public long getExecutionCount() {
        return this.executionCount.sum();
    }

    @Override
    public long getCacheHitCount() {
        return this.cacheHitCount.sum();
    }

    @Override
    public long getCachePutCount() {
        return this.cachePutCount.sum();
    }

    @Override
    public long getCacheMissCount() {
        return this.cacheMissCount.sum();
    }

    @Override
    public long getExecutionRowCount() {
        return this.executionRowCount.sum();
    }

    @Override
    public long getExecutionAvgTime() {
        return (long)this.getExecutionAvgTimeAsDouble();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double getExecutionAvgTimeAsDouble() {
        this.writeLock.lock();
        try {
            double avgExecutionTime = 0.0;
            long ec = this.executionCount.sum();
            if (ec > 0L) {
                avgExecutionTime = (double)this.totalExecutionTime.get() / (double)ec;
            }
            double d = avgExecutionTime;
            return d;
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
    public long getExecutionTotalTime() {
        return this.totalExecutionTime.get();
    }

    @Override
    public long getPlanCacheHitCount() {
        return this.planCacheHitCount.sum();
    }

    @Override
    public long getPlanCacheMissCount() {
        return this.planCacheMissCount.sum();
    }

    @Override
    public long getPlanCompilationTotalMicroseconds() {
        return this.planCompilationTotalMicroseconds.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void executed(long rows, long time) {
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
            this.executionCount.increment();
            this.executionRowCount.add(rows);
            this.totalExecutionTime.addAndGet(time);
        }
        finally {
            this.readLock.unlock();
        }
    }

    void compiled(long microseconds) {
        this.planCacheMissCount.increment();
        this.planCompilationTotalMicroseconds.addAndGet(microseconds);
    }

    void incrementCacheHitCount() {
        this.cacheHitCount.increment();
    }

    void incrementCacheMissCount() {
        this.cacheMissCount.increment();
    }

    void incrementCachePutCount() {
        this.cachePutCount.increment();
    }

    void incrementPlanCacheHitCount() {
        this.planCacheHitCount.increment();
    }

    void incrementPlanCacheMissCount() {
        this.planCacheMissCount.increment();
    }

    public String toString() {
        return "QueryStatistics[query=" + this.query + ",cacheHitCount=" + this.cacheHitCount + ",cacheMissCount=" + this.cacheMissCount + ",cachePutCount=" + this.cachePutCount + ",planCacheHitCount=" + this.planCacheHitCount + ",planCacheMissCount=" + this.planCacheMissCount + ",executionCount=" + this.executionCount + ",executionRowCount=" + this.executionRowCount + ",executionAvgTime=" + this.getExecutionAvgTime() + ",executionMaxTime=" + this.executionMaxTime + ",executionMinTime=" + this.executionMinTime + ']';
    }
}

