/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.profiling;

import com.atlassian.util.profiling.StrategiesRegistry;
import com.atlassian.util.profiling.strategy.ProfilingStrategy;
import com.atlassian.util.profiling.strategy.impl.StackProfilingStrategy;
import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
public class UtilTimerStack {
    private static final ConcurrentLinkedQueue<ProfilingStrategy> strategies = new ConcurrentLinkedQueue();
    private static StackProfilingStrategy defaultProfilingStrategy = new StackProfilingStrategy(StrategiesRegistry.getDefaultProfilerStrategy());

    public static void add(ProfilingStrategy strategy) {
        if (!defaultProfilingStrategy.equals(strategy) && !strategies.contains(strategy)) {
            strategies.add(strategy);
        }
    }

    public static StackProfilingStrategy getDefaultStrategy() {
        return defaultProfilingStrategy;
    }

    public static boolean isActive() {
        if (defaultProfilingStrategy.isEnabled()) {
            return true;
        }
        for (ProfilingStrategy strategy : strategies) {
            if (!strategy.isEnabled()) continue;
            return true;
        }
        return false;
    }

    public static void pop(String name) {
        defaultProfilingStrategy.stop(name);
        for (ProfilingStrategy strategy : strategies) {
            strategy.stop(name);
        }
    }

    public static void push(String name) {
        if (defaultProfilingStrategy.isEnabled()) {
            defaultProfilingStrategy.start(name);
        }
        for (ProfilingStrategy strategy : strategies) {
            if (!strategy.isEnabled()) continue;
            strategy.start(name);
        }
    }

    public static void remove(ProfilingStrategy strategy) {
        strategies.remove(strategy);
    }
}

