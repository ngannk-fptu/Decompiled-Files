/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalWriteOperationsUnbuffered
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.ExternalWriteOperationsUnbuffered;
import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedExternalCache;
import com.atlassian.vcache.internal.core.metrics.TimedUtils;
import java.util.concurrent.CompletionStage;

abstract class TimedExternalWriteOperationsUnbuffered<V>
extends TimedExternalCache<V>
implements ExternalWriteOperationsUnbuffered<V> {
    TimedExternalWriteOperationsUnbuffered(MetricsRecorder metricsRecorder) {
        super(metricsRecorder);
    }

    protected abstract ExternalWriteOperationsUnbuffered<V> getDelegateOps();

    public CompletionStage<Boolean> put(String key, V value, PutPolicy policy) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_PUT_CALL, t));){
            CompletionStage result = this.getDelegateOps().put(key, value, policy);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_PUT, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }

    public CompletionStage<Void> remove(Iterable<String> keys) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_REMOVE_CALL, t));){
            CompletionStage result = this.getDelegateOps().remove(keys);
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_REMOVE, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }

    public CompletionStage<Void> removeAll() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_REMOVE_ALL_CALL, t));){
            CompletionStage result = this.getDelegateOps().removeAll();
            TimedUtils.whenCompletableFuture(result, future -> {
                if (future.isCompletedExceptionally()) {
                    this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_REMOVE_ALL, 1L);
                }
            });
            CompletionStage completionStage = result;
            return completionStage;
        }
    }
}

