/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateUtil {
    static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static long midnightUTC(long epochMillis) {
        Calendar day = Calendar.getInstance(UTC);
        day.setTimeInMillis(epochMillis);
        day.set(14, 0);
        day.set(13, 0);
        day.set(12, 0);
        day.set(11, 0);
        return day.getTimeInMillis();
    }

    public static List<Long> epochDays(long endTs, long lookback) {
        long to = DateUtil.midnightUTC(endTs);
        long startMillis = endTs - (lookback != 0L ? lookback : endTs);
        long from = startMillis <= 0L ? 0L : DateUtil.midnightUTC(startMillis);
        ArrayList<Long> days = new ArrayList<Long>();
        for (long time = from; time <= to; time += TimeUnit.DAYS.toMillis(1L)) {
            days.add(time);
        }
        return days;
    }
}

