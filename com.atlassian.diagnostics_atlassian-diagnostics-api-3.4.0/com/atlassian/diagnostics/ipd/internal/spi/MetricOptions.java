/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.MetricKey
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.util.profiling.MetricKey;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MetricOptions {
    private final String productPrefix;
    private final Supplier<Boolean> enabledCheck;
    private final Consumer<IpdMetric> metricUpdateListener;
    private final MetricKey ipdMetricKey;
    private final boolean regularLogging;

    public MetricOptions(MetricKey ipdMetricKey, String productPrefix, Supplier<Boolean> enabledCheck, Consumer<IpdMetric> metricUpdateListener, boolean regularLogging) {
        this.productPrefix = productPrefix;
        this.enabledCheck = enabledCheck;
        this.metricUpdateListener = metricUpdateListener;
        this.ipdMetricKey = ipdMetricKey;
        this.regularLogging = regularLogging;
    }

    public String getProductPrefix() {
        return this.productPrefix;
    }

    public Supplier<Boolean> getEnabledCheck() {
        return this.enabledCheck;
    }

    public Consumer<IpdMetric> getMetricUpdateListener() {
        return this.metricUpdateListener;
    }

    public MetricKey getIpdMetricKey() {
        return this.ipdMetricKey;
    }

    public boolean isRegularLogging() {
        return this.regularLogging;
    }
}

