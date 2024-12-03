/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class ISO8601 {
    private static final Map<String, TimeZone> TZS;
    private static TimeZone UTC;

    public static Calendar parse(String text) {
        TimeZone tz;
        int ms;
        int sec;
        int min;
        int hour;
        int day;
        int month;
        int year;
        int start;
        int sign;
        if (text == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        if (text.startsWith("-")) {
            sign = 45;
            start = 1;
        } else if (text.startsWith("+")) {
            sign = 43;
            start = 1;
        } else {
            sign = 43;
            start = 0;
        }
        try {
            year = Integer.parseInt(text.substring(start, start + 4));
            if (text.charAt(start += 4) != '-') {
                return null;
            }
            month = Integer.parseInt(text.substring(++start, start + 2));
            if (text.charAt(start += 2) != '-') {
                return null;
            }
            day = Integer.parseInt(text.substring(++start, start + 2));
            if (text.charAt(start += 2) != 'T') {
                return null;
            }
            hour = Integer.parseInt(text.substring(++start, start + 2));
            if (text.charAt(start += 2) != ':') {
                return null;
            }
            min = Integer.parseInt(text.substring(++start, start + 2));
            if (text.charAt(start += 2) != ':') {
                return null;
            }
            sec = Integer.parseInt(text.substring(++start, start + 2));
            if (text.charAt(start += 2) != '.') {
                return null;
            }
            ms = Integer.parseInt(text.substring(++start, start + 3));
            String tzid = text.substring(start += 3);
            tz = TZS.get(tzid);
            if (tz == null && !(tz = TimeZone.getTimeZone(tzid = "GMT" + tzid)).getID().equals(tzid)) {
                return null;
            }
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
        catch (NumberFormatException e) {
            return null;
        }
        Calendar cal = Calendar.getInstance(tz);
        cal.setLenient(false);
        if (sign == 45 || year == 0) {
            cal.set(1, year + 1);
            cal.set(0, 0);
        } else {
            cal.set(1, year);
            cal.set(0, 1);
        }
        cal.set(2, month - 1);
        cal.set(5, day);
        cal.set(11, hour);
        cal.set(12, min);
        cal.set(13, sec);
        cal.set(14, ms);
        try {
            cal.getTime();
            ISO8601.getYear(cal);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        return cal;
    }

    public static String format(Date date) throws IllegalArgumentException {
        return ISO8601.format(date, 0);
    }

    public static String format(Clock clock) throws IllegalArgumentException {
        return ISO8601.format(clock.millis(), clock.getZone().getRules().getOffset(clock.instant()).getTotalSeconds());
    }

    public static String format(long millisSinceEpoch) throws IllegalArgumentException {
        return ISO8601.format(millisSinceEpoch, 0);
    }

    public static String format(Date date, int tzOffsetInSeconds) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        return ISO8601.format(date.getTime(), tzOffsetInSeconds);
    }

    public static String format(long millisSinceEpoch, int tzOffsetInSeconds) throws IllegalArgumentException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tzOffsetInSeconds == 0 ? UTC : new SimpleTimeZone(tzOffsetInSeconds * 1000, ""));
        cal.setTimeInMillis(millisSinceEpoch);
        return ISO8601.format(cal);
    }

    public static String format(Calendar cal) throws IllegalArgumentException {
        return ISO8601.format(cal, true);
    }

    private static String format(Calendar cal, boolean includeMs) throws IllegalArgumentException {
        TimeZone tz;
        int offset;
        if (cal == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        StringBuilder buf = new StringBuilder();
        ISO8601.appendZeroPaddedInt(buf, ISO8601.getYear(cal), 4);
        buf.append('-');
        ISO8601.appendZeroPaddedInt(buf, cal.get(2) + 1, 2);
        buf.append('-');
        ISO8601.appendZeroPaddedInt(buf, cal.get(5), 2);
        buf.append('T');
        ISO8601.appendZeroPaddedInt(buf, cal.get(11), 2);
        buf.append(':');
        ISO8601.appendZeroPaddedInt(buf, cal.get(12), 2);
        buf.append(':');
        ISO8601.appendZeroPaddedInt(buf, cal.get(13), 2);
        if (includeMs) {
            buf.append('.');
            ISO8601.appendZeroPaddedInt(buf, cal.get(14), 3);
        }
        if ((offset = (tz = cal.getTimeZone()).getOffset(cal.getTimeInMillis())) != 0) {
            int hours = Math.abs(offset / 60000 / 60);
            int minutes = Math.abs(offset / 60000 % 60);
            buf.append(offset < 0 ? (char)'-' : '+');
            ISO8601.appendZeroPaddedInt(buf, hours, 2);
            buf.append(':');
            ISO8601.appendZeroPaddedInt(buf, minutes, 2);
        } else {
            buf.append('Z');
        }
        return buf.toString();
    }

    public static int getYear(Calendar cal) throws IllegalArgumentException {
        int year = cal.get(1);
        if (cal.isSet(0) && cal.get(0) == 0) {
            year = 0 - year + 1;
        }
        if (year > 9999 || year < -9999) {
            throw new IllegalArgumentException("Calendar has more than four year digits, cannot be formatted as ISO8601: " + year);
        }
        return year;
    }

    private static void appendZeroPaddedInt(StringBuilder buf, int n, int precision) {
        if (n < 0) {
            buf.append('-');
            n = -n;
        }
        for (int exp = precision - 1; exp > 0 && (double)n < Math.pow(10.0, exp); --exp) {
            buf.append('0');
        }
        buf.append(n);
    }

    static {
        String[] tzs;
        TZS = new HashMap<String, TimeZone>();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        TZS.put("Z", gmt);
        TZS.put("+00:00", gmt);
        TZS.put("-00:00", gmt);
        for (String tz : tzs = new String[]{"-12:00", "-11:00", "-10:00", "-09:30", "-09:00", "-08:00", "-07:00", "-06:00", "-05:00", "-04:30", "-04:00", "-03:30", "-03:00", "-02:00", "-01:00", "+01:00", "+02:00", "+03:00", "+03:30", "+04:00", "+04:30", "+05:00", "+05:30", "+05:45", "+06:00", "+06:30", "+07:00", "+08:00", "+08:45", "+09:00", "+09:30", "+10:00", "+10:30", "+11:00", "+11:30", "+12:00", "+12:45", "+13:00", "+14:00"}) {
            TZS.put(tz, TimeZone.getTimeZone("GMT" + tz));
        }
        UTC = TimeZone.getTimeZone("UTC");
    }

    public static class SHORT {
        public static String format(Date date) throws IllegalArgumentException {
            return SHORT.format(date, 0);
        }

        public static String format(Clock clock) throws IllegalArgumentException {
            return SHORT.format(clock.millis(), clock.getZone().getRules().getOffset(clock.instant()).getTotalSeconds());
        }

        public static String format(long millisSinceEpoch) throws IllegalArgumentException {
            return SHORT.format(millisSinceEpoch, 0);
        }

        public static String format(Date date, int tzOffsetInSeconds) throws IllegalArgumentException {
            if (date == null) {
                throw new IllegalArgumentException("argument can not be null");
            }
            return SHORT.format(date.getTime(), tzOffsetInSeconds);
        }

        public static String format(long millisSinceEpoch, int tzOffsetInSeconds) throws IllegalArgumentException {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(tzOffsetInSeconds == 0 ? UTC : new SimpleTimeZone(tzOffsetInSeconds * 1000, ""));
            cal.setTimeInMillis(millisSinceEpoch);
            return SHORT.format(cal);
        }

        public static String format(Calendar cal) throws IllegalArgumentException {
            return ISO8601.format(cal, false);
        }
    }
}

