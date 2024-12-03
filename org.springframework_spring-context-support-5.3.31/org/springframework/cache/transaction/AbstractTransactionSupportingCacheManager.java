/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cache.Cache
 *  org.springframework.cache.support.AbstractCacheManager
 */
package org.springframework.cache.transaction;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;

public abstract class AbstractTransactionSupportingCacheManager
extends AbstractCacheManager {
    private boolean transactionAware = false;

    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

    public boolean isTransactionAware() {
        return this.transactionAware;
    }

    protected Cache decorateCache(Cache cache) {
        return this.isTransactionAware() ? new TransactionAwareCacheDecorator(cache) : cache;
    }
}

