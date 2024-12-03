/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 */
package com.atlassian.confluence.impl.cache.tx;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;

public interface TransactionAwareCacheFactory {
    public <K, V> TransactionAwareCache<K, V> getTxCache(String var1);

    public <K, V> TransactionAwareCache<K, V> getTxCache(String var1, CacheLoader<K, V> var2);

    public static TransactionAwareCacheFactory from(final CacheFactory delegate) {
        return new TransactionAwareCacheFactory(){

            @Override
            public <K, V> TransactionAwareCache<K, V> getTxCache(String cacheName) {
                return TransactionAwareCache.from(delegate.getCache(cacheName));
            }

            @Override
            public <K, V> TransactionAwareCache<K, V> getTxCache(String cacheName, CacheLoader<K, V> loader) {
                return TransactionAwareCache.from(delegate.getCache(cacheName, loader));
            }
        };
    }
}

