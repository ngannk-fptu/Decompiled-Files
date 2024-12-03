/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.TransactionControl;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import java.util.Objects;

class TimedTransactionControl
implements TransactionControl {
    private final TransactionControl delegate;
    private final MetricsRecorder metricsRecorder;
    private final String cacheName;

    TimedTransactionControl(TransactionControl delegate, MetricsRecorder metricsRecorder, String cacheName) {
        this.delegate = Objects.requireNonNull(delegate);
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
        this.cacheName = Objects.requireNonNull(cacheName);
    }

    @Override
    public void transactionSync() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.TIMED_TRANSACTION_SYNC_CALL, t));){
            this.delegate.transactionSync();
        }
    }

    @Override
    public boolean transactionDiscard() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.TIMED_TRANSACTION_DISCARD_CALL, t));){
            boolean bl = this.delegate.transactionDiscard();
            return bl;
        }
    }
}

