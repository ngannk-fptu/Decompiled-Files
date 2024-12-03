/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {
    public static final long SECOND = 1000L;
    public static final long MINUTE = 60000L;
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;
    public static final long CALENDAR_YEAR = 31556952000L;

    private DateUtil() {
    }

    public static long delta(long l) {
        return System.currentTimeMillis() - l;
    }

    public static long delta(Date date) {
        return System.currentTimeMillis() - date.getTime();
    }

    public static long currentTimeSecond() {
        return DateUtil.roundToSecond(System.currentTimeMillis());
    }

    public static long currentTimeMinute() {
        return DateUtil.roundToMinute(System.currentTimeMillis());
    }

    public static long currentTimeHour() {
        return DateUtil.roundToHour(System.currentTimeMillis());
    }

    public static long currentTimeDay() {
        return DateUtil.roundToDay(System.currentTimeMillis());
    }

    public static long roundToSecond(long l) {
        return l / 1000L * 1000L;
    }

    public static long roundToMinute(long l) {
        return l / 60000L * 60000L;
    }

    public static long roundToHour(long l) {
        return DateUtil.roundToHour(l, TimeZone.getDefault());
    }

    public static long roundToHour(long l, TimeZone timeZone) {
        int n = timeZone.getOffset(l);
        return l / 3600000L * 3600000L - (long)n;
    }

    public static long roundToDay(long l) {
        return DateUtil.roundToDay(l, TimeZone.getDefault());
    }

    public static long roundToDay(long l, TimeZone timeZone) {
        int n = timeZone.getOffset(l);
        return (l + (long)n) / 86400000L * 86400000L - (long)n;
    }
}

