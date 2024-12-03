/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cache.Cache
 *  org.springframework.cache.Cache$ValueWrapper
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
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
        Assert.notNull((Object)targetCache, (String)"Target Cache must not be null");
        this.targetCache = targetCache;
    }

    public Cache getTargetCache() {
        return this.targetCache;
    }

    public String getName() {
        return this.targetCache.getName();
    }

    public Object getNativeCache() {
        return this.targetCache.getNativeCache();
    }

    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return this.targetCache.get(key);
    }

    public <T> T get(Object key, @Nullable Class<T> type) {
        return (T)this.targetCache.get(key, type);
    }

    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T)this.targetCache.get(key, valueLoader);
    }

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

    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return this.targetCache.putIfAbsent(key, value);
    }

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

    public boolean evictIfPresent(Object key) {
        return this.targetCache.evictIfPresent(key);
    }

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

    public boolean invalidate() {
        return this.targetCache.invalidate();
    }
}

