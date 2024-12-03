/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.util.profiling.strategy;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricsConfiguration;
import com.atlassian.util.profiling.MetricsFilter;
import com.atlassian.util.profiling.Ticker;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Internal
public interface MetricStrategy {
    default public void cleanupMetrics(MetricsFilter filter) {
    }

    default public void resetMetric(MetricKey metricKey) {
    }

    default public void onRequestEnd() {
    }

    default public void setConfiguration(MetricsConfiguration configuration) {
    }

    @Nonnull
    public Ticker startTimer(String var1);

    @Nonnull
    default public Ticker startTimer(MetricKey metricKey) {
        return this.startTimer(metricKey.getMetricName());
    }

    @Nonnull
    default public Ticker startLongRunningTimer(String metricName) {
        return Ticker.NO_OP;
    }

    @Nonnull
    default public Ticker startLongRunningTimer(MetricKey metricKey) {
        return Ticker.NO_OP;
    }

    default public void incrementCounter(MetricKey metricKey, long deltaValue) {
    }

    default public void incrementGauge(MetricKey metricKey, long deltaValue) {
    }

    public void updateHistogram(String var1, long var2);

    default public void updateHistogram(MetricKey metricKey, long value) {
        this.updateHistogram(metricKey.getMetricName(), value);
    }

    public void updateTimer(String var1, long var2, TimeUnit var4);

    default public void updateTimer(MetricKey metricKey, Duration time) {
        this.updateTimer(metricKey.getMetricName(), time.toNanos(), TimeUnit.NANOSECONDS);
    }

    default public void setGauge(MetricKey metricKey, long currentValue) {
    }
}

