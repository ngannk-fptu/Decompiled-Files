/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateFormatUtil {
    private static final String ISO_8601_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private DateFormatUtil() {
    }

    public static String formatPDFDate(Date date, TimeZone timeZone) {
        DateFormat dateFormat = DateFormatUtil.createDateFormat("'D:'yyyyMMddHHmmss", timeZone);
        return DateFormatUtil.formatDate(date, dateFormat, '\'', true);
    }

    public static String formatISO8601(Date date, TimeZone timeZone) {
        DateFormat dateFormat = DateFormatUtil.createDateFormat(ISO_8601_DATE_PATTERN, timeZone);
        return DateFormatUtil.formatDate(date, dateFormat, ':', false);
    }

    private static DateFormat createDateFormat(String format, TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        dateFormat.setTimeZone(timeZone);
        return dateFormat;
    }

    private static String formatDate(Date date, DateFormat dateFormat, char delimiter, boolean endWithDelimiter) {
        Calendar cal = Calendar.getInstance(dateFormat.getTimeZone(), Locale.ENGLISH);
        cal.setTime(date);
        int offset = DateFormatUtil.getOffsetInMinutes(cal);
        StringBuilder sb = new StringBuilder(dateFormat.format(date));
        DateFormatUtil.appendOffset(sb, delimiter, offset, endWithDelimiter);
        return sb.toString();
    }

    private static int getOffsetInMinutes(Calendar cal) {
        int offset = cal.get(15);
        offset += cal.get(16);
        return offset /= 60000;
    }

    private static void appendOffset(StringBuilder sb, char delimiter, int offset, boolean endWithDelimiter) {
        if (offset == 0) {
            DateFormatUtil.appendOffsetUTC(sb);
        } else {
            DateFormatUtil.appendOffsetNoUTC(sb, delimiter, offset, endWithDelimiter);
        }
    }

    private static void appendOffsetUTC(StringBuilder sb) {
        sb.append('Z');
    }

    private static void appendOffsetNoUTC(StringBuilder sb, char delimiter, int offset, boolean endWithDelimiter) {
        int zoneOffsetHours = offset / 60;
        DateFormatUtil.appendOffsetSign(sb, zoneOffsetHours);
        DateFormatUtil.appendPaddedNumber(sb, Math.abs(zoneOffsetHours));
        sb.append(delimiter);
        DateFormatUtil.appendPaddedNumber(sb, Math.abs(offset % 60));
        if (endWithDelimiter) {
            sb.append(delimiter);
        }
    }

    private static void appendOffsetSign(StringBuilder sb, int zoneOffsetHours) {
        if (zoneOffsetHours >= 0) {
            sb.append('+');
        } else {
            sb.append('-');
        }
    }

    private static void appendPaddedNumber(StringBuilder sb, int number) {
        if (number < 10) {
            sb.append('0');
        }
        sb.append(number);
    }

    public static Date parseISO8601Date(String date) {
        String errorMessage = "Invalid ISO 8601 date format: ";
        date = DateFormatUtil.formatDateToParse(date, "Invalid ISO 8601 date format: ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            return dateFormat.parse(date);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Invalid ISO 8601 date format: " + date);
        }
    }

    private static String formatDateToParse(String date, String errorMessage) {
        if (!date.contains("Z")) {
            int lastColonIndex = date.lastIndexOf(":");
            if (lastColonIndex < 0) {
                throw new IllegalArgumentException(errorMessage + date);
            }
            date = date.substring(0, lastColonIndex) + date.substring(lastColonIndex + 1, date.length());
        } else {
            date = date.replace("Z", "+0000");
        }
        return date;
    }
}

