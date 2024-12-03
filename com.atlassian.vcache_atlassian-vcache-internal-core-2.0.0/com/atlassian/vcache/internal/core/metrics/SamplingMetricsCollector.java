/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestContext
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.DefaultMetricsCollector;
import com.atlassian.vcache.internal.core.metrics.DefaultRequestMetrics;
import com.atlassian.vcache.internal.core.metrics.EmptyRequestMetrics;
import com.atlassian.vcache.internal.core.metrics.MutableRequestMetrics;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SamplingMetricsCollector
extends DefaultMetricsCollector {
    private static final NoOpRequestMetrics NOOP_REQUEST_METRICS = new NoOpRequestMetrics();
    private final Predicate<RequestContext> collectMetrics;

    public SamplingMetricsCollector(Supplier<RequestContext> contextSupplier, Predicate<RequestContext> collectMetrics) {
        super(contextSupplier);
        this.collectMetrics = Objects.requireNonNull(collectMetrics);
    }

    @Override
    protected MutableRequestMetrics obtainMetrics(RequestContext context) {
        return (MutableRequestMetrics)context.computeIfAbsent((Object)this, () -> this.collectMetrics.test(context) ? new DefaultRequestMetrics() : NOOP_REQUEST_METRICS);
    }

    private static class NoOpRequestMetrics
    extends EmptyRequestMetrics
    implements MutableRequestMetrics {
        private NoOpRequestMetrics() {
        }

        @Override
        public void record(String cacheName, CacheType cacheType, MetricLabel metricLabel, long sample) {
        }
    }
}

