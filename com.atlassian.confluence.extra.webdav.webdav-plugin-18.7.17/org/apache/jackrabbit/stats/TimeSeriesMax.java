/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.Arrays;
import org.apache.jackrabbit.api.stats.TimeSeries;

public class TimeSeriesMax
implements TimeSeries {
    private final MaxValue max;
    private final long missingValue;
    private final long[] perSecond;
    private final long[] perMinute;
    private final long[] perHour;
    private final long[] perWeek;
    private int seconds;
    private int minutes;
    private int hours;
    private int weeks;

    public TimeSeriesMax() {
        this(0L);
    }

    public TimeSeriesMax(long missingValue) {
        this.missingValue = missingValue;
        this.max = new MaxValue(missingValue);
        this.perSecond = TimeSeriesMax.newArray(60, missingValue);
        this.perMinute = TimeSeriesMax.newArray(60, missingValue);
        this.perHour = TimeSeriesMax.newArray(168, missingValue);
        this.perWeek = TimeSeriesMax.newArray(156, missingValue);
    }

    private static long[] newArray(int size, long value) {
        long[] array = new long[size];
        Arrays.fill(array, value);
        return array;
    }

    public void recordValue(long value) {
        this.max.setIfMaximal(value);
    }

    public synchronized void recordOneSecond() {
        this.perSecond[this.seconds++] = this.max.getAndSetValue(this.missingValue);
        if (this.seconds == this.perSecond.length) {
            this.seconds = 0;
            this.perMinute[this.minutes++] = this.max(this.perSecond);
        }
        if (this.minutes == this.perMinute.length) {
            this.minutes = 0;
            this.perHour[this.hours++] = this.max(this.perMinute);
        }
        if (this.hours == this.perHour.length) {
            this.hours = 0;
            this.perWeek[this.weeks++] = this.max(this.perHour);
        }
        if (this.weeks == this.perWeek.length) {
            this.weeks = 0;
        }
    }

    @Override
    public long getMissingValue() {
        return this.missingValue;
    }

    @Override
    public synchronized long[] getValuePerSecond() {
        return TimeSeriesMax.cyclicCopyFrom(this.perSecond, this.seconds);
    }

    @Override
    public synchronized long[] getValuePerMinute() {
        return TimeSeriesMax.cyclicCopyFrom(this.perMinute, this.minutes);
    }

    @Override
    public synchronized long[] getValuePerHour() {
        return TimeSeriesMax.cyclicCopyFrom(this.perHour, this.hours);
    }

    @Override
    public synchronized long[] getValuePerWeek() {
        return TimeSeriesMax.cyclicCopyFrom(this.perWeek, this.weeks);
    }

    private long max(long[] array) {
        long max = this.missingValue;
        for (long v : array) {
            if (max == this.missingValue) {
                max = v;
                continue;
            }
            if (v == this.missingValue) continue;
            max = Math.max(max, v);
        }
        return max;
    }

    private static long[] cyclicCopyFrom(long[] array, int pos) {
        long[] reverse = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            reverse[i] = array[(pos + i) % array.length];
        }
        return reverse;
    }

    private class MaxValue {
        private long max;

        public MaxValue(long max) {
            this.max = max;
        }

        public synchronized long getAndSetValue(long value) {
            long v = this.max;
            this.max = value;
            return v;
        }

        public synchronized void setIfMaximal(long value) {
            if (this.max == TimeSeriesMax.this.missingValue) {
                this.max = value;
            } else if (value != TimeSeriesMax.this.missingValue) {
                this.max = Math.max(this.max, value);
            }
        }
    }
}

