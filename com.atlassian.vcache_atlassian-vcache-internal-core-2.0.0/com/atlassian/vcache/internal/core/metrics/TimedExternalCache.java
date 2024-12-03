/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCache
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.VCacheCoreUtils;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedFactory;
import com.atlassian.vcache.internal.core.metrics.TimedSupplier;
import com.atlassian.vcache.internal.core.metrics.TimedUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class TimedExternalCache<V>
implements ExternalCache<V> {
    protected final MetricsRecorder metricsRecorder;

    TimedExternalCache(MetricsRecorder metricsRecorder) {
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
    }

    protected abstract ExternalCache<V> getDelegate();

    public CompletionStage<Optional<V>> get(String key) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_GET_CALL, t));){
            CompletionStage result = this.getDelegate().get(key);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_GET, 1L);
                } else {
                    Optional rj = (Optional)future.join();
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, rj.isPresent() ? MetricLabel.NUMBER_OF_HITS : MetricLabel.NUMBER_OF_MISSES, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }

    public CompletionStage<V> get(String key, Supplier<V> supplier) {
        Throwable throwable = null;
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_GET_CALL, t));){
            CompletionStage completionStage;
            TimedSupplier<V> timedSupplier = new TimedSupplier<V>(supplier, this::handleTimedSupplier);
            Throwable throwable2 = null;
            try {
                CompletionStage result = this.getDelegate().get(key, timedSupplier);
                TimedUtils.whenCompletableFuture(result, future -> {
                    if (future.isCompletedExceptionally()) {
                        this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_GET, 1L);
                    } else {
                        this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, timedSupplier.wasInvoked() ? MetricLabel.NUMBER_OF_MISSES : MetricLabel.NUMBER_OF_HITS, 1L);
                    }
                });
                completionStage = result;
            }
            catch (Throwable throwable3) {
                try {
                    try {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    catch (Throwable throwable4) {
                        TimedExternalCache.$closeResource(throwable2, timedSupplier);
                        throw throwable4;
                    }
                }
                catch (Throwable throwable5) {
                    throwable = throwable5;
                    throw throwable5;
                }
            }
            TimedExternalCache.$closeResource(throwable2, timedSupplier);
            return completionStage;
        }
    }

    public CompletionStage<Map<String, Optional<V>>> getBulk(Iterable<String> keys) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_GET_CALL, t));){
            CompletionStage result = this.getDelegate().getBulk(keys);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_GET, 1L);
                } else {
                    Map rj = (Map)future.join();
                    long hits = rj.values().stream().filter(Optional::isPresent).count();
                    VCacheCoreUtils.whenPositive(hits, v -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_HITS, v));
                    VCacheCoreUtils.whenPositive((long)rj.size() - hits, v -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_MISSES, v));
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }

    public CompletionStage<Map<String, V>> getBulk(Function<Set<String>, Map<String, V>> factory, Iterable<String> keys) {
        Throwable throwable = null;
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_GET_CALL, t));){
            CompletionStage completionStage;
            TimedFactory timedFactory = new TimedFactory(factory, this::handleTimedFactory);
            Throwable throwable2 = null;
            try {
                CompletionStage result = this.getDelegate().getBulk(timedFactory, keys);
                TimedUtils.whenCompletableFuture(result, future -> {
                    if (future.isCompletedExceptionally()) {
                        this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_GET, 1L);
                    } else {
                        Map rj = (Map)future.join();
                        VCacheCoreUtils.whenPositive((long)rj.size() - timedFactory.getNumberOfKeys(), v -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_HITS, v));
                        VCacheCoreUtils.whenPositive(timedFactory.getNumberOfKeys(), v -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_MISSES, v));
                    }
                });
                completionStage = result;
            }
            catch (Throwable throwable3) {
                try {
                    try {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    catch (Throwable throwable4) {
                        TimedExternalCache.$closeResource(throwable2, timedFactory);
                        throw throwable4;
                    }
                }
                catch (Throwable throwable5) {
                    throwable = throwable5;
                    throw throwable5;
                }
            }
            TimedExternalCache.$closeResource(throwable2, timedFactory);
            return completionStage;
        }
    }

    public String getName() {
        return this.getDelegate().getName();
    }

    void handleTimedSupplier(Optional<Long> time) {
        if (time.isPresent()) {
            this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_SUPPLIER_CALL, time.get());
        }
    }

    private void handleTimedFactory(Optional<Long> time, Long numberOfKeys) {
        if (time.isPresent()) {
            this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_FACTORY_CALL, time.get());
            this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FACTORY_KEYS, numberOfKeys);
        }
    }
}

