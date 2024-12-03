/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.TransactionalExternalCache
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedExternalCache;
import java.util.Objects;

class TimedTransactionalExternalCache<V>
extends TimedExternalCache<V>
implements TransactionalExternalCache<V> {
    private final TransactionalExternalCache<V> delegate;

    TimedTransactionalExternalCache(MetricsRecorder metricsRecorder, TransactionalExternalCache<V> delegate) {
        super(metricsRecorder);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    protected TransactionalExternalCache<V> getDelegate() {
        return this.delegate;
    }

    public void put(String key, V value, PutPolicy policy) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_PUT_CALL, t));){
            this.getDelegate().put(key, value, policy);
        }
    }

    public void remove(Iterable<String> keys) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_REMOVE_CALL, t));){
            this.getDelegate().remove(keys);
        }
    }

    public void removeAll() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.getDelegate().getName(), CacheType.EXTERNAL, MetricLabel.TIMED_REMOVE_ALL_CALL, t));){
            this.getDelegate().removeAll();
        }
    }
}

