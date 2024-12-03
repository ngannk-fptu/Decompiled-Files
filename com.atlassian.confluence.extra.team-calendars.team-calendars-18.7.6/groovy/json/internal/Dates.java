/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.CharScanner;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dates {
    private static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
    static final int SHORT_ISO_8601_TIME_LENGTH = "1994-11-05T08:15:30Z".length();
    static final int LONG_ISO_8601_TIME_LENGTH = "1994-11-05T08:15:30-05:00".length();
    public static final int JSON_TIME_LENGTH = "2013-12-14T01:55:33.412Z".length();

    public static long utc(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setTimeZone(UTC_TIME_ZONE);
        return calendar.getTime().getTime();
    }

    private static Date internalDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second, int miliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, month - 1);
        calendar.set(5, day);
        calendar.set(11, hour);
        calendar.set(12, minute);
        calendar.set(13, second);
        calendar.set(14, miliseconds);
        calendar.setTimeZone(tz);
        return calendar.getTime();
    }

    public static Date toDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second) {
        return Dates.internalDate(tz, year, month, day, hour, minute, second, 0);
    }

    public static Date toDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second, int miliseconds) {
        return Dates.internalDate(tz, year, month, day, hour, minute, second, miliseconds);
    }

    public static Date fromISO8601(char[] charArray, int from, int to) {
        try {
            if (Dates.isISO8601(charArray, from, to)) {
                int year = CharScanner.parseIntFromTo(charArray, from, from + 4);
                int month = CharScanner.parseIntFromTo(charArray, from + 5, from + 7);
                int day = CharScanner.parseIntFromTo(charArray, from + 8, from + 10);
                int hour = CharScanner.parseIntFromTo(charArray, from + 11, from + 13);
                int minute = CharScanner.parseIntFromTo(charArray, from + 14, from + 16);
                int second = CharScanner.parseIntFromTo(charArray, from + 17, from + 19);
                TimeZone tz = null;
                if (charArray[from + 19] == 'Z') {
                    tz = TimeZone.getTimeZone("GMT");
                } else {
                    String tzStr = "GMT" + String.valueOf(charArray, from + 19, 6);
                    tz = TimeZone.getTimeZone(tzStr);
                }
                return Dates.toDate(tz, year, month, day, hour, minute, second);
            }
            return null;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Date fromJsonDate(char[] charArray, int from, int to) {
        try {
            if (Dates.isJsonDate(charArray, from, to)) {
                int year = CharScanner.parseIntFromTo(charArray, from, from + 4);
                int month = CharScanner.parseIntFromTo(charArray, from + 5, from + 7);
                int day = CharScanner.parseIntFromTo(charArray, from + 8, from + 10);
                int hour = CharScanner.parseIntFromTo(charArray, from + 11, from + 13);
                int minute = CharScanner.parseIntFromTo(charArray, from + 14, from + 16);
                int second = CharScanner.parseIntFromTo(charArray, from + 17, from + 19);
                int miliseconds = CharScanner.parseIntFromTo(charArray, from + 20, from + 23);
                TimeZone tz = TimeZone.getTimeZone("GMT");
                return Dates.toDate(tz, year, month, day, hour, minute, second, miliseconds);
            }
            return null;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static boolean isISO8601(char[] charArray, int start, int to) {
        boolean valid = true;
        int length = to - start;
        if (length == SHORT_ISO_8601_TIME_LENGTH) {
            valid &= charArray[start + 19] == 'Z';
        } else if (length == LONG_ISO_8601_TIME_LENGTH) {
            valid &= charArray[start + 19] == '-' || charArray[start + 19] == '+';
            valid &= charArray[start + 22] == ':';
        } else {
            return false;
        }
        return valid &= charArray[start + 4] == '-' && charArray[start + 7] == '-' && charArray[start + 10] == 'T' && charArray[start + 13] == ':' && charArray[start + 16] == ':';
    }

    public static boolean isISO8601QuickCheck(char[] charArray, int start, int to) {
        int length = to - start;
        try {
            return length == JSON_TIME_LENGTH || length == LONG_ISO_8601_TIME_LENGTH || length == SHORT_ISO_8601_TIME_LENGTH || length >= 17 && charArray[start + 16] == ':';
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean isJsonDate(char[] charArray, int start, int to) {
        boolean valid = true;
        int length = to - start;
        if (length != JSON_TIME_LENGTH) {
            return false;
        }
        if (!(valid &= charArray[start + 19] == '.' || charArray[start + 19] == '+')) {
            return false;
        }
        return valid &= charArray[start + 4] == '-' && charArray[start + 7] == '-' && charArray[start + 10] == 'T' && charArray[start + 13] == ':' && charArray[start + 16] == ':';
    }
}

