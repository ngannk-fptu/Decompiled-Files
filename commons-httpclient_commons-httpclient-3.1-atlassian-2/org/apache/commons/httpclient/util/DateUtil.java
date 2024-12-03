/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.httpclient.util.DateParseException;

public class DateUtil {
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    private static final Collection DEFAULT_PATTERNS = Arrays.asList("EEE MMM d HH:mm:ss yyyy", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    private static final TimeZone GMT;

    public static Date parseDate(String dateValue) throws DateParseException {
        return DateUtil.parseDate(dateValue, null, null);
    }

    public static Date parseDate(String dateValue, Collection dateFormats) throws DateParseException {
        return DateUtil.parseDate(dateValue, dateFormats, null);
    }

    public static Date parseDate(String dateValue, Collection dateFormats, Date startDate) throws DateParseException {
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DEFAULT_PATTERNS;
        }
        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }
        SimpleDateFormat dateParser = null;
        for (String format : dateFormats) {
            if (dateParser == null) {
                dateParser = new SimpleDateFormat(format, Locale.US);
                dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
                dateParser.set2DigitYearStart(startDate);
            } else {
                dateParser.applyPattern(format);
            }
            try {
                return dateParser.parse(dateValue);
            }
            catch (ParseException pe) {
            }
        }
        throw new DateParseException("Unable to parse the date " + dateValue);
    }

    public static String formatDate(Date date) {
        return DateUtil.formatDate(date, PATTERN_RFC1123);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
        formatter.setTimeZone(GMT);
        return formatter.format(date);
    }

    private DateUtil() {
    }

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 0, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
        GMT = TimeZone.getTimeZone("GMT");
    }
}

