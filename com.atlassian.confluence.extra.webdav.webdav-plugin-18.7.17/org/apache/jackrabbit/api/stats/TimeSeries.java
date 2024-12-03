/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.stats;

public interface TimeSeries {
    public long[] getValuePerSecond();

    public long[] getValuePerMinute();

    public long[] getValuePerHour();

    public long[] getValuePerWeek();

    public long getMissingValue();
}

