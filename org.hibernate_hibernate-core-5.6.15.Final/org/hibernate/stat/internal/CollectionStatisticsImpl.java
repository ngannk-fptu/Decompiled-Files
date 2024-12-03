/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.internal.AbstractCacheableDataStatistics;

public class CollectionStatisticsImpl
extends AbstractCacheableDataStatistics
implements CollectionStatistics,
Serializable {
    private final String collectionRole;
    private final LongAdder loadCount = new LongAdder();
    private final LongAdder fetchCount = new LongAdder();
    private final LongAdder updateCount = new LongAdder();
    private final LongAdder removeCount = new LongAdder();
    private final LongAdder recreateCount = new LongAdder();

    CollectionStatisticsImpl(CollectionPersister persister) {
        super(() -> persister.getCacheAccessStrategy() != null ? persister.getCacheAccessStrategy().getRegion() : null);
        this.collectionRole = persister.getRole();
    }

    @Override
    public long getLoadCount() {
        return this.loadCount.sum();
    }

    @Override
    public long getFetchCount() {
        return this.fetchCount.sum();
    }

    @Override
    public long getRecreateCount() {
        return this.recreateCount.sum();
    }

    @Override
    public long getRemoveCount() {
        return this.removeCount.sum();
    }

    @Override
    public long getUpdateCount() {
        return this.updateCount.sum();
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

    void incrementRecreateCount() {
        this.recreateCount.increment();
    }

    void incrementRemoveCount() {
        this.removeCount.increment();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder().append("CollectionStatistics").append("[collectionRole=").append(this.collectionRole).append(",loadCount=").append(this.loadCount).append(",fetchCount=").append(this.fetchCount).append(",recreateCount=").append(this.recreateCount).append(",removeCount=").append(this.removeCount).append(",updateCount=").append(this.updateCount);
        this.appendCacheStats(buffer);
        return buffer.append(']').toString();
    }
}

