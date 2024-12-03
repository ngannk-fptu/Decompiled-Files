/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.event;

import com.atlassian.annotations.VisibleForTesting;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

public class DurationChecker {
    public static final String THRESHOLD_IN_SECONDS_KEY = "confluence.event.duration_checker.threshold_in_seconds";
    private final AtomicLong previousSavedTime;
    private final long thresholdInNanoSeconds;
    private final int thresholdInSeconds;
    private final LongSupplier nanoTime;

    public DurationChecker(int defaultThresholdInSeconds) {
        this(defaultThresholdInSeconds, System::nanoTime);
    }

    @VisibleForTesting
    public DurationChecker(int defaultThresholdInSeconds, LongSupplier nanoTime) {
        this.nanoTime = nanoTime;
        this.thresholdInSeconds = Integer.getInteger(THRESHOLD_IN_SECONDS_KEY, defaultThresholdInSeconds);
        this.thresholdInNanoSeconds = TimeUnit.SECONDS.toNanos(this.thresholdInSeconds);
        this.previousSavedTime = new AtomicLong();
    }

    public int getThresholdInSeconds() {
        return this.thresholdInSeconds;
    }

    public boolean thresholdElapsed() {
        long previousTime;
        long currentTime = this.nanoTime.getAsLong();
        if (currentTime - (previousTime = this.previousSavedTime.get()) > this.thresholdInNanoSeconds || previousTime == 0L) {
            return this.previousSavedTime.compareAndSet(previousTime, currentTime);
        }
        return false;
    }
}

