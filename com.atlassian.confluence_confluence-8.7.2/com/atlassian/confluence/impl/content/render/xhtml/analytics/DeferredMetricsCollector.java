/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Ticker
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  net.jcip.annotations.ThreadSafe
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.fugue.Effect;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
class DeferredMetricsCollector
implements MarshallerMetricsCollector {
    private static final Logger log = LoggerFactory.getLogger(DeferredMetricsCollector.class);
    private final MarshallerMetricsAccumulationKey accumulationKey;
    private final Effect<MarshallerMetrics> metricsAccumulationCallback;
    private final Ticker ticker;
    private final ConcurrentMap<String, Long> metrics = Maps.newConcurrentMap();
    private static final String EXECUTION_TIME = "executionTime";
    private static final String STREAMING_TIME = "streamingTime";
    private static final Collection RESERVED_METRIC_NAMES = ImmutableSet.of((Object)"executionTime", (Object)"streamingTime");

    DeferredMetricsCollector(MarshallerMetricsAccumulationKey accumulationKey, Ticker ticker, Effect<MarshallerMetrics> metricsAccumulationCallback) {
        this.accumulationKey = (MarshallerMetricsAccumulationKey)Preconditions.checkNotNull((Object)accumulationKey);
        this.metricsAccumulationCallback = (Effect)Preconditions.checkNotNull(metricsAccumulationCallback);
        this.ticker = (Ticker)Preconditions.checkNotNull((Object)ticker);
    }

    @Override
    public @NonNull DeferredMetricsCollector addCustomMetric(String name, long value) {
        Preconditions.checkArgument((!RESERVED_METRIC_NAMES.contains(name) ? 1 : 0) != 0, (String)"Metric name [%s] is reserved", (Object)name);
        return this.addMetric(name, value);
    }

    private DeferredMetricsCollector addMetric(String name, long value) {
        if (this.metrics.putIfAbsent(name, value) != null) {
            throw new IllegalStateException("Metric [" + name + "] already recorded for key " + this.accumulationKey);
        }
        log.debug("Stored metric [{}] with value [{}] for key [{}]", new Object[]{name, value, this.accumulationKey});
        return this;
    }

    @Override
    public void publish() {
        Long executionTime = (Long)this.metrics.remove(EXECUTION_TIME);
        Long streamingTime = (Long)this.metrics.remove(STREAMING_TIME);
        Preconditions.checkState((executionTime != null ? 1 : 0) != 0, (String)"Execution time not recorded for key [%s] - not publishing metrics", (Object)this.accumulationKey);
        Preconditions.checkState((streamingTime != null ? 1 : 0) != 0, (String)"Streaming time not recorded for key [%s] - not publishing metrics", (Object)this.accumulationKey);
        this.metricsAccumulationCallback.apply((Object)new MarshallerMetrics(this.accumulationKey, 1, executionTime, streamingTime, this.metrics));
    }

    @Override
    public @NonNull MarshallerMetricsCollector.Timer executionStart() {
        return this.newTimer(EXECUTION_TIME);
    }

    @Override
    public @NonNull MarshallerMetricsCollector.Timer streamingStart() {
        return this.newTimer(STREAMING_TIME);
    }

    private MarshallerMetricsCollector.Timer newTimer(String metricName) {
        long startTime = this.ticker.read();
        return () -> this.addMetric(metricName, this.ticker.read() - startTime);
    }
}

