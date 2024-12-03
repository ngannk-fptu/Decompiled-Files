/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.RequestCache
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedLocalCacheOperations;

class TimedRequestCache<K, V>
extends TimedLocalCacheOperations<K, V>
implements RequestCache<K, V> {
    private final RequestCache<K, V> delegate;

    TimedRequestCache(RequestCache<K, V> delegate, MetricsRecorder metricsRecorder) {
        super(delegate.getName(), CacheType.REQUEST, metricsRecorder);
        this.delegate = delegate;
    }

    @Override
    protected RequestCache<K, V> getDelegate() {
        return this.delegate;
    }

    public String getName() {
        return this.delegate.getName();
    }
}

