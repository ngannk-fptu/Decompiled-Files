/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cache;

import com.atlassian.confluence.cache.Deferred;
import com.atlassian.confluence.cache.DeferredOperationsCache;
import com.atlassian.confluence.cache.TransactionalCacheFactory;
import com.atlassian.confluence.impl.cache.CacheFlusher;

class TransactionalCacheFactoryFlusher
implements CacheFlusher {
    private final TransactionalCacheFactory transactionalCacheFactory;

    TransactionalCacheFactoryFlusher(TransactionalCacheFactory transactionalCacheFactory) {
        this.transactionalCacheFactory = transactionalCacheFactory;
    }

    static TransactionalCacheFactoryFlusher createTransactionalCacheFactoryFlusher(TransactionalCacheFactory transactionalCacheFactory) {
        return new TransactionalCacheFactoryFlusher(transactionalCacheFactory);
    }

    @Override
    public void flushCaches() {
        for (Deferred deferred : this.transactionalCacheFactory.getDeferreds()) {
            if (!(deferred instanceof DeferredOperationsCache)) continue;
            ((DeferredOperationsCache)deferred).removeAll();
        }
    }
}

