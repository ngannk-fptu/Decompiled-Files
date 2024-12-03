/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.cache.transaction;

import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class TransactionAwareCacheDecorator
implements Cache {
    private final Cache targetCache;

    public TransactionAwareCacheDecorator(Cache targetCache) {
        Assert.notNull((Object)targetCache, "Target Cache must not be null");
        this.targetCache = targetCache;
    }

    public Cache getTargetCache() {
        return this.targetCache;
    }

    @Override
    public String getName() {
        return this.targetCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return this.targetCache.getNativeCache();
    }

    @Override
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return this.targetCache.get(key);
    }

    @Override
    public <T> T get(Object key, @Nullable Class<T> type) {
        return this.targetCache.get(key, type);
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return this.targetCache.get(key, valueLoader);
    }

    @Override
    public void put(final Object key, final @Nullable Object value) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronization(){

                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.put(key, value);
                }
            });
        } else {
            this.targetCache.put(key, value);
        }
    }

    @Override
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return this.targetCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(final Object key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronization(){

                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.evict(key);
                }
            });
        } else {
            this.targetCache.evict(key);
        }
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return this.targetCache.evictIfPresent(key);
    }

    @Override
    public void clear() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronization(){

                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.clear();
                }
            });
        } else {
            this.targetCache.clear();
        }
    }

    @Override
    public boolean invalidate() {
        return this.targetCache.invalidate();
    }
}

