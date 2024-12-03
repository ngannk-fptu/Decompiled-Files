/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.util;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class TimeUtils {
    private static final Pattern PARSE_PATTERN = Pattern.compile("[,_ ]");
    private static final long C0 = 1L;
    private static final long C1 = 1000L;
    private static final long C2 = 1000000L;
    private static final long C3 = 1000000000L;
    private static final long C4 = 60000000000L;
    private static final long C5 = 3600000000000L;
    private static final long C6 = 86400000000000L;

    private TimeUtils() {
    }

    public static double convert(double t, TimeUnit sourceUnit, TimeUnit destinationUnit) {
        switch (sourceUnit) {
            case NANOSECONDS: {
                return TimeUtils.nanosToUnit(t, destinationUnit);
            }
            case MICROSECONDS: {
                return TimeUtils.microsToUnit(t, destinationUnit);
            }
            case MILLISECONDS: {
                return TimeUtils.millisToUnit(t, destinationUnit);
            }
            case SECONDS: {
                return TimeUtils.secondsToUnit(t, destinationUnit);
            }
            case MINUTES: {
                return TimeUtils.minutesToUnit(t, destinationUnit);
            }
            case HOURS: {
                return TimeUtils.hoursToUnit(t, destinationUnit);
            }
        }
        return TimeUtils.daysToUnit(t, destinationUnit);
    }

    public static double nanosToUnit(double nanos, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            default: {
                return nanos;
            }
            case MICROSECONDS: {
                return nanos / 1000.0;
            }
            case MILLISECONDS: {
                return nanos / 1000000.0;
            }
            case SECONDS: {
                return nanos / 1.0E9;
            }
            case MINUTES: {
                return nanos / 6.0E10;
            }
            case HOURS: {
                return nanos / 3.6E12;
            }
            case DAYS: 
        }
        return nanos / 8.64E13;
    }

    public static double microsToUnit(double micros, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS: {
                return micros * 1000.0;
            }
            default: {
                return micros;
            }
            case MILLISECONDS: {
                return micros / 1000.0;
            }
            case SECONDS: {
                return micros / 1000000.0;
            }
            case MINUTES: {
                return micros / 6.0E7;
            }
            case HOURS: {
                return micros / 3.6E9;
            }
            case DAYS: 
        }
        return micros / 8.64E10;
    }

    public static double millisToUnit(double millis, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS: {
                return millis * 1000000.0;
            }
            case MICROSECONDS: {
                return millis * 1000.0;
            }
            default: {
                return millis;
            }
            case SECONDS: {
                return millis / 1000.0;
            }
            case MINUTES: {
                return millis / 60000.0;
            }
            case HOURS: {
                return millis / 3600000.0;
            }
            case DAYS: 
        }
        return millis / 8.64E7;
    }

    public static double secondsToUnit(double seconds, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS: {
                return seconds * 1.0E9;
            }
            case MICROSECONDS: {
                return seconds * 1000000.0;
            }
            case MILLISECONDS: {
                return seconds * 1000.0;
            }
            default: {
                return seconds;
            }
            case MINUTES: {
                return seconds / 60.0;
            }
            case HOURS: {
                return seconds / 3600.0;
            }
            case DAYS: 
        }
        return seconds / 86400.0;
    }

    public static double minutesToUnit(double minutes, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS: {
                return minutes * 6.0E10;
            }
            case MICROSECONDS: {
                return minutes * 6.0E7;
            }
            case MILLISECONDS: {
                return minutes * 60000.0;
            }
            case SECONDS: {
                return minutes * 60.0;
            }
            default: {
                return minutes;
            }
            case HOURS: {
                return minutes / 60.0;
            }
            case DAYS: 
        }
        return minutes / 1440.0;
    }

    public static double hoursToUnit(double hours, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS: {
                return hours * 3.6E12;
            }
            case MICROSECONDS: {
                return hours * 3.6E9;
            }
            case MILLISECONDS: {
                return hours * 3600000.0;
            }
            case SECONDS: {
                return hours * 3600.0;
            }
            case MINUTES: {
                return hours * 60.0;
            }
            default: {
                return hours;
            }
            case DAYS: 
        }
        return hours / 24.0;
    }

    public static double daysToUnit(double days, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS: {
                return days * 8.64E13;
            }
            case MICROSECONDS: {
                return days * 8.64E10;
            }
            case MILLISECONDS: {
                return days * 8.64E7;
            }
            case SECONDS: {
                return days * 86400.0;
            }
            case MINUTES: {
                return days * 1440.0;
            }
            case HOURS: {
                return days * 24.0;
            }
        }
        return days;
    }

    @Deprecated
    public static Duration simpleParse(String time) {
        String timeLower = PARSE_PATTERN.matcher(time.toLowerCase()).replaceAll("");
        if (timeLower.endsWith("ns")) {
            return Duration.ofNanos(Long.parseLong(timeLower.substring(0, timeLower.length() - 2)));
        }
        if (timeLower.endsWith("ms")) {
            return Duration.ofMillis(Long.parseLong(timeLower.substring(0, timeLower.length() - 2)));
        }
        if (timeLower.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(timeLower.substring(0, timeLower.length() - 1)));
        }
        if (timeLower.endsWith("m")) {
            return Duration.ofMinutes(Long.parseLong(timeLower.substring(0, timeLower.length() - 1)));
        }
        if (timeLower.endsWith("h")) {
            return Duration.ofHours(Long.parseLong(timeLower.substring(0, timeLower.length() - 1)));
        }
        if (timeLower.endsWith("d")) {
            return Duration.of(Long.parseLong(timeLower.substring(0, timeLower.length() - 1)), ChronoUnit.DAYS);
        }
        throw new DateTimeParseException("Unable to parse " + time + " into duration", timeLower, 0);
    }

    public static String format(Duration duration) {
        int totalSeconds = (int)(duration.toMillis() / 1000L);
        int seconds = totalSeconds % 60;
        int totalMinutes = totalSeconds / 60;
        int minutes = totalMinutes % 60;
        int hours = totalMinutes / 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours);
            sb.append('h');
        }
        if (minutes > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(minutes);
            sb.append('m');
        }
        int nanos = duration.getNano();
        if (seconds > 0 || nanos > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(seconds);
            if (nanos > 0) {
                sb.append('.');
                sb.append(String.format("%09d", nanos).replaceFirst("0+$", ""));
            }
            sb.append('s');
        }
        return sb.toString();
    }
}

