/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.FutureCallback
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.CacheUpdatePolicy;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.failurecache.MutableCache;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheUpdateCallback<K, V>
implements FutureCallback<ExpiringValue<V>> {
    private static final Logger logger = LoggerFactory.getLogger(CacheUpdateCallback.class);
    private final K key;
    private final ExpiringValue<V> oldValue;
    private final MutableCache<K, V> cache;
    private final CacheUpdatePolicy<K, V> cacheUpdatePolicy;

    public CacheUpdateCallback(K key, ExpiringValue<V> oldValue, MutableCache<K, V> cache, CacheUpdatePolicy<K, V> cacheUpdatePolicy) {
        this.key = Preconditions.checkNotNull(key);
        this.oldValue = (ExpiringValue)Preconditions.checkNotNull(oldValue);
        this.cache = (MutableCache)Preconditions.checkNotNull(cache);
        this.cacheUpdatePolicy = (CacheUpdatePolicy)Preconditions.checkNotNull(cacheUpdatePolicy);
    }

    public void onSuccess(ExpiringValue<V> result) {
        this.handleResponse(result);
    }

    public void onFailure(Throwable t) {
        logger.debug(String.format("Exception raised while refreshing cache entry with key '%s'; treating this as a null result.", this.key), t);
        this.handleResponse(null);
    }

    private void handleResponse(@Nullable ExpiringValue<V> result) {
        this.cacheUpdatePolicy.evaluateResult(this.key, this.oldValue, result).apply(this.cache);
    }
}

