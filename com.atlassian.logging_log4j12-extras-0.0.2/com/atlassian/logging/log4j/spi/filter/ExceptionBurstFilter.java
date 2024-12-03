/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.spi.Filter
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.atlassian.logging.log4j.spi.filter;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class ExceptionBurstFilter
extends Filter {
    private static final int HASH_SHIFT = 32;
    private String exceptionFqcn;
    private int burstDurationSecs = 1;
    private int maxBurst = 1;
    private int exceptionMaxDepth = 10;
    private long burstIntervalNanos = 1000000000L;
    private final DelayQueue<LogDelay> history = new DelayQueue();
    private final Queue<LogDelay> available = new ConcurrentLinkedQueue<LogDelay>();

    public String getExceptionFqcn() {
        return this.exceptionFqcn;
    }

    public void setExceptionFqcn(String exceptionFqcn) {
        this.exceptionFqcn = exceptionFqcn;
    }

    public int getBurstDurationSecs() {
        return this.burstDurationSecs;
    }

    public void setBurstDurationSecs(int burstDurationSecs) {
        this.burstDurationSecs = burstDurationSecs;
    }

    public int getMaxBurst() {
        return this.maxBurst;
    }

    public void setMaxBurst(int maxBurst) {
        this.maxBurst = maxBurst;
    }

    public int getExceptionMaxDepth() {
        return this.exceptionMaxDepth;
    }

    public void setExceptionMaxDepth(int exceptionMaxDepth) {
        this.exceptionMaxDepth = exceptionMaxDepth;
    }

    public void activateOptions() {
        super.activateOptions();
        this.burstIntervalNanos = Duration.ofSeconds(this.burstDurationSecs).toNanos() / (long)this.maxBurst;
        IntStream.rangeClosed(1, this.maxBurst).forEach(ignored -> this.available.add(new LogDelay(0L)));
    }

    public int decide(LoggingEvent event) {
        if (this.causedByConfiguredException(event)) {
            LogDelay delay = (LogDelay)this.history.poll();
            while (delay != null) {
                this.available.add(delay);
                delay = (LogDelay)this.history.poll();
            }
            delay = this.available.poll();
            if (delay != null) {
                delay.setDelayNanos(this.burstIntervalNanos);
                this.history.add(delay);
                return 1;
            }
            return -1;
        }
        return 0;
    }

    boolean causedByConfiguredException(LoggingEvent event) {
        if (this.exceptionFqcn == null || event.getThrowableInformation() == null) {
            return false;
        }
        Throwable cause = event.getThrowableInformation().getThrowable();
        for (int attemptRemaining = this.exceptionMaxDepth; cause != null && attemptRemaining > 0; cause = cause.getCause(), --attemptRemaining) {
            if (!this.exceptionFqcn.equals(cause.getClass().getName())) continue;
            return true;
        }
        return false;
    }

    private static class LogDelay
    implements Delayed {
        private volatile long expireTime;

        LogDelay(long expireTime) {
            this.expireTime = expireTime;
        }

        public void setDelayNanos(long delayNanos) {
            this.expireTime = delayNanos + System.nanoTime();
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            return timeUnit.convert(this.expireTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            long diff = this.expireTime - ((LogDelay)delayed).expireTime;
            return Long.signum(diff);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            LogDelay logDelay = (LogDelay)o;
            return this.expireTime == logDelay.expireTime;
        }

        public int hashCode() {
            return (int)(this.expireTime ^ this.expireTime >>> 32);
        }
    }
}

