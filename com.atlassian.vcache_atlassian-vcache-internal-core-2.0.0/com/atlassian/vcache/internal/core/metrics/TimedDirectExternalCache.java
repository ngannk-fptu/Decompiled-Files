/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.CasIdentifier
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.ExternalWriteOperationsUnbuffered
 *  com.atlassian.vcache.IdentifiedValue
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.CasIdentifier;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.ExternalWriteOperationsUnbuffered;
import com.atlassian.vcache.IdentifiedValue;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.VCacheCoreUtils;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedExternalWriteOperationsUnbuffered;
import com.atlassian.vcache.internal.core.metrics.TimedSupplier;
import com.atlassian.vcache.internal.core.metrics.TimedUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

class TimedDirectExternalCache<V>
extends TimedExternalWriteOperationsUnbuffered<V>
implements DirectExternalCache<V> {
    private final DirectExternalCache<V> delegate;

    TimedDirectExternalCache(MetricsRecorder metricsRecorder, DirectExternalCache<V> delegate) {
        super(metricsRecorder);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    protected ExternalWriteOperationsUnbuffered<V> getDelegateOps() {
        return this.delegate;
    }

    @Override
    protected DirectExternalCache<V> getDelegate() {
        return this.delegate;
    }

    public CompletionStage<Optional<IdentifiedValue<V>>> getIdentified(String key) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_IDENTIFIED_GET_CALL, t));){
            CompletionStage result = this.getDelegate().getIdentified(key);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_IDENTIFIED_GET, 1L);
                } else {
                    Optional rj = (Optional)future.join();
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, rj.isPresent() ? MetricLabel.NUMBER_OF_HITS : MetricLabel.NUMBER_OF_MISSES, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }

    public CompletionStage<IdentifiedValue<V>> getIdentified(String key, Supplier<V> supplier) {
        Throwable throwable = null;
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_IDENTIFIED_GET_CALL, t));){
            CompletionStage completionStage;
            TimedSupplier<V> timedSupplier = new TimedSupplier<V>(supplier, this::handleTimedSupplier);
            Throwable throwable2 = null;
            try {
                CompletionStage result = this.getDelegate().getIdentified(key, timedSupplier);
                TimedUtils.whenCompletableFuture(result, future -> {
                    if (future.isCompletedExceptionally()) {
                        this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_IDENTIFIED_GET, 1L);
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
                        TimedDirectExternalCache.$closeResource(throwable2, timedSupplier);
                        throw throwable4;
                    }
                }
                catch (Throwable throwable5) {
                    throwable = throwable5;
                    throw throwable5;
                }
            }
            TimedDirectExternalCache.$closeResource(throwable2, timedSupplier);
            return completionStage;
        }
    }

    public CompletionStage<Map<String, Optional<IdentifiedValue<V>>>> getBulkIdentified(Iterable<String> keys) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_IDENTIFIED_GET_CALL, t));){
            CompletionStage result = this.getDelegate().getBulkIdentified(keys);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_IDENTIFIED_GET, 1L);
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

    public CompletionStage<Boolean> removeIf(String key, CasIdentifier casId) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_IDENTIFIED_REMOVE_CALL, t));){
            CompletionStage result = this.getDelegate().removeIf(key, casId);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_IDENTIFIED_REMOVE, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }

    public CompletionStage<Boolean> replaceIf(String key, CasIdentifier casId, V newValue) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_IDENTIFIED_REPLACE_CALL, t));){
            CompletionStage result = this.getDelegate().replaceIf(key, casId, newValue);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_IDENTIFIED_REPLACE, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }
}

