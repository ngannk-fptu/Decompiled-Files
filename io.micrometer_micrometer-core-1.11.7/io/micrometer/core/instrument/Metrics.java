/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class Metrics {
    public static final CompositeMeterRegistry globalRegistry = new CompositeMeterRegistry();
    private static final More more = new More();

    public static void addRegistry(MeterRegistry registry) {
        globalRegistry.add(registry);
    }

    public static void removeRegistry(MeterRegistry registry) {
        globalRegistry.remove(registry);
    }

    public static Counter counter(String name, Iterable<Tag> tags) {
        return globalRegistry.counter(name, tags);
    }

    public static Counter counter(String name, String ... tags) {
        return globalRegistry.counter(name, tags);
    }

    public static DistributionSummary summary(String name, Iterable<Tag> tags) {
        return globalRegistry.summary(name, tags);
    }

    public static DistributionSummary summary(String name, String ... tags) {
        return globalRegistry.summary(name, tags);
    }

    public static Timer timer(String name, Iterable<Tag> tags) {
        return globalRegistry.timer(name, tags);
    }

    public static Timer timer(String name, String ... tags) {
        return globalRegistry.timer(name, tags);
    }

    public static More more() {
        return more;
    }

    @Nullable
    public static <T> T gauge(String name, Iterable<Tag> tags, T obj, ToDoubleFunction<T> valueFunction) {
        return globalRegistry.gauge(name, tags, obj, valueFunction);
    }

    @Nullable
    public static <T extends Number> T gauge(String name, Iterable<Tag> tags, T number) {
        return globalRegistry.gauge(name, tags, number);
    }

    @Nullable
    public static <T extends Number> T gauge(String name, T number) {
        return globalRegistry.gauge(name, number);
    }

    @Nullable
    public static <T> T gauge(String name, T obj, ToDoubleFunction<T> valueFunction) {
        return globalRegistry.gauge(name, obj, valueFunction);
    }

    @Nullable
    public static <T extends Collection<?>> T gaugeCollectionSize(String name, Iterable<Tag> tags, T collection) {
        return globalRegistry.gaugeCollectionSize(name, tags, collection);
    }

    @Nullable
    public static <T extends Map<?, ?>> T gaugeMapSize(String name, Iterable<Tag> tags, T map) {
        return globalRegistry.gaugeMapSize(name, tags, map);
    }

    public static class More {
        public LongTaskTimer longTaskTimer(String name, String ... tags) {
            return globalRegistry.more().longTaskTimer(name, tags);
        }

        public LongTaskTimer longTaskTimer(String name, Iterable<Tag> tags) {
            return globalRegistry.more().longTaskTimer(name, tags);
        }

        public <T> FunctionCounter counter(String name, Iterable<Tag> tags, T obj, ToDoubleFunction<T> countFunction) {
            return globalRegistry.more().counter(name, tags, obj, countFunction);
        }

        public <T extends Number> FunctionCounter counter(String name, Iterable<Tag> tags, T number) {
            return globalRegistry.more().counter(name, tags, number);
        }

        public <T> TimeGauge timeGauge(String name, Iterable<Tag> tags, T obj, TimeUnit timeFunctionUnit, ToDoubleFunction<T> timeFunction) {
            return globalRegistry.more().timeGauge(name, tags, obj, timeFunctionUnit, timeFunction);
        }

        public <T> FunctionTimer timer(String name, Iterable<Tag> tags, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
            return globalRegistry.more().timer(name, tags, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit);
        }
    }
}

