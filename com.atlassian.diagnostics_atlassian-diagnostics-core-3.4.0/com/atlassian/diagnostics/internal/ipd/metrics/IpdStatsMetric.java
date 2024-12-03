/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  com.atlassian.util.profiling.MetricTimer
 *  com.atlassian.util.profiling.Metrics
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.diagnostics.internal.ipd.metrics;

import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdMicrometerMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricTag;
import com.atlassian.util.profiling.MetricTimer;
import com.atlassian.util.profiling.Metrics;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IpdStatsMetric
extends IpdMicrometerMetric {
    public static final List<String> allAttributes = ImmutableList.of((Object)"50thPercentile", (Object)"75thPercentile", (Object)"95thPercentile", (Object)"98thPercentile", (Object)"999thPercentile", (Object)"99thPercentile", (Object)"Count", (Object)"DurationUnit", (Object)"FifteenMinuteRate", (Object)"FiveMinuteRate", (Object)"Max", (Object)"Min", (Object[])new String[]{"Mean", "MeanRate", "OneMinuteRate", "RateUnit", "StdDev", "Value"});
    public static final List<String> shortAttributes = ImmutableList.of((Object)"99thPercentile", (Object)"Count", (Object)"Max", (Object)"Min", (Object)"Mean");
    private final MetricTimer micrometerMetric = Metrics.metric((String)this.getMetricKey().getMetricName()).tags((Iterable)this.getMetricKey().getTags()).timer();

    protected IpdStatsMetric(MetricOptions options) {
        super(options, allAttributes, shortAttributes);
    }

    public void update(Long value) {
        this.update(value, TimeUnit.MILLISECONDS);
    }

    public void update(Long value, TimeUnit timeUnit) {
        if (this.isEnabled()) {
            this.micrometerMetric.update(value.longValue(), timeUnit);
            this.metricUpdated();
        }
    }

    public static IpdMetricBuilder<IpdStatsMetric> builder(String metricName, MetricTag.RequiredMetricTag ... staticTags) {
        return new IpdMetricBuilder<IpdStatsMetric>(IpdStatsMetric.appendToMetricName(metricName, "statistics"), Arrays.asList(staticTags), IpdStatsMetric::new, IpdStatsMetric::verifyExpectedMetricType);
    }

    private static void verifyExpectedMetricType(IpdMetric ipdMetric) throws ClassCastException {
        if (ipdMetric instanceof IpdStatsMetric) {
            return;
        }
        throw new ClassCastException(String.format("Metric type was %s, but expected %s", ipdMetric.getClass(), IpdStatsMetric.class));
    }
}

