/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.CompositeTicker;
import com.atlassian.util.profiling.MetricTag;
import com.atlassian.util.profiling.MetricTimer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.StrategiesRegistry;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Tickers;
import com.atlassian.util.profiling.Timer;
import com.atlassian.util.profiling.strategy.MetricStrategy;
import com.atlassian.util.profiling.strategy.ProfilerStrategy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class Timers {
    private static final ProfilerConfiguration CONFIGURATION = new ProfilerConfiguration();
    private static final Logger log = LoggerFactory.getLogger(Timers.class);

    private Timers() {
        throw new UnsupportedOperationException("Timers is an utility class and should not be instantiated");
    }

    @Nonnull
    public static Timer concat(Timer ... timers) {
        return new CompositeTimer(timers);
    }

    @Nonnull
    public static ProfilerConfiguration getConfiguration() {
        return CONFIGURATION;
    }

    public static void onRequestEnd() {
        for (ProfilerStrategy profilerStrategy : StrategiesRegistry.getProfilerStrategies()) {
            try {
                profilerStrategy.onRequestEnd();
            }
            catch (Exception e) {
                log.warn("Error cleaning up profiler state for {}", (Object)profilerStrategy.getClass().getName(), (Object)e);
            }
        }
        for (MetricStrategy metricStrategy : StrategiesRegistry.getMetricStrategies()) {
            try {
                metricStrategy.onRequestEnd();
            }
            catch (Exception e) {
                log.warn("Error cleaning up metrics state for {}", (Object)metricStrategy.getClass().getName(), (Object)e);
            }
        }
    }

    @Nonnull
    public static Ticker start(String name) {
        if (CONFIGURATION.isEnabled()) {
            return Timers.timer(name).start(new String[0]);
        }
        return Ticker.NO_OP;
    }

    @Nonnull
    public static Ticker startWithMetric(String timerName) {
        if (CONFIGURATION.isEnabled()) {
            return Timers.timerWithMetric(timerName).start(new String[0]);
        }
        return Metrics.startTimer(timerName);
    }

    @Deprecated
    @Nonnull
    public static Ticker startWithMetric(String timerName, String metricName) {
        if (CONFIGURATION.isEnabled()) {
            return Timers.timerWithMetric(timerName, metricName).start(new String[0]);
        }
        return Metrics.startTimer(metricName);
    }

    @Deprecated
    @Nonnull
    public static Ticker startWithMetric(String timerName, MetricTag.RequiredMetricTag ... metricTags) {
        if (CONFIGURATION.isEnabled()) {
            return Timers.timerWithMetric(timerName, metricTags).start(new String[0]);
        }
        return Metrics.startTimer(timerName, metricTags);
    }

    @Deprecated
    @Nonnull
    public static Ticker startWithMetric(String timerName, String metricName, MetricTag.RequiredMetricTag ... metricTags) {
        if (CONFIGURATION.isEnabled()) {
            return Timers.timerWithMetric(timerName, metricName, metricTags).start(new String[0]);
        }
        return Metrics.startTimer(metricName, metricTags);
    }

    @Nonnull
    public static Timer timer(String name) {
        return new DefaultTimer(Objects.requireNonNull(name, "name"));
    }

    @Deprecated
    @Nonnull
    public static Timer timerWithMetric(String traceName, String metricName) {
        return Timers.timerWithMetric(traceName, metricName, Collections.emptySet());
    }

    @Deprecated
    @Nonnull
    public static Timer timerWithMetric(String traceName, String metricName, MetricTag.RequiredMetricTag ... tags) {
        return Timers.timerWithMetric(traceName, metricName, Arrays.asList(tags));
    }

    @Deprecated
    @Nonnull
    public static Timer timerWithMetric(String traceName, String metricName, Collection<MetricTag.RequiredMetricTag> tags) {
        return Timers.timerWithMetric(traceName, Metrics.timer(metricName, tags));
    }

    @Nonnull
    public static Timer timerWithMetric(String traceName, Metrics.Builder metricBuilder) {
        return Timers.timerWithMetric(traceName, metricBuilder.timer());
    }

    @Nonnull
    public static Timer timerWithMetric(String traceName, final MetricTimer metricTimer) {
        final Timer timer = Timers.timer(traceName);
        return new Timer(){

            @Override
            @Nonnull
            public Ticker start(Object ... callParameters) {
                Ticker traceTicker = timer.start(callParameters);
                Ticker metricTicker = metricTimer.start();
                return Tickers.of(traceTicker, metricTicker);
            }
        };
    }

    @Nonnull
    public static Timer timerWithMetric(String name) {
        return Timers.timerWithMetric(name, name);
    }

    @Deprecated
    @Nonnull
    public static Timer timerWithMetric(String name, Collection<MetricTag.RequiredMetricTag> tags) {
        return Timers.timerWithMetric(name, name, tags);
    }

    @Deprecated
    @Nonnull
    public static Timer timerWithMetric(String name, MetricTag.RequiredMetricTag ... tags) {
        return Timers.timerWithMetric(name, name, Arrays.asList(tags));
    }

    private static class DefaultTimer
    implements Timer {
        private static final Logger log = LoggerFactory.getLogger(DefaultTimer.class);
        private final String name;

        DefaultTimer(String name) {
            this.name = name;
        }

        @Override
        @Nonnull
        public Ticker start(Object ... callParameters) {
            String frameName;
            if (!CONFIGURATION.isEnabled()) {
                return Ticker.NO_OP;
            }
            if (callParameters == null || callParameters.length == 0) {
                frameName = this.name;
            } else {
                StringBuilder builder = new StringBuilder(this.name);
                boolean added = false;
                for (Object p : callParameters) {
                    String param;
                    String string = param = p == null ? null : String.valueOf(p);
                    if (param == null || param.isEmpty()) continue;
                    if (added) {
                        builder.append(", ");
                    } else {
                        builder.append("(");
                        added = true;
                    }
                    builder.append(param);
                }
                if (added) {
                    builder.append(")");
                }
                frameName = builder.toString();
            }
            CompositeTicker compositeTicker = null;
            for (ProfilerStrategy strategy : StrategiesRegistry.getProfilerStrategies()) {
                try {
                    compositeTicker = Tickers.addTicker(strategy.start(frameName), compositeTicker);
                }
                catch (Exception e) {
                    log.warn("Failed to start profiling frame for {}", (Object)frameName, (Object)e);
                }
            }
            return compositeTicker == null ? Ticker.NO_OP : compositeTicker;
        }
    }

    @ParametersAreNonnullByDefault
    private static class CompositeTimer
    implements Timer {
        private final Timer[] timers;

        private CompositeTimer(Timer[] timers) {
            this.timers = Objects.requireNonNull(timers, "timers");
        }

        @Override
        @Nonnull
        public Ticker start(Object ... callParameters) {
            CompositeTicker ticker = null;
            for (Timer timer : this.timers) {
                if (timer == null) continue;
                ticker = Tickers.addTicker(timer.start(callParameters), ticker);
            }
            return ticker == null ? Ticker.NO_OP : ticker;
        }
    }
}

