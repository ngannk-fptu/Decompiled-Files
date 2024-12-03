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

public class IpdValueMetric
extends IpdMicrometerMetric {
    public static final List<String> attributes = ImmutableList.of((Object)"Value");
    private final Metrics.Builder micrometerMetric = Metrics.metric((String)this.getMetricKey().getMetricName()).tags((Iterable)this.getMetricKey().getTags());

    protected IpdValueMetric(MetricOptions options) {
        super(options, attributes, attributes);
    }

    public void update(Long value) {
        if (this.isEnabled()) {
            this.micrometerMetric.setGauge(value);
            this.metricUpdated();
        }
    }

    public static IpdMetricBuilder<IpdValueMetric> builder(String metricName, MetricTag.RequiredMetricTag ... staticTags) {
        return new IpdMetricBuilder<IpdValueMetric>(IpdValueMetric.appendToMetricName(metricName, "value"), Arrays.asList(staticTags), IpdValueMetric::new, IpdValueMetric::verifyExpectedMetricType);
    }

    private static void verifyExpectedMetricType(IpdMetric ipdMetric) throws ClassCastException {
        if (ipdMetric instanceof IpdValueMetric) {
            return;
        }
        throw new ClassCastException(String.format("Metric type was %s, but expected %s", ipdMetric.getClass(), IpdValueMetric.class));
    }
}

