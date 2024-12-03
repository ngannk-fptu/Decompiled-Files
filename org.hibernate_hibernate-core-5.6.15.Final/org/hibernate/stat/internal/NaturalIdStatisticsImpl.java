/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.NaturalIdStatistics;
import org.hibernate.stat.internal.AbstractCacheableDataStatistics;

public class NaturalIdStatisticsImpl
extends AbstractCacheableDataStatistics
implements NaturalIdStatistics,
Serializable {
    private final String rootEntityName;
    private final AtomicLong executionCount = new AtomicLong();
    private final AtomicLong executionMaxTime = new AtomicLong();
    private final AtomicLong executionMinTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong totalExecutionTime = new AtomicLong();
    private final Lock readLock;
    private final Lock writeLock;

    NaturalIdStatisticsImpl(EntityPersister rootEntityDescriptor) {
        super(() -> rootEntityDescriptor.getNaturalIdCacheAccessStrategy() != null ? rootEntityDescriptor.getNaturalIdCacheAccessStrategy().getRegion() : null);
        this.rootEntityName = rootEntityDescriptor.getRootEntityName();
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
        StringBuilder buf = new StringBuilder().append("NaturalIdCacheStatistics").append("[rootEntityName=").append(this.rootEntityName).append(",executionCount=").append(this.executionCount).append(",executionAvgTime=").append(this.getExecutionAvgTime()).append(",executionMinTime=").append(this.executionMinTime).append(",executionMaxTime=").append(this.executionMaxTime);
        this.appendCacheStats(buf);
        return buf.append(']').toString();
    }
}

