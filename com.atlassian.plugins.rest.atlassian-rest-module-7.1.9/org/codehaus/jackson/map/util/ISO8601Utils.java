/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
    private static final String GMT_ID = "GMT";
    private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");

    public static String format(Date date) {
        return ISO8601Utils.format(date, false, TIMEZONE_GMT);
    }

    public static String format(Date date, boolean millis) {
        return ISO8601Utils.format(date, millis, TIMEZONE_GMT);
    }

    public static String format(Date date, boolean millis, TimeZone tz) {
        int offset;
        GregorianCalendar calendar = new GregorianCalendar(tz, Locale.US);
        calendar.setTime(date);
        int capacity = "yyyy-MM-ddThh:mm:ss".length();
        capacity += millis ? ".sss".length() : 0;
        StringBuilder formatted = new StringBuilder(capacity += tz.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length());
        ISO8601Utils.padInt(formatted, calendar.get(1), "yyyy".length());
        formatted.append('-');
        ISO8601Utils.padInt(formatted, calendar.get(2) + 1, "MM".length());
        formatted.append('-');
        ISO8601Utils.padInt(formatted, calendar.get(5), "dd".length());
        formatted.append('T');
        ISO8601Utils.padInt(formatted, calendar.get(11), "hh".length());
        formatted.append(':');
        ISO8601Utils.padInt(formatted, calendar.get(12), "mm".length());
        formatted.append(':');
        ISO8601Utils.padInt(formatted, calendar.get(13), "ss".length());
        if (millis) {
            formatted.append('.');
            ISO8601Utils.padInt(formatted, calendar.get(14), "sss".length());
        }
        if ((offset = tz.getOffset(calendar.getTimeInMillis())) != 0) {
            int hours = Math.abs(offset / 60000 / 60);
            int minutes = Math.abs(offset / 60000 % 60);
            formatted.append(offset < 0 ? (char)'-' : '+');
            ISO8601Utils.padInt(formatted, hours, "hh".length());
            formatted.append(':');
            ISO8601Utils.padInt(formatted, minutes, "mm".length());
        } else {
            formatted.append('Z');
        }
        return formatted.toString();
    }

    public static Date parse(String date) {
        try {
            String timezoneId;
            char timezoneIndicator;
            int offset = 0;
            int year = ISO8601Utils.parseInt(date, offset, offset += 4);
            ISO8601Utils.checkOffset(date, offset, '-');
            int month = ISO8601Utils.parseInt(date, ++offset, offset += 2);
            ISO8601Utils.checkOffset(date, offset, '-');
            int day = ISO8601Utils.parseInt(date, ++offset, offset += 2);
            ISO8601Utils.checkOffset(date, offset, 'T');
            int hour = ISO8601Utils.parseInt(date, ++offset, offset += 2);
            ISO8601Utils.checkOffset(date, offset, ':');
            int minutes = ISO8601Utils.parseInt(date, ++offset, offset += 2);
            ISO8601Utils.checkOffset(date, offset, ':');
            int seconds = ISO8601Utils.parseInt(date, ++offset, offset += 2);
            int milliseconds = 0;
            if (date.charAt(offset) == '.') {
                ISO8601Utils.checkOffset(date, offset, '.');
                milliseconds = ISO8601Utils.parseInt(date, ++offset, offset += 3);
            }
            if ((timezoneIndicator = date.charAt(offset)) == '+' || timezoneIndicator == '-') {
                timezoneId = GMT_ID + date.substring(offset);
            } else if (timezoneIndicator == 'Z') {
                timezoneId = GMT_ID;
            } else {
                throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
            }
            TimeZone timezone = TimeZone.getTimeZone(timezoneId);
            if (!timezone.getID().equals(timezoneId)) {
                throw new IndexOutOfBoundsException();
            }
            GregorianCalendar calendar = new GregorianCalendar(timezone);
            calendar.setLenient(false);
            calendar.set(1, year);
            calendar.set(2, month - 1);
            calendar.set(5, day);
            calendar.set(11, hour);
            calendar.set(12, minutes);
            calendar.set(13, seconds);
            calendar.set(14, milliseconds);
            return calendar.getTime();
        }
        catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        }
    }

    private static void checkOffset(String value, int offset, char expected) throws IndexOutOfBoundsException {
        char found = value.charAt(offset);
        if (found != expected) {
            throw new IndexOutOfBoundsException("Expected '" + expected + "' character but found '" + found + "'");
        }
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        int digit;
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int i = beginIndex;
        int result = 0;
        if (i < endIndex) {
            if ((digit = Character.digit(value.charAt(i++), 10)) < 0) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result = -digit;
        }
        while (i < endIndex) {
            if ((digit = Character.digit(value.charAt(i++), 10)) < 0) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; --i) {
            buffer.append('0');
        }
        buffer.append(strValue);
    }
}

