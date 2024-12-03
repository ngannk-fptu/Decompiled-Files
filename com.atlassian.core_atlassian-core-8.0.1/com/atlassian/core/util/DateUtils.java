/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.util;

import com.atlassian.core.util.InvalidDurationException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?|\\.\\d+)(.+)");
    public static final long SECOND_MILLIS = Duration.SECOND.getMilliseconds();
    public static final long MINUTE_MILLIS = Duration.MINUTE.getMilliseconds();
    public static final long HOUR_MILLIS = Duration.HOUR.getMilliseconds();
    public static final long DAY_MILLIS = Duration.DAY.getMilliseconds();
    public static final long MONTH_MILLIS = Duration.MONTH.getMilliseconds();
    public static final long YEAR_MILLIS = Duration.YEAR.getMilliseconds();
    public static final String AM = "am";
    public static final String PM = "pm";
    private static final int[] CALENDAR_PERIODS = new int[]{1, 2, 5, 11, 12, 13, 14};
    private final ResourceBundle resourceBundle;
    public static final DateFormat ISO8601DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    public DateUtils(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public static boolean equalTimestamps(Timestamp t1, Timestamp t2) {
        return Math.abs(t1.getTime() - t2.getTime()) < 10L;
    }

    public String dateDifferenceBean(long dateA, long dateB, long resolution, ResourceBundle resourceBundle) {
        return DateUtils.dateDifference(dateA, dateB, resolution, resourceBundle);
    }

    public static String dateDifference(long dateA, long dateB, long resolution, ResourceBundle resourceBundle) {
        StringBuilder sb = new StringBuilder();
        long difference = Math.abs(dateB - dateA);
        --resolution;
        long months = difference / Duration.MONTH.getMilliseconds();
        if (months > 0L) {
            difference %= Duration.MONTH.getMilliseconds();
            if (months > 1L) {
                sb.append(months).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.months")).append(", ");
            } else {
                sb.append(months).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.month")).append(", ");
            }
        }
        if (resolution < 0L) {
            if (sb.length() == 0) {
                return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.months");
            }
            return sb.substring(0, sb.length() - 2);
        }
        --resolution;
        long days = difference / Duration.DAY.getMilliseconds();
        if (days > 0L) {
            difference %= Duration.DAY.getMilliseconds();
            if (days > 1L) {
                sb.append(days).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.days")).append(", ");
            } else {
                sb.append(days).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.day")).append(", ");
            }
        }
        if (resolution < 0L) {
            if (sb.length() == 0) {
                return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.days");
            }
            return sb.substring(0, sb.length() - 2);
        }
        --resolution;
        long hours = difference / Duration.HOUR.getMilliseconds();
        if (hours > 0L) {
            difference %= Duration.HOUR.getMilliseconds();
            if (hours > 1L) {
                sb.append(hours).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.hours")).append(", ");
            } else {
                sb.append(hours).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.hour")).append(", ");
            }
        }
        if (resolution < 0L) {
            if (sb.length() == 0) {
                return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.hours");
            }
            return sb.substring(0, sb.length() - 2);
        }
        --resolution;
        long minutes = difference / Duration.MINUTE.getMilliseconds();
        if (minutes > 0L) {
            difference %= Duration.MINUTE.getMilliseconds();
            if (minutes > 1L) {
                sb.append(minutes).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.minutes")).append(", ");
            } else {
                sb.append(minutes).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.minute")).append(", ");
            }
        }
        if (resolution < 0L) {
            if (sb.length() == 0) {
                return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.minutes");
            }
            return sb.substring(0, sb.length() - 2);
        }
        --resolution;
        long seconds = difference / Duration.SECOND.getMilliseconds();
        if (seconds > 0L) {
            if (seconds > 1L) {
                sb.append(seconds).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.seconds")).append(", ");
            } else {
                sb.append(seconds).append(" ").append(DateUtils.getText(resourceBundle, "core.dateutils.second")).append(", ");
            }
        }
        if (resolution <= 0L && sb.length() == 0) {
            return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.seconds");
        }
        if (sb.length() > 2) {
            return sb.substring(0, sb.length() - 2);
        }
        return "";
    }

    public static String formatDateISO8601(java.util.Date ts) {
        return ISO8601DateFormat.format(ts);
    }

    public static boolean validDuration(String s) {
        try {
            DateUtils.getDuration(s);
            return true;
        }
        catch (InvalidDurationException e) {
            return false;
        }
    }

    public static long getDuration(String durationStr) throws InvalidDurationException {
        return DateUtils.getDuration(durationStr, Duration.MINUTE);
    }

    public static long getDuration(String durationStr, Duration defaultUnit) throws InvalidDurationException {
        return DateUtils.getDurationSeconds(durationStr, Duration.DAY.getSeconds(), Duration.WEEK.getSeconds(), defaultUnit);
    }

    public static long getDuration(String durationStr, int hoursPerDay, int daysPerWeek) throws InvalidDurationException {
        return DateUtils.getDuration(durationStr, hoursPerDay, daysPerWeek, Duration.MINUTE);
    }

    public static long getDuration(String durationStr, int hoursPerDay, int daysPerWeek, Duration defaultUnit) throws InvalidDurationException {
        long secondsInDay = (long)hoursPerDay * Duration.HOUR.getSeconds();
        long secondsPerWeek = (long)daysPerWeek * secondsInDay;
        return DateUtils.getDurationSeconds(durationStr, secondsInDay, secondsPerWeek, defaultUnit);
    }

    public static long getDurationWithNegative(String durationStr) throws InvalidDurationException {
        String cleanedDurationStr;
        String string = cleanedDurationStr = durationStr != null ? durationStr.trim() : "";
        if (cleanedDurationStr.isEmpty()) {
            return 0L;
        }
        boolean negative = false;
        if (cleanedDurationStr.charAt(0) == '-') {
            negative = true;
        }
        if (negative) {
            return 0L - DateUtils.getDuration(cleanedDurationStr.substring(1));
        }
        return DateUtils.getDuration(cleanedDurationStr);
    }

    public static long getDurationSeconds(String durationStr, long secondsPerDay, long secondsPerWeek, Duration defaultUnit) throws InvalidDurationException {
        long time = 0L;
        if (durationStr == null || durationStr.trim().isEmpty()) {
            return 0L;
        }
        if ((durationStr = durationStr.trim().toLowerCase()).indexOf(" ") > 0) {
            StringTokenizer st = new StringTokenizer(durationStr, ", ");
            while (st.hasMoreTokens()) {
                time += DateUtils.getDurationSeconds(st.nextToken(), secondsPerDay, secondsPerWeek, defaultUnit);
            }
        } else {
            try {
                time = Long.parseLong(durationStr.trim()) * defaultUnit.getModifiedSeconds(secondsPerDay, secondsPerWeek);
            }
            catch (Exception ex) {
                Matcher matcher = DURATION_PATTERN.matcher(durationStr);
                if (matcher.matches()) {
                    String numberAsString = matcher.group(1);
                    BigDecimal number = new BigDecimal(numberAsString);
                    long unit = DateUtils.getUnit(matcher.group(2), secondsPerDay, secondsPerWeek);
                    BigDecimal seconds = number.multiply(BigDecimal.valueOf(unit));
                    try {
                        seconds.divide(BigDecimal.valueOf(60L)).intValueExact();
                        time = seconds.intValueExact();
                    }
                    catch (ArithmeticException e) {
                        throw new InvalidDurationException("Specified decimal fraction duration cannot maintain precision", e);
                    }
                }
                throw new InvalidDurationException("Unable to parse duration string: " + durationStr);
            }
        }
        return time;
    }

    private static long getUnit(String unit, long secondsPerDay, long secondsPerWeek) throws InvalidDurationException {
        long time;
        switch (unit.charAt(0)) {
            case 'm': {
                DateUtils.validateDurationUnit(unit.substring(0), Duration.MINUTE);
                time = Duration.MINUTE.getSeconds();
                break;
            }
            case 'h': {
                DateUtils.validateDurationUnit(unit.substring(0), Duration.HOUR);
                time = Duration.HOUR.getSeconds();
                break;
            }
            case 'd': {
                DateUtils.validateDurationUnit(unit.substring(0), Duration.DAY);
                time = secondsPerDay;
                break;
            }
            case 'w': {
                DateUtils.validateDurationUnit(unit.substring(0), Duration.WEEK);
                time = secondsPerWeek;
                break;
            }
            default: {
                throw new InvalidDurationException("Not a valid duration string");
            }
        }
        return time;
    }

    private static String validateDurationUnit(String durationString, Duration duration) throws InvalidDurationException {
        if (durationString.length() > 1) {
            String singular = duration.name().toLowerCase();
            String plural = duration.name().toLowerCase() + "s";
            if (durationString.contains(plural)) {
                return durationString.substring(durationString.indexOf(plural));
            }
            if (durationString.contains(singular)) {
                return durationString.substring(durationString.indexOf(singular));
            }
            throw new InvalidDurationException("Not a valid durationString string");
        }
        return durationString.substring(1);
    }

    public static String getDurationString(long seconds) {
        return DateUtils.getDurationStringSeconds(seconds, Duration.DAY.getSeconds(), Duration.WEEK.getSeconds());
    }

    public static String getDurationStringWithNegative(long seconds) {
        if (seconds < 0L) {
            return "-" + DateUtils.getDurationString(-seconds);
        }
        return DateUtils.getDurationString(seconds);
    }

    public static String getDurationString(long l, int hoursPerDay, int daysPerWeek) {
        long secondsInDay = (long)hoursPerDay * Duration.HOUR.getSeconds();
        long secondsPerWeek = (long)daysPerWeek * secondsInDay;
        return DateUtils.getDurationStringSeconds(l, secondsInDay, secondsPerWeek);
    }

    public static String getDurationStringSeconds(long l, long secondsPerDay, long secondsPerWeek) {
        if (l == 0L) {
            return "0m";
        }
        StringBuilder result = new StringBuilder();
        if (l >= secondsPerWeek) {
            result.append(l / secondsPerWeek);
            result.append("w ");
            l %= secondsPerWeek;
        }
        if (l >= secondsPerDay) {
            result.append(l / secondsPerDay);
            result.append("d ");
            l %= secondsPerDay;
        }
        if (l >= Duration.HOUR.getSeconds()) {
            result.append(l / Duration.HOUR.getSeconds());
            result.append("h ");
            l %= Duration.HOUR.getSeconds();
        }
        if (l >= Duration.MINUTE.getSeconds()) {
            result.append(l / Duration.MINUTE.getSeconds());
            result.append("m ");
        }
        return result.toString().trim();
    }

    public static String getDurationPretty(long numSecs, ResourceBundle resourceBundle) {
        return DateUtils.getDurationPrettySeconds(numSecs, Duration.DAY.getSeconds(), Duration.WEEK.getSeconds(), resourceBundle, false);
    }

    public static String getDurationPretty(long numSecs, int hoursPerDay, int daysPerWeek, ResourceBundle resourceBundle) {
        long secondsInDay = (long)hoursPerDay * Duration.HOUR.getSeconds();
        long secondsPerWeek = (long)daysPerWeek * secondsInDay;
        return DateUtils.getDurationPrettySeconds(numSecs, secondsInDay, secondsPerWeek, resourceBundle, false);
    }

    public static String getDurationPrettySecondsResolution(long numSecs, ResourceBundle resourceBundle) {
        return DateUtils.getDurationPrettySeconds(numSecs, Duration.DAY.getSeconds(), Duration.WEEK.getSeconds(), resourceBundle, true);
    }

    public static String getDurationPrettySecondsResolution(long numSecs, int hoursPerDay, int daysPerWeek, ResourceBundle resourceBundle) {
        long secondsInDay = (long)hoursPerDay * Duration.HOUR.getSeconds();
        long secondsPerWeek = (long)daysPerWeek * secondsInDay;
        return DateUtils.getDurationPrettySeconds(numSecs, secondsInDay, secondsPerWeek, resourceBundle, true);
    }

    private static String getDurationPrettySeconds(long numSecs, long secondsPerDay, long secondsPerWeek, ResourceBundle resourceBundle, boolean secondsDuration) {
        long secondsPerYear = secondsPerWeek * 52L;
        return DateUtils.getDurationPrettySeconds(numSecs, secondsPerYear, secondsPerDay, secondsPerWeek, resourceBundle, secondsDuration);
    }

    public static String getDurationPrettySeconds(long numSecs, long secondsPerDay, long secondsPerWeek, ResourceBundle resourceBundle) {
        return DateUtils.getDurationPrettySeconds(numSecs, secondsPerDay, secondsPerWeek, resourceBundle, false);
    }

    private static String getDurationPrettySeconds(long numSecs, long secondsPerYear, long secondsPerDay, long secondsPerWeek, ResourceBundle resourceBundle, boolean secondResolution) {
        if (numSecs == 0L) {
            if (secondResolution) {
                return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.seconds");
            }
            return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.minutes");
        }
        StringBuilder result = new StringBuilder();
        if (numSecs >= secondsPerYear) {
            long years = numSecs / secondsPerYear;
            result.append(years).append(' ');
            if (years > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.years"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.year"));
            }
            result.append(", ");
            numSecs %= secondsPerYear;
        }
        if (numSecs >= secondsPerWeek) {
            long weeks = numSecs / secondsPerWeek;
            result.append(weeks).append(' ');
            if (weeks > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.weeks"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.week"));
            }
            result.append(", ");
            numSecs %= secondsPerWeek;
        }
        if (numSecs >= secondsPerDay) {
            long days = numSecs / secondsPerDay;
            result.append(days).append(' ');
            if (days > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.days"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.day"));
            }
            result.append(", ");
            numSecs %= secondsPerDay;
        }
        if (numSecs >= Duration.HOUR.getSeconds()) {
            long hours = numSecs / Duration.HOUR.getSeconds();
            result.append(hours).append(' ');
            if (hours > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.hours"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.hour"));
            }
            result.append(", ");
            numSecs %= Duration.HOUR.getSeconds();
        }
        if (numSecs >= Duration.MINUTE.getSeconds()) {
            long minute = numSecs / Duration.MINUTE.getSeconds();
            result.append(minute).append(' ');
            if (minute > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.minutes"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.minute"));
            }
            result.append(", ");
            if (secondResolution) {
                numSecs %= Duration.MINUTE.getSeconds();
            }
        }
        if (numSecs >= 1L && numSecs < Duration.MINUTE.getSeconds()) {
            result.append(numSecs).append(' ');
            if (numSecs > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.seconds"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.second"));
            }
            result.append(", ");
        }
        if (result.length() > 2) {
            return result.substring(0, result.length() - 2);
        }
        return result.toString();
    }

    public String formatDurationPretty(long l) {
        return DateUtils.getDurationPretty(l, this.resourceBundle);
    }

    public String formatDurationPretty(String seconds) {
        return DateUtils.getDurationPretty(Long.parseLong(seconds), this.resourceBundle);
    }

    public String formatDurationString(long l) {
        return DateUtils.getDurationPretty(l, this.resourceBundle);
    }

    private static String getText(ResourceBundle resourceBundle, String key) {
        try {
            return resourceBundle.getString(key);
        }
        catch (MissingResourceException e) {
            log.error("Key not found in bundle", (Throwable)e);
            return "";
        }
    }

    public static Calendar toEndOfPeriod(Calendar calendar, int period) {
        boolean zero = false;
        for (int calendarPeriod : CALENDAR_PERIODS) {
            if (zero) {
                calendar.set(calendarPeriod, calendar.getMaximum(calendarPeriod));
            }
            if (calendarPeriod != period) continue;
            zero = true;
        }
        if (!zero) {
            throw new IllegalArgumentException("unknown Calendar period: " + period);
        }
        return calendar;
    }

    public static Calendar toStartOfPeriod(Calendar calendar, int period) {
        boolean zero = false;
        for (int calendarPeriod : CALENDAR_PERIODS) {
            if (zero) {
                if (calendarPeriod == 5) {
                    calendar.set(5, 1);
                } else {
                    calendar.set(calendarPeriod, 0);
                }
            }
            if (calendarPeriod != period) continue;
            zero = true;
        }
        if (!zero) {
            throw new IllegalArgumentException("unknown Calendar period: " + period);
        }
        return calendar;
    }

    public static DateRange toDateRange(Calendar date, int period) {
        Calendar cal = (Calendar)date.clone();
        DateUtils.toStartOfPeriod(cal, period);
        java.util.Date startDate = new java.util.Date(cal.getTimeInMillis());
        cal.add(period, 1);
        java.util.Date endDate = new java.util.Date(cal.getTimeInMillis());
        return new DateRange(startDate, endDate);
    }

    public static Calendar getCalendarDay(int year, int month, int day) {
        return DateUtils.initCalendar(year, month, day, 0, 0, 0, 0);
    }

    public static java.util.Date getDateDay(int year, int month, int day) {
        return DateUtils.getCalendarDay(year, month, day).getTime();
    }

    public static java.util.Date getSqlDateDay(int year, int month, int day) {
        return new Date(DateUtils.getCalendarDay(year, month, day).getTimeInMillis());
    }

    public static int get24HourTime(String meridianIndicator, int hours) {
        if (hours == 12) {
            if (AM.equalsIgnoreCase(meridianIndicator)) {
                return 0;
            }
            if (PM.equalsIgnoreCase(meridianIndicator)) {
                return 12;
            }
        }
        int onceMeridianAdjustment = PM.equalsIgnoreCase(meridianIndicator) ? 12 : 0;
        return hours + onceMeridianAdjustment;
    }

    public static java.util.Date tomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.add(5, 1);
        return cal.getTime();
    }

    public static java.util.Date yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(5, -1);
        return cal.getTime();
    }

    private static Calendar initCalendar(int year, int month, int day, int hour, int minute, int second, int millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(14, millis);
        return calendar;
    }

    public static class DateRange {
        public final java.util.Date startDate;
        public final java.util.Date endDate;

        public DateRange(java.util.Date startDate, java.util.Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public static enum Duration {
        SECOND(1L),
        MINUTE(60L),
        HOUR(60L * MINUTE.getSeconds()),
        DAY(24L * HOUR.getSeconds()){

            @Override
            public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
                return secondsPerDay;
            }
        }
        ,
        WEEK(7L * DAY.getSeconds()){

            @Override
            public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
                return secondsPerWeek;
            }
        }
        ,
        MONTH(31L * DAY.getSeconds()){

            @Override
            public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
                return 31L * secondsPerDay;
            }
        }
        ,
        YEAR(52L * WEEK.getSeconds()){

            @Override
            public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
                return 52L * secondsPerWeek;
            }
        };

        private final long seconds;

        public long getSeconds() {
            return this.seconds;
        }

        public long getMilliseconds() {
            return 1000L * this.getSeconds();
        }

        public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
            return this.getSeconds();
        }

        private Duration(long seconds) {
            this.seconds = seconds;
        }
    }
}

