/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Metrics$Builder
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.diagnostics.internal.ipd.metrics;

import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdMicrometerMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricTag;
import com.atlassian.util.profiling.Metrics;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

public class IpdCounterMetric
extends IpdMicrometerMetric {
    public static final List<String> attributes = ImmutableList.of((Object)"Count");
    private final Metrics.Builder micrometerMetric = Metrics.metric((String)this.getMetricKey().getMetricName()).tags((Iterable)this.getMetricKey().getTags());

    protected IpdCounterMetric(MetricOptions metricOptions) {
        super(metricOptions, attributes, attributes);
    }

    public void increment() {
        this.increment(1L);
    }

    public void increment(long value) {
        if (this.isEnabled()) {
            this.micrometerMetric.incrementCounter(Long.valueOf(value));
            this.metricUpdated();
        }
    }

    public static IpdMetricBuilder<IpdCounterMetric> builder(String metricName, MetricTag.RequiredMetricTag ... staticTags) {
        return new IpdMetricBuilder<IpdCounterMetric>(IpdCounterMetric.appendToMetricName(metricName, "counter"), Arrays.asList(staticTags), IpdCounterMetric::new, IpdCounterMetric::verifyExpectedMetricType);
    }

    private static void verifyExpectedMetricType(IpdMetric ipdMetric) throws ClassCastException {
        if (ipdMetric instanceof IpdCounterMetric) {
            return;
        }
        throw new ClassCastException(String.format("Metric type was %s, but expected %s", ipdMetric.getClass(), IpdCounterMetric.class));
    }
}

