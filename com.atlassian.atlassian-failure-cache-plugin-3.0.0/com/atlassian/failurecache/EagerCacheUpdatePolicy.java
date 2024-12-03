/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.CacheUpdatePolicy;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.failurecache.failures.FailureCache;
import com.atlassian.failurecache.updates.EvictCacheEntryAction;
import com.atlassian.failurecache.updates.MutateCacheAction;
import com.atlassian.failurecache.updates.NoOpAction;
import com.atlassian.failurecache.updates.UpdateCacheEntryAction;
import com.atlassian.failurecache.util.date.Clock;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

public class EagerCacheUpdatePolicy<K, V>
implements CacheUpdatePolicy<K, V> {
    private final Clock clock;
    private final FailureCache<K> failureCache;

    public EagerCacheUpdatePolicy(Clock clock, FailureCache<K> failureCache) {
        this.clock = (Clock)Preconditions.checkNotNull((Object)clock);
        this.failureCache = (FailureCache)Preconditions.checkNotNull(failureCache);
    }

    @Override
    public boolean isUpdateRecommended(K key, ExpiringValue<V> value) {
        Preconditions.checkNotNull(value, (Object)"value");
        return (value.isStale(this.clock) || value.isExpired(this.clock)) && !this.failureCache.isFailing(key);
    }

    @Override
    public MutateCacheAction<K, V> evaluateResult(K key, ExpiringValue<V> oldValue, @Nullable ExpiringValue<V> newValue) {
        Preconditions.checkNotNull(key, (Object)"key");
        Preconditions.checkNotNull(oldValue, (Object)"oldValue");
        if (newValue != null && newValue.isValid(this.clock)) {
            this.failureCache.registerSuccess(key);
            return new UpdateCacheEntryAction<K, V>(key, oldValue, newValue);
        }
        this.failureCache.registerFailure(key);
        return oldValue.isExpired(this.clock) ? new EvictCacheEntryAction<K, V>(key, oldValue) : NoOpAction.instance();
    }
}

