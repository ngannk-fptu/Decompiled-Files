/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.jackrabbit.api.stats.RepositoryStatistics;
import org.apache.jackrabbit.api.stats.TimeSeries;

public class TimeSeriesRecorder
implements TimeSeries {
    private final AtomicLong counter;
    private final boolean resetValueEachSecond;
    private final long missingValue;
    private final long[] valuePerSecond;
    private final long[] valuePerMinute;
    private final long[] valuePerHour;
    private final long[] valuePerWeek;
    private int seconds;
    private int minutes;
    private int hours;
    private int weeks;

    public TimeSeriesRecorder(RepositoryStatistics.Type type) {
        this(type.isResetValueEachSecond());
    }

    public TimeSeriesRecorder(boolean resetValueEachSecond) {
        this(resetValueEachSecond, 0L);
    }

    public TimeSeriesRecorder(boolean resetValueEachSecond, long missingValue) {
        this.resetValueEachSecond = resetValueEachSecond;
        this.missingValue = missingValue;
        this.counter = new AtomicLong(missingValue);
        this.valuePerSecond = TimeSeriesRecorder.newArray(60, missingValue);
        this.valuePerMinute = TimeSeriesRecorder.newArray(60, missingValue);
        this.valuePerHour = TimeSeriesRecorder.newArray(168, missingValue);
        this.valuePerWeek = TimeSeriesRecorder.newArray(156, missingValue);
    }

    private static long[] newArray(int size, long value) {
        long[] array = new long[size];
        Arrays.fill(array, value);
        return array;
    }

    public AtomicLong getCounter() {
        return this.counter;
    }

    public synchronized void recordOneSecond() {
        this.valuePerSecond[this.seconds++] = this.resetValueEachSecond ? this.counter.getAndSet(this.missingValue) : this.counter.get();
        if (this.seconds == this.valuePerSecond.length) {
            this.seconds = 0;
            this.valuePerMinute[this.minutes++] = this.aggregate(this.valuePerSecond);
        }
        if (this.minutes == this.valuePerMinute.length) {
            this.minutes = 0;
            this.valuePerHour[this.hours++] = this.aggregate(this.valuePerMinute);
        }
        if (this.hours == this.valuePerHour.length) {
            this.hours = 0;
            this.valuePerWeek[this.weeks++] = this.aggregate(this.valuePerHour);
        }
        if (this.weeks == this.valuePerWeek.length) {
            this.weeks = 0;
        }
    }

    @Override
    public long getMissingValue() {
        return this.missingValue;
    }

    @Override
    public synchronized long[] getValuePerSecond() {
        return TimeSeriesRecorder.cyclicCopyFrom(this.valuePerSecond, this.seconds);
    }

    @Override
    public synchronized long[] getValuePerMinute() {
        return TimeSeriesRecorder.cyclicCopyFrom(this.valuePerMinute, this.minutes);
    }

    @Override
    public synchronized long[] getValuePerHour() {
        return TimeSeriesRecorder.cyclicCopyFrom(this.valuePerHour, this.hours);
    }

    @Override
    public synchronized long[] getValuePerWeek() {
        return TimeSeriesRecorder.cyclicCopyFrom(this.valuePerWeek, this.weeks);
    }

    private long aggregate(long[] array) {
        long sum = 0L;
        int count = 0;
        for (long value : array) {
            if (value == this.missingValue) continue;
            ++count;
            sum += value;
        }
        if (count == 0) {
            return this.missingValue;
        }
        if (this.resetValueEachSecond) {
            return sum;
        }
        return Math.round((double)sum / (double)count);
    }

    private static long[] cyclicCopyFrom(long[] array, int pos) {
        long[] reverse = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            reverse[i] = array[(pos + i) % array.length];
        }
        return reverse;
    }
}

