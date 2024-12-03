/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCache
 *  com.atlassian.vcache.ExternalWriteOperationsUnbuffered
 *  com.atlassian.vcache.StableReadExternalCache
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.ExternalWriteOperationsUnbuffered;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedExternalWriteOperationsUnbuffered;
import java.util.Objects;

class TimedStableReadExternalCache<V>
extends TimedExternalWriteOperationsUnbuffered<V>
implements StableReadExternalCache<V> {
    private final StableReadExternalCache<V> delegate;

    TimedStableReadExternalCache(MetricsRecorder metricsRecorder, StableReadExternalCache<V> delegate) {
        super(metricsRecorder);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    protected ExternalWriteOperationsUnbuffered<V> getDelegateOps() {
        return this.delegate;
    }

    @Override
    protected ExternalCache<V> getDelegate() {
        return this.delegate;
    }
}

