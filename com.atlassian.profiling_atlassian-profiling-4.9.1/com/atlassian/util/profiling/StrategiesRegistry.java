/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.strategy.MetricStrategy;
import com.atlassian.util.profiling.strategy.ProfilerStrategy;
import com.atlassian.util.profiling.strategy.impl.StackProfilerStrategy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;

@Internal
public class StrategiesRegistry {
    private static final List<MetricStrategy> metricStrategies = new CopyOnWriteArrayList<MetricStrategy>();
    private static final List<ProfilerStrategy> profilerStrategies = new CopyOnWriteArrayList<ProfilerStrategy>();

    private StrategiesRegistry() {
        throw new IllegalStateException("StrategiesRegistry should not be instantiated");
    }

    public static void addMetricStrategy(@Nonnull MetricStrategy strategy) {
        metricStrategies.add(Objects.requireNonNull(strategy, "strategy"));
        strategy.setConfiguration(Metrics.getConfiguration());
    }

    public static void addProfilerStrategy(@Nonnull ProfilerStrategy strategy) {
        profilerStrategies.add(Objects.requireNonNull(strategy, "strategy"));
        strategy.setConfiguration(Timers.getConfiguration());
    }

    public static boolean removeMetricStrategy(@Nonnull MetricStrategy strategy) {
        return metricStrategies.remove(strategy);
    }

    public static boolean removeProfilerStrategy(@Nonnull ProfilerStrategy strategy) {
        if (Objects.equals(strategy, StrategiesRegistry.getDefaultProfilerStrategy())) {
            return false;
        }
        return profilerStrategies.remove(strategy);
    }

    @Nonnull
    public static StackProfilerStrategy getDefaultProfilerStrategy() {
        return (StackProfilerStrategy)profilerStrategies.get(0);
    }

    @Nonnull
    static Collection<MetricStrategy> getMetricStrategies() {
        return Collections.unmodifiableCollection(metricStrategies);
    }

    @Nonnull
    static Collection<ProfilerStrategy> getProfilerStrategies() {
        return Collections.unmodifiableCollection(profilerStrategies);
    }

    static {
        StackProfilerStrategy defaultStrategy = new StackProfilerStrategy();
        defaultStrategy.setConfiguration(Timers.getConfiguration());
        profilerStrategies.add(defaultStrategy);
    }
}

