/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import org.apache.jackrabbit.api.stats.TimeSeries;

public class TimeSeriesAverage
implements TimeSeries {
    private final TimeSeries value;
    private final TimeSeries counter;
    private final long missingValue;

    public TimeSeriesAverage(TimeSeries value, TimeSeries counter) {
        this(value, counter, 0L);
    }

    public TimeSeriesAverage(TimeSeries value, TimeSeries counter, long missingValue) {
        this.value = value;
        this.counter = counter;
        this.missingValue = missingValue;
    }

    @Override
    public long getMissingValue() {
        return this.missingValue;
    }

    @Override
    public long[] getValuePerSecond() {
        long[] values = this.value.getValuePerSecond();
        long[] counts = this.counter.getValuePerSecond();
        return this.divide(values, counts);
    }

    @Override
    public long[] getValuePerMinute() {
        long[] values = this.value.getValuePerMinute();
        long[] counts = this.counter.getValuePerMinute();
        return this.divide(values, counts);
    }

    @Override
    public synchronized long[] getValuePerHour() {
        long[] values = this.value.getValuePerHour();
        long[] counts = this.counter.getValuePerHour();
        return this.divide(values, counts);
    }

    @Override
    public synchronized long[] getValuePerWeek() {
        long[] values = this.value.getValuePerWeek();
        long[] counts = this.counter.getValuePerWeek();
        return this.divide(values, counts);
    }

    private long[] divide(long[] v, long[] c) {
        long[] avg = new long[v.length];
        for (int i = 0; i < v.length; ++i) {
            avg[i] = c[i] == 0L || v[i] == this.value.getMissingValue() || c[i] == this.counter.getMissingValue() ? this.missingValue : v[i] / c[i];
        }
        return avg;
    }
}

