/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Ticker
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.contributors.analytics;

import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import org.joda.time.Duration;

class Timer {
    private final Ticker ticker;
    private long startTimeNanos;
    private long cumulativeDurationNanos;

    Timer(Ticker ticker) {
        this.ticker = ticker;
    }

    void start() {
        this.startTimeNanos = this.ticker.read();
    }

    void stop() {
        this.cumulativeDurationNanos += this.ticker.read() - this.startTimeNanos;
    }

    Duration duration() {
        return Duration.millis((long)TimeUnit.MILLISECONDS.convert(this.cumulativeDurationNanos, TimeUnit.NANOSECONDS));
    }
}

