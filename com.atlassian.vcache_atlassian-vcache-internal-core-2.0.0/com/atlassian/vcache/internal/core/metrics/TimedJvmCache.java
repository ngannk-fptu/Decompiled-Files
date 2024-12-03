/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.metrics.TimedLocalCacheOperations;
import java.util.Objects;
import java.util.Set;

class TimedJvmCache<K, V>
extends TimedLocalCacheOperations<K, V>
implements JvmCache<K, V> {
    private final JvmCache<K, V> delegate;

    TimedJvmCache(JvmCache<K, V> delegate, MetricsRecorder metricsRecorder) {
        super(delegate.getName(), CacheType.JVM, metricsRecorder);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    protected JvmCache<K, V> getDelegate() {
        return this.delegate;
    }

    public Set<K> getKeys() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, this.cacheType, MetricLabel.TIMED_GET_KEYS_CALL, t));){
            Set set = this.delegate.getKeys();
            return set;
        }
    }

    public String getName() {
        return this.delegate.getName();
    }
}

