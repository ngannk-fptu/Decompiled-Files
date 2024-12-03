/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.impl.metrics;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.impl.metrics.MetricEmitter;
import com.atlassian.cache.impl.metrics.MissingInterfacesException;
import com.atlassian.instrumentation.caches.CacheCollector;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstrumentedCache<K, V>
implements Cache<K, V>,
ManagedCache {
    private final Cache<K, V> delegate;
    private final MetricEmitter metricEmitter;

    @VisibleForTesting
    InstrumentedCache(Cache<K, V> delegate, MetricEmitter metricEmitter) {
        this.delegate = delegate;
        this.metricEmitter = metricEmitter;
    }

    public static <K, V> InstrumentedCache<K, V> wrap(@Nonnull Cache<K, V> delegate) {
        Objects.requireNonNull(delegate, "delegate");
        if (!(delegate instanceof ManagedCache)) {
            throw new MissingInterfacesException(delegate, ManagedCache.class);
        }
        MetricEmitter metricEmitter = MetricEmitter.create(delegate.getClass().getName());
        return new InstrumentedCache<K, V>(delegate, metricEmitter);
    }

    @Nonnull
    public String getName() {
        return this.delegate.getName();
    }

    public boolean containsKey(@Nonnull K key) {
        return this.delegate.containsKey(key);
    }

    @Nonnull
    public Collection<K> getKeys() {
        return this.delegate.getKeys();
    }

    @Nullable
    public V get(@Nonnull K key) {
        return (V)this.delegate.get(key);
    }

    @Nonnull
    public V get(@Nonnull K key, @Nonnull Supplier<? extends V> valueSupplier) {
        return (V)this.delegate.get(key, valueSupplier);
    }

    @Nonnull
    public Map<K, V> getBulk(@Nonnull Set<K> keys, @Nonnull Function<Set<K>, Map<K, V>> valuesSupplier) {
        return this.delegate.getBulk(keys, valuesSupplier);
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        this.delegate.put(key, value);
    }

    @Nullable
    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        return (V)this.delegate.putIfAbsent(key, value);
    }

    public void remove(@Nonnull K key) {
        this.delegate.remove(key);
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        return this.delegate.remove(key, value);
    }

    public void removeAll() {
        this.metricEmitter.emitCacheRemoveAll();
        this.delegate.removeAll();
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        return this.delegate.replace(key, oldValue, newValue);
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        this.delegate.addListener(listener, includeValues);
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        this.delegate.removeListener(listener);
    }

    public void clear() {
        ((ManagedCache)this.delegate).clear();
    }

    public boolean isFlushable() {
        return ((ManagedCache)this.delegate).isFlushable();
    }

    @Nullable
    public Integer currentMaxEntries() {
        return ((ManagedCache)this.delegate).currentMaxEntries();
    }

    public boolean updateMaxEntries(int newValue) {
        return ((ManagedCache)this.delegate).updateMaxEntries(newValue);
    }

    @Nullable
    public Long currentExpireAfterAccessMillis() {
        return ((ManagedCache)this.delegate).currentExpireAfterAccessMillis();
    }

    public boolean updateExpireAfterAccess(long expireAfter, @Nonnull TimeUnit timeUnit) {
        return ((ManagedCache)this.delegate).updateExpireAfterAccess(expireAfter, timeUnit);
    }

    @Nullable
    public Long currentExpireAfterWriteMillis() {
        return ((ManagedCache)this.delegate).currentExpireAfterWriteMillis();
    }

    public boolean updateExpireAfterWrite(long expireAfter, @Nonnull TimeUnit timeUnit) {
        return ((ManagedCache)this.delegate).updateExpireAfterWrite(expireAfter, timeUnit);
    }

    public boolean isLocal() {
        return ((ManagedCache)this.delegate).isLocal();
    }

    public boolean isReplicateAsynchronously() {
        return ((ManagedCache)this.delegate).isReplicateAsynchronously();
    }

    public boolean isReplicateViaCopy() {
        return ((ManagedCache)this.delegate).isReplicateViaCopy();
    }

    public boolean isStatisticsEnabled() {
        return ((ManagedCache)this.delegate).isStatisticsEnabled();
    }

    @Deprecated
    public void setStatistics(boolean enabled) {
        ((ManagedCache)this.delegate).setStatistics(enabled);
    }

    @Nonnull
    public SortedMap<CacheStatisticsKey, java.util.function.Supplier<Long>> getStatistics() {
        return ((ManagedCache)this.delegate).getStatistics();
    }

    @Nullable
    public CacheCollector getCacheCollector() {
        return ((ManagedCache)this.delegate).getCacheCollector();
    }

    @VisibleForTesting
    MetricEmitter getMetricEmitter() {
        return this.metricEmitter;
    }
}

