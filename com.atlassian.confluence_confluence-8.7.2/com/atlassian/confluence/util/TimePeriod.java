/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Deprecated
public class TimePeriod
implements Serializable {
    private long period;
    private TimeUnit timeUnit;

    public TimePeriod(long time, TimeUnit timeUnit) {
        if (timeUnit == null) {
            throw new IllegalArgumentException("Time unit may not be null.");
        }
        this.period = time;
        this.timeUnit = timeUnit;
    }

    protected TimePeriod() {
        this(0L, TimeUnit.MILLISECONDS);
    }

    public long getPeriod() {
        return this.period;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public long convertTo(TimeUnit timeUnit) {
        return timeUnit.convert(this.period, this.timeUnit);
    }

    public String toString() {
        return this.period + " " + this.timeUnit.toString().toLowerCase();
    }
}

