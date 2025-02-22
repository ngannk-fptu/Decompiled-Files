/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.util.concurrent.TimeUnit;

public final class TimeUtil {
    private TimeUtil() {
    }

    public static long timeInMsOrOneIfResultIsZero(long time, TimeUnit timeUnit) {
        long timeInMillis = timeUnit.toMillis(time);
        if (time > 0L && timeInMillis == 0L) {
            timeInMillis = 1L;
        }
        return timeInMillis;
    }

    public static long timeInMsOrTimeIfNullUnit(long time, TimeUnit timeUnit) {
        return timeUnit != null ? timeUnit.toMillis(time) : time;
    }

    public static long zeroOutMs(long timestamp) {
        return TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timestamp));
    }
}

