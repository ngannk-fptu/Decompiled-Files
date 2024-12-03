/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.LocalCacheOperations
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.LocalCacheOperations;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedFactory;
import com.atlassian.vcache.internal.core.metrics.TimedSupplier;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class TimedLocalCacheOperations<K, V>
implements LocalCacheOperations<K, V> {
    protected final String cacheName;
    protected final CacheType cacheType;
    protected final MetricsRecorder metricsRecorder;

    TimedLocalCacheOperations(String cacheName, CacheType cacheType, MetricsRecorder metricsRecorder) {
        this.cacheName = Objects.requireNonNull(cacheName);
        this.cacheType = Objects.requireNonNull(cacheType);
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
    }

    protected abstract LocalCacheOperations<K, V> getDelegate();

    public Optional<V> get(K key) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_GET_CALL, t));){
            Optional result = this.getDelegate().get(key);
            this.metricsRecorder.record(this.cacheName, this.cacheType, result.isPresent() ? MetricLabel.NUMBER_OF_HITS : MetricLabel.NUMBER_OF_MISSES, 1L);
            Optional optional = result;
            return optional;
        }
    }

    public V get(K key, Supplier<? extends V> supplier) {
        Throwable throwable = null;
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_GET_CALL, t));){
            Object object;
            TimedSupplier<? extends V> timedSupplier = new TimedSupplier<V>(supplier, this::handleTimedSupplier);
            Throwable throwable2 = null;
            try {
                object = this.getDelegate().get(key, timedSupplier);
            }
            catch (Throwable throwable3) {
                try {
                    try {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    catch (Throwable throwable4) {
                        TimedLocalCacheOperations.$closeResource(throwable2, timedSupplier);
                        throw throwable4;
                    }
                }
                catch (Throwable throwable5) {
                    throwable = throwable5;
                    throw throwable5;
                }
            }
            TimedLocalCacheOperations.$closeResource(throwable2, timedSupplier);
            return (V)object;
        }
    }

    public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, Iterable<K> keys) {
        Throwable throwable = null;
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_GET_CALL, t));){
            Map map;
            TimedFactory<K, V> timedFactory = new TimedFactory<K, V>(factory, this::handleTimedFactory);
            Throwable throwable2 = null;
            try {
                map = this.getDelegate().getBulk(timedFactory, keys);
            }
            catch (Throwable throwable3) {
                try {
                    try {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    catch (Throwable throwable4) {
                        TimedLocalCacheOperations.$closeResource(throwable2, timedFactory);
                        throw throwable4;
                    }
                }
                catch (Throwable throwable5) {
                    throwable = throwable5;
                    throw throwable5;
                }
            }
            TimedLocalCacheOperations.$closeResource(throwable2, timedFactory);
            return map;
        }
    }

    public void put(K key, V value) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_PUT_CALL, t));){
            this.getDelegate().put(key, value);
        }
    }

    public Optional<V> putIfAbsent(K key, V value) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_PUT_CALL, t));){
            Optional optional = this.getDelegate().putIfAbsent(key, value);
            return optional;
        }
    }

    public boolean replaceIf(K key, V currentValue, V newValue) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_PUT_CALL, t));){
            boolean bl = this.getDelegate().replaceIf(key, currentValue, newValue);
            return bl;
        }
    }

    public boolean removeIf(K key, V value) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_REMOVE_CALL, t));){
            boolean bl = this.getDelegate().removeIf(key, value);
            return bl;
        }
    }

    public void remove(K key) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_REMOVE_CALL, t));){
            this.getDelegate().remove(key);
        }
    }

    public void removeAll() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_REMOVE_ALL_CALL, t));){
            this.getDelegate().removeAll();
        }
    }

    private void handleTimedSupplier(Optional<Long> time) {
        if (time.isPresent()) {
            this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_SUPPLIER_CALL, time.get());
        }
        this.metricsRecorder.record(this.cacheName, this.cacheType, time.isPresent() ? MetricLabel.NUMBER_OF_MISSES : MetricLabel.NUMBER_OF_HITS, 1L);
    }

    private void handleTimedFactory(Optional<Long> time, Long numberOfKeys) {
        time.ifPresent(t -> {
            this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_FACTORY_CALL, (long)t);
            this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.NUMBER_OF_FACTORY_KEYS, numberOfKeys);
        });
    }
}

