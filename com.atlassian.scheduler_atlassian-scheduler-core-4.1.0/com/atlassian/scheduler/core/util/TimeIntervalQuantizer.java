/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.scheduler.core.util;

import com.google.common.base.Preconditions;

public class TimeIntervalQuantizer {
    private static final int MILLIS_PER_MINUTE = 60000;

    public static long quantize(long intervalInMilliseconds, int resolution) {
        Preconditions.checkArgument((intervalInMilliseconds >= 0L ? 1 : 0) != 0, (Object)"intervalInMilliseconds cannot be negative");
        if (resolution <= 1) {
            return intervalInMilliseconds;
        }
        long remainder = intervalInMilliseconds % (long)resolution;
        if (remainder == 0L) {
            return intervalInMilliseconds;
        }
        return TimeIntervalQuantizer.roundUpWithBoundsCheck(intervalInMilliseconds - remainder, resolution);
    }

    private static long roundUpWithBoundsCheck(long floor, int resolution) {
        if (floor >= Long.MAX_VALUE - (long)resolution) {
            return Long.MAX_VALUE;
        }
        return floor + (long)resolution;
    }

    public static long quantizeToMinutes(long intervalInMilliseconds) {
        return TimeIntervalQuantizer.quantize(intervalInMilliseconds, 60000);
    }
}

