/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Ticker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.profiling;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class TimedAnalyticsImpl
implements TimedAnalytics {
    private static final Logger LOG = LoggerFactory.getLogger(TimedAnalyticsImpl.class);
    private final Consumer<TimedAnalyticsEvent> consumer;
    private final Stopwatch stopwatch;

    public TimedAnalyticsImpl(Consumer<TimedAnalyticsEvent> consumer) {
        this(consumer, Ticker.systemTicker());
    }

    @VisibleForTesting
    TimedAnalyticsImpl(Consumer<TimedAnalyticsEvent> consumer, Ticker stopwatchTicker) {
        this.consumer = Objects.requireNonNull(consumer);
        this.stopwatch = Stopwatch.createStarted((Ticker)Objects.requireNonNull(stopwatchTicker));
    }

    @Override
    public com.atlassian.util.profiling.Ticker start(String eventName) {
        return this.startAt(eventName, this.stopwatch.elapsed());
    }

    @Override
    public com.atlassian.util.profiling.Ticker startAt(String eventName, Duration at) {
        return new AnalyticsTicker(Objects.requireNonNull(eventName), Objects.requireNonNull(at));
    }

    @ParametersAreNonnullByDefault
    public static class TimedAnalyticsEvent {
        private static String correlationId = UUID.randomUUID().toString();
        private final String name;
        private final long start;
        private final long end;
        private final long duration;

        public TimedAnalyticsEvent(String name, Duration start, Duration end) {
            this.name = Objects.requireNonNull(name);
            this.start = Objects.requireNonNull(start).toMillis();
            this.end = Objects.requireNonNull(end).toMillis();
            this.duration = end.minus(start).toMillis();
        }

        public String getCorrelationId() {
            return correlationId;
        }

        @EventName
        public String getName() {
            return this.name;
        }

        public long getStart() {
            return this.start;
        }

        public long getEnd() {
            return this.end;
        }

        public long getDuration() {
            return this.duration;
        }
    }

    private class AnalyticsTicker
    implements com.atlassian.util.profiling.Ticker {
        private final Duration start;
        private final String name;

        public AnalyticsTicker(String name, Duration start) {
            this.name = name;
            this.start = start;
            LOG.info("Started: {}", (Object)name);
        }

        public void close() {
            TimedAnalyticsEvent event = new TimedAnalyticsEvent(this.name, this.start, TimedAnalyticsImpl.this.stopwatch.elapsed());
            LOG.info("Completed: {}, duration: {}", (Object)this.name, (Object)event.duration);
            TimedAnalyticsImpl.this.consumer.accept(event);
        }
    }
}

