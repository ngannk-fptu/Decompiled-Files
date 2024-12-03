/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.util.profiling.MetricKey
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.util.profiling.MetricKey;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class IpdMetricRegistryDelegate
implements IpdMetricRegistry {
    private final IpdMetricRegistry delegate;
    private final Consumer<IpdMetricBuilder<?>> metricBuilderMutation;
    private final Set<MetricKey> registeredMetrics = ConcurrentHashMap.newKeySet();

    IpdMetricRegistryDelegate(IpdMetricRegistry delegate, Consumer<IpdMetricBuilder<?>> metricBuilderMutation) {
        this.delegate = delegate;
        this.metricBuilderMutation = metricBuilderMutation;
    }

    @Override
    public void remove(MetricKey metricKey) {
        this.registeredMetrics.remove(metricKey);
        this.delegate.remove(metricKey);
    }

    @Override
    public void remove(IpdMetricBuilder<?> ipdMetricBuilder) {
        try {
            this.metricBuilderMutation.accept(ipdMetricBuilder);
            this.delegate.remove(ipdMetricBuilder);
        }
        finally {
            this.registeredMetrics.remove(ipdMetricBuilder.getMetricKey());
        }
    }

    @Override
    public void removeIf(Predicate<IpdMetric> predicate) {
        List metrics = this.registeredMetrics.stream().map(this::get).filter(Objects::nonNull).collect(Collectors.toList());
        metrics.stream().filter(predicate).map(IpdMetric::getMetricKey).forEach(this::remove);
    }

    @Override
    public <T extends IpdMetric> T register(IpdMetricBuilder<T> ipdMetricBuilder) {
        try {
            this.metricBuilderMutation.accept(ipdMetricBuilder);
            T t = this.delegate.register(ipdMetricBuilder);
            return t;
        }
        finally {
            this.registeredMetrics.add(ipdMetricBuilder.getMetricKey());
        }
    }

    @Override
    @Nullable
    public IpdMetric get(MetricKey metricKey) {
        if (this.registeredMetrics.contains(metricKey)) {
            return this.delegate.get(metricKey);
        }
        return null;
    }

    @Override
    public void removeAll() {
        this.registeredMetrics.forEach(this.delegate::remove);
        this.registeredMetrics.clear();
    }
}

