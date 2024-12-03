/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.impl.metrics;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.impl.metrics.MetricEmitter;
import com.atlassian.cache.impl.metrics.MissingInterfacesException;
import com.atlassian.instrumentation.caches.CacheCollector;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstrumentedCachedReference<V>
implements CachedReference<V>,
ManagedCache {
    private final CachedReference<V> delegate;
    private final MetricEmitter metricEmitter;

    @VisibleForTesting
    InstrumentedCachedReference(CachedReference<V> delegate, MetricEmitter metricEmitter) {
        this.delegate = delegate;
        this.metricEmitter = metricEmitter;
    }

    public static <V> InstrumentedCachedReference<V> wrap(@Nonnull CachedReference<V> delegate) {
        Objects.requireNonNull(delegate, "delegate");
        if (!(delegate instanceof ManagedCache)) {
            throw new MissingInterfacesException(delegate, ManagedCache.class);
        }
        MetricEmitter metricEmitter = MetricEmitter.create(delegate.getClass().getName());
        return new InstrumentedCachedReference<V>(delegate, metricEmitter);
    }

    @VisibleForTesting
    MetricEmitter getMetricEmitter() {
        return this.metricEmitter;
    }

    @Nonnull
    public V get() {
        return (V)this.delegate.get();
    }

    public void reset() {
        this.metricEmitter.emitCachedReferenceReset();
        this.delegate.reset();
    }

    public boolean isPresent() {
        return this.delegate.isPresent();
    }

    @Nonnull
    public Optional<V> getIfPresent() {
        return this.delegate.getIfPresent();
    }

    public void addListener(@Nonnull CachedReferenceListener<V> listener, boolean includeValues) {
        this.delegate.addListener(listener, includeValues);
    }

    public void removeListener(@Nonnull CachedReferenceListener<V> listener) {
        this.delegate.removeListener(listener);
    }

    public void clear() {
        ((ManagedCache)this.delegate).clear();
    }

    @Nonnull
    public String getName() {
        return ((ManagedCache)this.delegate).getName();
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
    public SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics() {
        return ((ManagedCache)this.delegate).getStatistics();
    }

    @Nullable
    public CacheCollector getCacheCollector() {
        return ((ManagedCache)this.delegate).getCacheCollector();
    }
}

