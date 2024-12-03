/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.util.profiling.MetricKey
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.internal.ipd.IpdMainRegistryConfiguration;
import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.exceptions.IpdRegisterException;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.util.profiling.MetricKey;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class IpdMainRegistry
implements IpdMetricRegistry {
    public static final Consumer<IpdMetric> EMPTY_LISTENER = metric -> {};
    private final AtomicBoolean ipdEnabled = new AtomicBoolean(true);
    private final AtomicBoolean ipdEnabledWip = new AtomicBoolean(false);
    private final IpdMainRegistryConfiguration configuration;
    protected final Map<MetricKey, IpdMetric> metrics = new ConcurrentHashMap<MetricKey, IpdMetric>();

    public IpdMainRegistry(IpdMainRegistryConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T extends IpdMetric> T register(IpdMetricBuilder<T> metricBuilder) {
        IpdMetric ipdMetric = this.metrics.computeIfAbsent(metricBuilder.getMetricKey(), k -> metricBuilder.buildMetric(this.configuration.getProductPrefix(), this.createMetricEnabledCheck(metricBuilder), this.getUpdateMetricListener(metricBuilder)));
        try {
            metricBuilder.verifyExpectedMetricType(ipdMetric);
            return (T)ipdMetric;
        }
        catch (ClassCastException e) {
            throw new IpdRegisterException(String.format("Ipd metric type check failed for metric %s", ipdMetric.getMetricKey()), e);
        }
    }

    @Override
    @Nullable
    public IpdMetric get(MetricKey metricKey) {
        return this.metrics.get(metricKey);
    }

    @Override
    public void remove(MetricKey metricKey) {
        IpdMetric metric = this.metrics.remove(metricKey);
        if (metric != null) {
            metric.close();
        }
    }

    @Override
    public void remove(IpdMetricBuilder<?> ipdMetricBuilder) {
        this.remove(ipdMetricBuilder.getMetricKey());
    }

    @Override
    public void removeAll() {
        this.metrics.values().forEach(IpdMetric::close);
        this.metrics.clear();
    }

    @Override
    public void removeIf(Predicate<IpdMetric> predicate) {
        this.metrics.values().stream().filter(predicate).forEach(IpdMetric::close);
        this.metrics.values().removeIf(predicate);
    }

    public Set<IpdMetric> getMetrics() {
        this.updateFeatureFlagState();
        return new HashSet<IpdMetric>(this.metrics.values());
    }

    public void unregisterAllDisabledMetrics() {
        this.updateFeatureFlagState();
        this.getMetrics().stream().filter(ipdMetric -> !ipdMetric.isEnabled()).forEach(IpdMetric::unregisterJmx);
    }

    private void updateFeatureFlagState() {
        this.ipdEnabled.set(this.configuration.isIpdEnabled());
        this.ipdEnabledWip.set(this.configuration.isIpdWipEnabled());
    }

    protected Supplier<Boolean> createMetricEnabledCheck(IpdMetricBuilder<?> metricBuilder) {
        return () -> this.ipdEnabled.get() && (!metricBuilder.isWorkInProgressMetric() || this.ipdEnabledWip.get());
    }

    protected Consumer<IpdMetric> getUpdateMetricListener(IpdMetricBuilder<?> metricBuilder) {
        if (metricBuilder.isLogOnUpdate()) {
            return this.configuration::metricUpdated;
        }
        return EMPTY_LISTENER;
    }
}

