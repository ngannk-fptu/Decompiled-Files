/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.EventPublisher
 *  io.micrometer.core.instrument.Clock
 *  io.micrometer.core.instrument.Counter
 *  io.micrometer.core.instrument.DistributionSummary
 *  io.micrometer.core.instrument.FunctionCounter
 *  io.micrometer.core.instrument.FunctionTimer
 *  io.micrometer.core.instrument.Gauge
 *  io.micrometer.core.instrument.LongTaskTimer
 *  io.micrometer.core.instrument.Meter
 *  io.micrometer.core.instrument.TimeGauge
 *  io.micrometer.core.instrument.Timer
 *  io.micrometer.core.instrument.config.MeterFilter
 *  io.micrometer.core.instrument.step.StepMeterRegistry
 *  io.micrometer.core.instrument.step.StepRegistryConfig
 *  io.micrometer.core.instrument.util.NamedThreadFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling.micrometer.analytics;

import com.atlassian.annotations.Internal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.micrometer.analytics.AnalyticsRegistryConfig;
import com.atlassian.util.profiling.micrometer.analytics.events.CounterEvent;
import com.atlassian.util.profiling.micrometer.analytics.events.FunctionTimerEvent;
import com.atlassian.util.profiling.micrometer.analytics.events.GaugeEvent;
import com.atlassian.util.profiling.micrometer.analytics.events.LongTaskTimerEvent;
import com.atlassian.util.profiling.micrometer.analytics.events.MeterEvent;
import com.atlassian.util.profiling.micrometer.analytics.events.SummaryEvent;
import com.atlassian.util.profiling.micrometer.analytics.events.TimerEvent;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@Internal
public class AnalyticsMeterRegistry
extends StepMeterRegistry {
    public static final ThreadFactory DEFAULT_THREAD_FACTORY = new NamedThreadFactory("atlassian-analytics-metrics-publisher");
    public static final String SEND_ANALYTICS_TAG = "atl-analytics";
    private final EventPublisher eventPublisher;

    public AnalyticsMeterRegistry(@Nonnull AnalyticsRegistryConfig config, @Nonnull EventPublisher eventPublisher) {
        this(config, eventPublisher, Clock.SYSTEM, DEFAULT_THREAD_FACTORY);
    }

    public AnalyticsMeterRegistry(@Nonnull AnalyticsRegistryConfig config, @Nonnull EventPublisher eventPublisher, @Nonnull Clock clock, @Nonnull ThreadFactory threadFactory) {
        super((StepRegistryConfig)config, clock);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.start(Objects.requireNonNull(threadFactory));
        this.config().meterFilter(MeterFilter.denyUnless(id -> Boolean.parseBoolean(id.getTag(SEND_ANALYTICS_TAG))));
    }

    protected void publish() {
        this.forEachMeter(m -> {
            Optional event = (Optional)m.match(gauge -> Optional.of(new GaugeEvent((Gauge)gauge)), counter -> Optional.of(new CounterEvent((Counter)counter)).filter(e -> e.getCount() != 0.0), timer -> Optional.of(new TimerEvent((Timer)timer, this.getBaseTimeUnit())).filter(e -> e.getCount() != 0L), summary -> Optional.of(new SummaryEvent((DistributionSummary)summary)).filter(e -> e.getCount() != 0L), longTaskTimer -> Optional.of(new LongTaskTimerEvent((LongTaskTimer)longTaskTimer, this.getBaseTimeUnit())).filter(e -> e.getActiveTasks() != 0), timeGauge -> Optional.of(new GaugeEvent((TimeGauge)timeGauge, this.getBaseTimeUnit())).filter(e -> e.getValue() != 0.0), counter -> Optional.of(new CounterEvent((FunctionCounter)counter)).filter(e -> e.getCount() != 0.0), functionTimer -> Optional.of(new FunctionTimerEvent((FunctionTimer)functionTimer, this.getBaseTimeUnit())).filter(e -> e.getCount() != 0.0), other -> Optional.of(new MeterEvent((Meter)other)));
            event.ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        });
    }

    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}

