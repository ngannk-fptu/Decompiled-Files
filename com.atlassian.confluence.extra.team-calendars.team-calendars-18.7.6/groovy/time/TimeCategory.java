/*
 * Decompiled with CFR 0.152.
 */
package groovy.time;

import groovy.time.BaseDuration;
import groovy.time.DatumDependentDuration;
import groovy.time.Duration;
import groovy.time.TimeDuration;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeCategory {
    public static Date plus(Date date, BaseDuration duration) {
        return duration.plus(date);
    }

    public static Date minus(Date date, BaseDuration duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(1, -duration.getYears());
        cal.add(2, -duration.getMonths());
        cal.add(6, -duration.getDays());
        cal.add(11, -duration.getHours());
        cal.add(12, -duration.getMinutes());
        cal.add(13, -duration.getSeconds());
        cal.add(14, -duration.getMillis());
        return cal.getTime();
    }

    @Deprecated
    public static TimeZone getTimeZone(Date self) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(self);
        return calendar.getTimeZone();
    }

    public static Duration getDaylightSavingsOffset(Date self) {
        TimeZone timeZone = TimeCategory.getTimeZone(self);
        int millis = timeZone.useDaylightTime() && timeZone.inDaylightTime(self) ? timeZone.getDSTSavings() : 0;
        return new TimeDuration(0, 0, 0, millis);
    }

    public static Duration getDaylightSavingsOffset(BaseDuration self) {
        return TimeCategory.getDaylightSavingsOffset(new Date(self.toMilliseconds() + 1L));
    }

    public static Duration getRelativeDaylightSavingsOffset(Date self, Date other) {
        Duration d1 = TimeCategory.getDaylightSavingsOffset(self);
        Duration d2 = TimeCategory.getDaylightSavingsOffset(other);
        return new TimeDuration(0, 0, 0, (int)(d2.toMilliseconds() - d1.toMilliseconds()));
    }

    public static TimeDuration minus(Date lhs, Date rhs) {
        long milliseconds = lhs.getTime() - rhs.getTime();
        long days = milliseconds / 86400000L;
        int hours = (int)((milliseconds -= days * 24L * 60L * 60L * 1000L) / 3600000L);
        int minutes = (int)((milliseconds -= (long)(hours * 60 * 60 * 1000)) / 60000L);
        int seconds = (int)((milliseconds -= (long)(minutes * 60 * 1000)) / 1000L);
        return new TimeDuration((int)days, hours, minutes, seconds, (int)(milliseconds -= (long)(seconds * 1000)));
    }

    public static DatumDependentDuration getMonths(Integer self) {
        return new DatumDependentDuration(0, self, 0, 0, 0, 0, 0);
    }

    public static DatumDependentDuration getMonth(Integer self) {
        return TimeCategory.getMonths(self);
    }

    public static DatumDependentDuration getYears(Integer self) {
        return new DatumDependentDuration(self, 0, 0, 0, 0, 0, 0);
    }

    public static DatumDependentDuration getYear(Integer self) {
        return TimeCategory.getYears(self);
    }

    public static Duration getWeeks(Integer self) {
        return new Duration(self * 7, 0, 0, 0, 0);
    }

    public static Duration getWeek(Integer self) {
        return TimeCategory.getWeeks(self);
    }

    public static Duration getDays(Integer self) {
        return new Duration(self, 0, 0, 0, 0);
    }

    public static Duration getDay(Integer self) {
        return TimeCategory.getDays(self);
    }

    public static TimeDuration getHours(Integer self) {
        return new TimeDuration(0, self, 0, 0, 0);
    }

    public static TimeDuration getHour(Integer self) {
        return TimeCategory.getHours(self);
    }

    public static TimeDuration getMinutes(Integer self) {
        return new TimeDuration(0, 0, self, 0, 0);
    }

    public static TimeDuration getMinute(Integer self) {
        return TimeCategory.getMinutes(self);
    }

    public static TimeDuration getSeconds(Integer self) {
        return new TimeDuration(0, 0, 0, self, 0);
    }

    public static TimeDuration getSecond(Integer self) {
        return TimeCategory.getSeconds(self);
    }

    public static TimeDuration getMilliseconds(Integer self) {
        return new TimeDuration(0, 0, 0, 0, self);
    }

    public static TimeDuration getMillisecond(Integer self) {
        return TimeCategory.getMilliseconds(self);
    }
}

