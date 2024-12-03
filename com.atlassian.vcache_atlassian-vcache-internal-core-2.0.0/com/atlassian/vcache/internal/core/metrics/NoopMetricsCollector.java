/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.RequestCache
 *  com.atlassian.vcache.StableReadExternalCache
 *  com.atlassian.vcache.TransactionalExternalCache
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.RequestMetrics
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.RequestMetrics;
import com.atlassian.vcache.internal.core.TransactionControl;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.EmptyRequestMetrics;
import com.atlassian.vcache.internal.core.metrics.MetricsCollector;

public class NoopMetricsCollector
implements MetricsCollector {
    @Override
    public void record(String cacheName, CacheType cacheType, MetricLabel metricLabel, long sample) {
    }

    @Override
    public RequestMetrics obtainRequestMetrics(RequestContext requestContext) {
        return new EmptyRequestMetrics();
    }

    @Override
    public TransactionControl wrap(TransactionControl control, String cacheName) {
        return control;
    }

    @Override
    public <T> MarshallingPair<T> wrap(MarshallingPair<T> marshalling, String cacheName) {
        return marshalling;
    }

    @Override
    public <K, V> JvmCache<K, V> wrap(JvmCache<K, V> cache) {
        return cache;
    }

    @Override
    public <K, V> RequestCache<K, V> wrap(RequestCache<K, V> cache) {
        return cache;
    }

    @Override
    public <V> DirectExternalCache<V> wrap(DirectExternalCache<V> cache) {
        return cache;
    }

    @Override
    public <V> StableReadExternalCache<V> wrap(StableReadExternalCache<V> cache) {
        return cache;
    }

    @Override
    public <V> TransactionalExternalCache<V> wrap(TransactionalExternalCache<V> cache) {
        return cache;
    }
}

