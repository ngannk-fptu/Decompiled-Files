/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.poller;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ScheduleInterval {
    private final int delay;
    private final TimeUnit timeUnit;

    public static ScheduleInterval of(int delay, TimeUnit timeUnit) {
        return new ScheduleInterval(delay, timeUnit);
    }

    private ScheduleInterval(int delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public int getDelay() {
        return this.delay;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScheduleInterval that = (ScheduleInterval)o;
        return this.delay == that.delay && this.timeUnit == that.timeUnit;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.delay, this.timeUnit});
    }
}

