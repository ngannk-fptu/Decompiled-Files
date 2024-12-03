/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.Cache;
import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.CacheUpdateCallback;
import com.atlassian.failurecache.CacheUpdatePolicy;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.failurecache.MutableCache;
import com.atlassian.failurecache.PlaceholderFuture;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpirationDateBasedCacheImpl<K, V>
implements Cache<V>,
MutableCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(ExpirationDateBasedCacheImpl.class);
    private final ConcurrentHashMap<K, ExpiringValue<V>> cache = new ConcurrentHashMap();
    private final ConcurrentHashMap<K, ListenableFuture<ExpiringValue<V>>> runningUpdates = new ConcurrentHashMap();
    private final CacheLoader<K, V> loader;
    private final CacheUpdatePolicy<K, V> cacheUpdatePolicy;

    public ExpirationDateBasedCacheImpl(CacheLoader<K, V> loader, CacheUpdatePolicy<K, V> cacheUpdatePolicy) {
        this.loader = loader;
        this.cacheUpdatePolicy = cacheUpdatePolicy;
    }

    @Override
    public Iterable<V> getValues() {
        return Iterables.filter((Iterable)Iterables.transform(this.cache.values(), ExpiringValue.extractValue()), (Predicate)Predicates.notNull());
    }

    @Override
    public boolean remove(K key, ExpiringValue<V> value) {
        Preconditions.checkNotNull(key, (Object)"key");
        Preconditions.checkNotNull(value, (Object)"value");
        return this.cache.remove(key, value);
    }

    @Override
    public boolean replace(K key, ExpiringValue<V> oldValue, ExpiringValue<V> newValue) {
        Preconditions.checkNotNull(key, (Object)"key");
        Preconditions.checkNotNull(oldValue, (Object)"oldValue");
        Preconditions.checkNotNull(newValue, (Object)"newValue");
        return this.cache.replace(key, oldValue, newValue);
    }

    @Override
    public void clear() {
        this.invalidateExistingCacheValues();
    }

    @Override
    public ListenableFuture<?> refresh() {
        this.updateCacheKeys();
        this.updateCacheValues();
        this.removeFinishedUpdates();
        return Futures.successfulAsList(this.runningUpdates.values());
    }

    private void updateCacheKeys() {
        ImmutableSet<K> updatedKeySet = this.loader.getKeys();
        this.removeVanishedCacheEntries(updatedKeySet);
        this.addNewCacheEntries(updatedKeySet);
    }

    private void updateCacheValues() {
        for (Map.Entry<K, ExpiringValue<V>> entry : this.cache.entrySet()) {
            if (!this.cacheUpdatePolicy.isUpdateRecommended(entry.getKey(), entry.getValue())) continue;
            this.startCacheValueUpdate(entry.getKey(), entry.getValue());
        }
    }

    private void removeVanishedCacheEntries(ImmutableSet<K> updatedKeySet) {
        Sets.SetView vanishedCacheKeys = Sets.difference((Set)this.cache.keySet(), updatedKeySet);
        for (Object vanishedCacheKey : vanishedCacheKeys) {
            this.cache.remove(vanishedCacheKey);
            this.removeAndCancelRunningFutures(vanishedCacheKey);
        }
        Sets.SetView vanishedRunningUpdateKeys = Sets.difference((Set)this.runningUpdates.keySet(), updatedKeySet);
        for (Object vanishedRunningUpdateKey : vanishedRunningUpdateKeys) {
            this.removeAndCancelRunningFutures(vanishedRunningUpdateKey);
        }
    }

    private void removeAndCancelRunningFutures(K vanishedCacheKey) {
        Future future = (Future)this.runningUpdates.remove(vanishedCacheKey);
        if (future != null && !future.isDone() && !future.cancel(true)) {
            logger.debug("Failed to cancel running update for cache entry with key '{}'.", vanishedCacheKey);
        }
    }

    private void removeFinishedUpdates() {
        for (Map.Entry<K, ListenableFuture<ExpiringValue<V>>> entry : this.runningUpdates.entrySet()) {
            Future runningUpdate = (Future)entry.getValue();
            if (runningUpdate == null || !runningUpdate.isDone()) continue;
            this.runningUpdates.remove(entry.getKey(), runningUpdate);
        }
    }

    private void addNewCacheEntries(ImmutableSet<K> updatedKeySet) {
        Sets.SetView newCacheKeys = Sets.difference(updatedKeySet, (Set)this.cache.keySet());
        for (Object newCacheKey : newCacheKeys) {
            this.cache.putIfAbsent(newCacheKey, ExpiringValue.expiredNullValue());
        }
    }

    private void invalidateExistingCacheValues() {
        for (Map.Entry<K, ExpiringValue<V>> entry : this.cache.entrySet()) {
            this.cache.put(entry.getKey(), ExpiringValue.expiredNullValue());
        }
    }

    private void startCacheValueUpdate(K key, ExpiringValue<V> oldValue) {
        PlaceholderFuture<ExpiringValue<ExpiringValue<V>>> newFuture = this.tryReserveUpdateSlot(key);
        if (newFuture != null) {
            newFuture.setDelegate(this.withCallbackHandler(key, oldValue, this.tryLoadValue(key)));
        }
    }

    @Nullable
    private PlaceholderFuture<ExpiringValue<V>> tryReserveUpdateSlot(K key) {
        ListenableFuture<ExpiringValue<V>> oldFuture = this.runningUpdates.get(key);
        if (oldFuture != null && !oldFuture.isDone()) {
            logger.debug("Refresh of cache entry with key '{}' is already running.", key);
            return null;
        }
        PlaceholderFuture<ExpiringValue<V>> newFuture = new PlaceholderFuture<ExpiringValue<V>>();
        if (oldFuture == null) {
            return this.runningUpdates.putIfAbsent(key, (ListenableFuture<ExpiringValue<PlaceholderFuture<ExpiringValue<V>>>>)newFuture) == null ? newFuture : null;
        }
        return this.runningUpdates.replace(key, (ListenableFuture<ExpiringValue<PlaceholderFuture<ExpiringValue<V>>>>)oldFuture, (ListenableFuture<ExpiringValue<PlaceholderFuture<ExpiringValue<V>>>>)newFuture) ? newFuture : null;
    }

    private ListenableFuture<ExpiringValue<V>> tryLoadValue(K key) {
        ListenableFuture future = this.loader.loadValue(key);
        return future != null ? future : Futures.immediateFailedFuture((Throwable)new NullPointerException("future must not be null"));
    }

    private ListenableFuture<ExpiringValue<V>> withCallbackHandler(K key, ExpiringValue<V> oldValue, ListenableFuture<ExpiringValue<V>> future) {
        Futures.addCallback(future, new CacheUpdateCallback<K, V>(key, oldValue, this, this.cacheUpdatePolicy), (Executor)MoreExecutors.directExecutor());
        return future;
    }
}

