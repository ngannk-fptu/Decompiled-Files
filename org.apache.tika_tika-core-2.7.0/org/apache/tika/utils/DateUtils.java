/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final TimeZone MIDDAY = TimeZone.getTimeZone("GMT-12:00");
    private final List<DateFormat> iso8601InputFormats = this.loadDateFormats();

    private static DateFormat createDateFormat(String format, TimeZone timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, new DateFormatSymbols(Locale.US));
        if (timezone != null) {
            sdf.setTimeZone(timezone);
        }
        return sdf;
    }

    public static String formatDate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(UTC, Locale.US);
        calendar.setTime(date);
        return DateUtils.doFormatDate(calendar);
    }

    public static String formatDate(Calendar date) {
        date.setTimeZone(UTC);
        return DateUtils.doFormatDate(date);
    }

    public static String formatDateUnknownTimezone(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getDefault(), Locale.US);
        calendar.setTime(date);
        String formatted = DateUtils.formatDate(calendar);
        return formatted.substring(0, formatted.length() - 1);
    }

    private static String doFormatDate(Calendar calendar) {
        return String.format(Locale.ROOT, "%04d-%02d-%02dT%02d:%02d:%02dZ", calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13));
    }

    private List<DateFormat> loadDateFormats() {
        ArrayList<DateFormat> dateFormats = new ArrayList<DateFormat>();
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", UTC));
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", null));
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd'T'HH:mm:ss", null));
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd' 'HH:mm:ss'Z'", UTC));
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd' 'HH:mm:ssZ", null));
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd' 'HH:mm:ss", null));
        dateFormats.add(DateUtils.createDateFormat("yyyy-MM-dd", MIDDAY));
        dateFormats.add(DateUtils.createDateFormat("yyyy:MM:dd", MIDDAY));
        return dateFormats;
    }

    public Date tryToParse(String dateString) {
        int n = dateString.length();
        if (dateString.charAt(n - 3) == ':' && (dateString.charAt(n - 6) == '+' || dateString.charAt(n - 6) == '-')) {
            dateString = dateString.substring(0, n - 3) + dateString.substring(n - 2);
        }
        for (DateFormat df : this.iso8601InputFormats) {
            try {
                return df.parse(dateString);
            }
            catch (ParseException parseException) {
            }
        }
        return null;
    }
}

