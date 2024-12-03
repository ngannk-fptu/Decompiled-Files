/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Stopwatch
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.impl.seraph;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
class TimingAccumulator {
    private long totalElapsedMicroSeconds;
    private int executionCount;
    private Long maxElapsedMicroSeconds;
    private Long minElapsedMicroSeconds;

    TimingAccumulator() {
    }

    public void accumulateOperation(long elapsedMicros) {
        ++this.executionCount;
        this.totalElapsedMicroSeconds += elapsedMicros;
        this.maxElapsedMicroSeconds = this.maxElapsedMicroSeconds == null ? elapsedMicros : Math.max(this.maxElapsedMicroSeconds, elapsedMicros);
        this.minElapsedMicroSeconds = this.minElapsedMicroSeconds == null ? elapsedMicros : Math.min(this.minElapsedMicroSeconds, elapsedMicros);
    }

    private MetricsEvent createEvent(String eventName) {
        return new MetricsEvent(eventName, this.totalElapsedMicroSeconds, this.executionCount, this.maxElapsedMicroSeconds, this.minElapsedMicroSeconds);
    }

    public boolean hasData() {
        return this.executionCount > 0;
    }

    public void publishEvent(EventPublisher eventPublisher, String eventName) {
        if (this.hasData()) {
            eventPublisher.publish((Object)this.createEvent(eventName));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T accumulateOperation(Supplier<T> impl) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            T t = impl.get();
            return t;
        }
        finally {
            this.accumulateOperation(stopwatch.elapsed(TimeUnit.MICROSECONDS));
        }
    }

    public static class MetricsEvent {
        private final String eventName;
        private final long totalElapsedMicroSeconds;
        private final int executionCount;
        private final long maxElapsedMicroSeconds;
        private final long minElapsedMicroSeconds;

        MetricsEvent(String eventName, long totalElapsedMicroSeconds, int executionCount, long maxElapsedMicroSeconds, long minElapsedMicroSeconds) {
            this.eventName = eventName;
            this.totalElapsedMicroSeconds = totalElapsedMicroSeconds;
            this.executionCount = executionCount;
            this.maxElapsedMicroSeconds = maxElapsedMicroSeconds;
            this.minElapsedMicroSeconds = minElapsedMicroSeconds;
        }

        @EventName
        public String getEventName() {
            return this.eventName;
        }

        public long getTotalElapsedMicroSeconds() {
            return this.totalElapsedMicroSeconds;
        }

        public int getExecutionCount() {
            return this.executionCount;
        }

        public long getMaxElapsedMicroSeconds() {
            return this.maxElapsedMicroSeconds;
        }

        public long getMinElapsedMicroSeconds() {
            return this.minElapsedMicroSeconds;
        }
    }
}

