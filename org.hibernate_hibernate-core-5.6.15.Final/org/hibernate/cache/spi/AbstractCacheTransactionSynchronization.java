/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.cache.spi.RegionFactory;

public abstract class AbstractCacheTransactionSynchronization
implements CacheTransactionSynchronization {
    private long lastTransactionCompletionTimestamp;
    private final RegionFactory regionFactory;

    public AbstractCacheTransactionSynchronization(RegionFactory regionFactory) {
        this.lastTransactionCompletionTimestamp = regionFactory.nextTimestamp();
        this.regionFactory = regionFactory;
    }

    @Override
    public long getCurrentTransactionStartTimestamp() {
        return this.lastTransactionCompletionTimestamp;
    }

    @Override
    public final void transactionJoined() {
        this.lastTransactionCompletionTimestamp = this.regionFactory.nextTimestamp();
    }

    @Override
    public final void transactionCompleting() {
    }

    @Override
    public void transactionCompleted(boolean successful) {
    }
}

