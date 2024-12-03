/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;

public interface TaskScheduler {
    default public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Nullable
    public ScheduledFuture<?> schedule(Runnable var1, Trigger var2);

    default public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return this.schedule(task, Date.from(startTime));
    }

    public ScheduledFuture<?> schedule(Runnable var1, Date var2);

    default public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        return this.scheduleAtFixedRate(task, Date.from(startTime), period.toMillis());
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, Date var2, long var3);

    default public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return this.scheduleAtFixedRate(task, period.toMillis());
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2);

    default public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        return this.scheduleWithFixedDelay(task, Date.from(startTime), delay.toMillis());
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, Date var2, long var3);

    default public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return this.scheduleWithFixedDelay(task, delay.toMillis());
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2);
}

