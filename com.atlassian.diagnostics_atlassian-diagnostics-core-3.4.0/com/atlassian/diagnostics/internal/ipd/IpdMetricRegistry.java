/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistryDelegate;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCopyMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCounterMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.management.ObjectName;

public interface IpdMetricRegistry {
    public void remove(MetricKey var1);

    public void remove(IpdMetricBuilder<?> var1);

    public <T extends IpdMetric> T register(IpdMetricBuilder<T> var1);

    @Nullable
    public IpdMetric get(MetricKey var1);

    public void removeAll();

    public void removeIf(Predicate<IpdMetric> var1);

    default public IpdMetricRegistry createRegistry(String prefix, MetricTag.RequiredMetricTag ... tags) {
        return this.createRegistry(builder -> builder.withPrefix(prefix).withTags(tags));
    }

    default public IpdMetricRegistry createRegistry(Consumer<IpdMetricBuilder<?>> metricBuilderMutation) {
        return new IpdMetricRegistryDelegate(this, metricBuilderMutation);
    }

    default public IpdValueAndStatsMetricWrapper valueAndStatsMetric(String name, MetricTag.RequiredMetricTag ... staticTags) {
        return new IpdValueAndStatsMetricWrapper(this.register(IpdStatsMetric.builder(name, staticTags)), this.register(IpdValueMetric.builder(name, staticTags)));
    }

    default public IpdValueMetric valueMetric(String name, MetricTag.RequiredMetricTag ... staticTags) {
        return this.register(IpdValueMetric.builder(name, staticTags));
    }

    default public IpdStatsMetric statsMetric(String name, MetricTag.RequiredMetricTag ... staticTags) {
        return this.register(IpdStatsMetric.builder(name, staticTags));
    }

    default public IpdCounterMetric counterMetric(String name, MetricTag.RequiredMetricTag ... staticTags) {
        return this.register(IpdCounterMetric.builder(name, staticTags));
    }

    default public <T> IpdCustomMetric<T> customMetric(String name, Class<T> type, MetricTag.RequiredMetricTag ... staticTags) {
        return this.register(IpdCustomMetric.builder(name, type, staticTags));
    }

    default public <T> IpdCustomMetric<T> customMetric(String name, T obj, MetricTag.RequiredMetricTag ... staticTags) {
        return this.register(IpdCustomMetric.builder(name, obj, staticTags));
    }

    default public IpdCopyMetric statsCopyMetric(String name, ObjectName objectToCopy, MetricTag.RequiredMetricTag ... staticTags) {
        return this.register(IpdCopyMetric.builder(name, objectToCopy, IpdStatsMetric.allAttributes, IpdStatsMetric.shortAttributes, staticTags));
    }
}

