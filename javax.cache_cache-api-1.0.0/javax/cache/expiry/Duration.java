/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.expiry;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Duration
implements Serializable {
    public static final long serialVersionUID = 201305101442L;
    public static final Duration ETERNAL = new Duration();
    public static final Duration ONE_DAY = new Duration(TimeUnit.DAYS, 1L);
    public static final Duration ONE_HOUR = new Duration(TimeUnit.HOURS, 1L);
    public static final Duration THIRTY_MINUTES = new Duration(TimeUnit.MINUTES, 30L);
    public static final Duration TWENTY_MINUTES = new Duration(TimeUnit.MINUTES, 20L);
    public static final Duration TEN_MINUTES = new Duration(TimeUnit.MINUTES, 10L);
    public static final Duration FIVE_MINUTES = new Duration(TimeUnit.MINUTES, 5L);
    public static final Duration ONE_MINUTE = new Duration(TimeUnit.MINUTES, 1L);
    public static final Duration ZERO = new Duration(TimeUnit.SECONDS, 0L);
    private final TimeUnit timeUnit;
    private final long durationAmount;

    public Duration() {
        this.timeUnit = null;
        this.durationAmount = 0L;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Duration(TimeUnit timeUnit, long durationAmount) {
        if (timeUnit == null) {
            if (durationAmount != 0L) throw new NullPointerException();
            this.timeUnit = null;
            this.durationAmount = 0L;
            return;
        } else {
            switch (timeUnit) {
                case NANOSECONDS: 
                case MICROSECONDS: {
                    throw new IllegalArgumentException("Must specify a TimeUnit of milliseconds or higher.");
                }
            }
            this.timeUnit = timeUnit;
            if (durationAmount < 0L) {
                throw new IllegalArgumentException("Cannot specify a negative durationAmount.");
            }
            this.durationAmount = durationAmount;
        }
    }

    public Duration(long startTime, long endTime) {
        if (startTime == Long.MAX_VALUE || endTime == Long.MAX_VALUE) {
            this.timeUnit = null;
            this.durationAmount = 0L;
        } else {
            if (startTime < 0L) {
                throw new IllegalArgumentException("Cannot specify a negative startTime.");
            }
            if (endTime < 0L) {
                throw new IllegalArgumentException("Cannot specify a negative endTime.");
            }
            this.timeUnit = TimeUnit.MILLISECONDS;
            this.durationAmount = Math.max(startTime, endTime) - Math.min(startTime, endTime);
        }
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public long getDurationAmount() {
        return this.durationAmount;
    }

    public boolean isEternal() {
        return this.timeUnit == null && this.durationAmount == 0L;
    }

    public boolean isZero() {
        return this.timeUnit != null && this.durationAmount == 0L;
    }

    public long getAdjustedTime(long time) {
        if (this.isEternal()) {
            return Long.MAX_VALUE;
        }
        return time + this.timeUnit.toMillis(this.durationAmount);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Duration duration = (Duration)other;
        if (this.timeUnit == null && duration.timeUnit == null && this.durationAmount == duration.durationAmount) {
            return true;
        }
        if (this.timeUnit != null && duration.timeUnit != null) {
            long time2;
            long time1 = this.timeUnit.toMillis(this.durationAmount);
            return time1 == (time2 = duration.timeUnit.toMillis(duration.durationAmount));
        }
        return false;
    }

    public int hashCode() {
        return this.timeUnit == null ? -1 : (int)this.timeUnit.toMillis(this.durationAmount);
    }
}

