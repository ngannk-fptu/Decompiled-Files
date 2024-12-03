/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.internal.throttling.CallTiming;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallThrottler {
    private static final Logger logger = LoggerFactory.getLogger(CallThrottler.class);
    private final List<TrackedCall> calls = new CopyOnWriteArrayList<TrackedCall>();
    private final Supplier<Instant> clock;
    private final Duration maxAllowedWallclockTime;
    private final Duration timeWindow;
    private final int allowedWallClockPercentage;

    public CallThrottler(Duration timeWindow, int allowedWallClockPercentage) {
        this(Instant::now, timeWindow, allowedWallClockPercentage);
    }

    public CallThrottler(Supplier<Instant> clock, Duration timeWindow, int allowedWallClockPercentage) {
        this.clock = clock;
        this.timeWindow = timeWindow;
        this.allowedWallClockPercentage = allowedWallClockPercentage;
        this.maxAllowedWallclockTime = Duration.ofMillis((long)Runtime.getRuntime().availableProcessors() * timeWindow.toMillis() * (long)allowedWallClockPercentage / 100L);
    }

    public boolean isBudgetExceeded() {
        Instant timeWindowStart = this.getTimeWindowStart();
        long consumedWallclockMs = this.calls.stream().map(TrackedCall::getCallTiming).filter(t -> t.mayEndAfter(timeWindowStart)).mapToLong(CallTiming::getCallDurationMs).map(duration -> Math.min(this.timeWindow.toMillis(), duration)).sum();
        if (logger.isDebugEnabled()) {
            long maxAllowedWallclockTimeMs = this.maxAllowedWallclockTime.toMillis();
            long budgetUtilisation = maxAllowedWallclockTimeMs <= 0L ? 100L : 100L * consumedWallclockMs / maxAllowedWallclockTimeMs;
            logger.debug("Tracking {} calls, since {}, budget utilisation: {}%", new Object[]{this.calls.size(), timeWindowStart, budgetUtilisation});
        }
        return Duration.ofMillis(consumedWallclockMs).compareTo(this.maxAllowedWallclockTime) > 0;
    }

    public Map<String, Duration> getStats() {
        return this.calls.stream().collect(Collectors.toMap(TrackedCall::getContext, e -> Duration.ofMillis(e.getCallTiming().getCallDurationMs()), Duration::plus));
    }

    public TrackedCall startTracking(String context) {
        this.removeCallsOlderThan(this.getTimeWindowStart());
        TrackedCall trackedCall = new TrackedCall(context);
        this.calls.add(trackedCall);
        return trackedCall;
    }

    public int getAllowedWallClockPercentage() {
        return this.allowedWallClockPercentage;
    }

    public Duration getTimeWindow() {
        return this.timeWindow;
    }

    private void removeCallsOlderThan(Instant instant) {
        this.calls.removeIf(e -> e.getCallTiming().endedBefore(instant));
    }

    private Instant getTimeWindowStart() {
        return this.clock.get().minus(this.timeWindow);
    }

    public final class TrackedCall
    implements AutoCloseable {
        private final String context;
        private volatile CallTiming callTiming;

        private TrackedCall(String context) {
            this.context = context;
            this.callTiming = CallTiming.start(CallThrottler.this.clock);
        }

        @Override
        public void close() {
            this.callTiming = this.callTiming.end();
        }

        public String getContext() {
            return this.context;
        }

        public CallTiming getCallTiming() {
            return this.callTiming;
        }
    }
}

