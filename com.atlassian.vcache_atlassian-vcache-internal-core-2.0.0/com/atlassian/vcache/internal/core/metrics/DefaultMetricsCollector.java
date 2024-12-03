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
import com.atlassian.vcache.internal.core.metrics.DefaultRequestMetrics;
import com.atlassian.vcache.internal.core.metrics.MetricsCollector;
import com.atlassian.vcache.internal.core.metrics.MutableRequestMetrics;
import com.atlassian.vcache.internal.core.metrics.TimedDirectExternalCache;
import com.atlassian.vcache.internal.core.metrics.TimedJvmCache;
import com.atlassian.vcache.internal.core.metrics.TimedMarshaller;
import com.atlassian.vcache.internal.core.metrics.TimedRequestCache;
import com.atlassian.vcache.internal.core.metrics.TimedStableReadExternalCache;
import com.atlassian.vcache.internal.core.metrics.TimedTransactionControl;
import com.atlassian.vcache.internal.core.metrics.TimedTransactionalExternalCache;
import com.atlassian.vcache.internal.core.metrics.TimedUnmarshaller;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultMetricsCollector
implements MetricsCollector {
    private final Supplier<RequestContext> contextSupplier;

    public DefaultMetricsCollector(Supplier<RequestContext> contextSupplier) {
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
    }

    @Override
    public void record(String cacheName, CacheType cacheType, MetricLabel metricLabel, long sample) {
        this.obtainMetrics(this.contextSupplier.get()).record(cacheName, cacheType, metricLabel, sample);
    }

    @Override
    public RequestMetrics obtainRequestMetrics(RequestContext context) {
        return this.obtainMetrics(context);
    }

    @Override
    public TransactionControl wrap(TransactionControl control, String cacheName) {
        return new TimedTransactionControl(control, this, cacheName);
    }

    @Override
    public <T> MarshallingPair<T> wrap(MarshallingPair<T> marshalling, String cacheName) {
        return new MarshallingPair(new TimedMarshaller(marshalling.getMarshaller(), this, cacheName), new TimedUnmarshaller(marshalling.getUnmarshaller(), this, cacheName));
    }

    @Override
    public <K, V> JvmCache<K, V> wrap(JvmCache<K, V> cache) {
        return new TimedJvmCache<K, V>(cache, this);
    }

    @Override
    public <K, V> RequestCache<K, V> wrap(RequestCache<K, V> cache) {
        return new TimedRequestCache<K, V>(cache, this);
    }

    @Override
    public <V> DirectExternalCache<V> wrap(DirectExternalCache<V> cache) {
        return new TimedDirectExternalCache<V>(this, cache);
    }

    @Override
    public <V> StableReadExternalCache<V> wrap(StableReadExternalCache<V> cache) {
        return new TimedStableReadExternalCache<V>(this, cache);
    }

    @Override
    public <V> TransactionalExternalCache<V> wrap(TransactionalExternalCache<V> cache) {
        return new TimedTransactionalExternalCache<V>(this, cache);
    }

    MutableRequestMetrics obtainMetrics(RequestContext context) {
        return (MutableRequestMetrics)context.computeIfAbsent((Object)this, DefaultRequestMetrics::new);
    }
}

