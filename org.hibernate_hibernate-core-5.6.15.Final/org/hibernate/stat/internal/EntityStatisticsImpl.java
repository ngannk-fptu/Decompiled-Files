/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.internal.AbstractCacheableDataStatistics;

public class EntityStatisticsImpl
extends AbstractCacheableDataStatistics
implements EntityStatistics,
Serializable {
    private final String rootEntityName;
    private final LongAdder loadCount = new LongAdder();
    private final LongAdder updateCount = new LongAdder();
    private final LongAdder insertCount = new LongAdder();
    private final LongAdder deleteCount = new LongAdder();
    private final LongAdder fetchCount = new LongAdder();
    private final LongAdder optimisticFailureCount = new LongAdder();

    EntityStatisticsImpl(EntityPersister rootEntityDescriptor) {
        super(() -> rootEntityDescriptor.getCacheAccessStrategy() != null ? rootEntityDescriptor.getCacheAccessStrategy().getRegion() : null);
        this.rootEntityName = rootEntityDescriptor.getRootEntityName();
    }

    @Override
    public long getDeleteCount() {
        return this.deleteCount.sum();
    }

    @Override
    public long getInsertCount() {
        return this.insertCount.sum();
    }

    @Override
    public long getLoadCount() {
        return this.loadCount.sum();
    }

    @Override
    public long getUpdateCount() {
        return this.updateCount.sum();
    }

    @Override
    public long getFetchCount() {
        return this.fetchCount.sum();
    }

    @Override
    public long getOptimisticFailureCount() {
        return this.optimisticFailureCount.sum();
    }

    void incrementLoadCount() {
        this.loadCount.increment();
    }

    void incrementFetchCount() {
        this.fetchCount.increment();
    }

    void incrementUpdateCount() {
        this.updateCount.increment();
    }

    void incrementInsertCount() {
        this.insertCount.increment();
    }

    void incrementDeleteCount() {
        this.deleteCount.increment();
    }

    void incrementOptimisticFailureCount() {
        this.optimisticFailureCount.increment();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder().append("EntityStatistics").append("[rootEntityName=").append(this.rootEntityName).append(",loadCount=").append(this.loadCount).append(",updateCount=").append(this.updateCount).append(",insertCount=").append(this.insertCount).append(",deleteCount=").append(this.deleteCount).append(",fetchCount=").append(this.fetchCount).append(",optimisticLockFailureCount=").append(this.optimisticFailureCount);
        this.appendCacheStats(buffer);
        return buffer.append(']').toString();
    }
}

