/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.Cache;
import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.failures.ExponentialBackOffFailureCache;

public interface CacheFactory {
    public <K, V> Cache<V> createExpirationDateBasedCache(CacheLoader<K, V> var1);

    public <K, V> Cache<V> createExpirationDateBasedCache(CacheLoader<K, V> var1, ExponentialBackOffFailureCache var2);
}

