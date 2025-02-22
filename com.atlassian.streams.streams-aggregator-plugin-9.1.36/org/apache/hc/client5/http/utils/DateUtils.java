/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.utils;

import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.util.Args;

public final class DateUtils {
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    private static final String[] DEFAULT_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy"};
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    public static final TimeZone GMT;

    public static Date parseDate(String dateValue) {
        return DateUtils.parseDate(dateValue, null, null);
    }

    public static Date parseDate(MessageHeaders headers, String headerName) {
        if (headers == null) {
            return null;
        }
        Header header = headers.getFirstHeader(headerName);
        if (header == null) {
            return null;
        }
        return DateUtils.parseDate(header.getValue(), null, null);
    }

    public static boolean isAfter(MessageHeaders message1, MessageHeaders message2, String headerName) {
        Date date2;
        Date date1;
        Header dateHeader2;
        Header dateHeader1;
        if (message1 != null && message2 != null && (dateHeader1 = message1.getFirstHeader(headerName)) != null && (dateHeader2 = message2.getFirstHeader(headerName)) != null && (date1 = DateUtils.parseDate(dateHeader1.getValue())) != null && (date2 = DateUtils.parseDate(dateHeader2.getValue())) != null) {
            return date1.after(date2);
        }
        return false;
    }

    public static boolean isBefore(MessageHeaders message1, MessageHeaders message2, String headerName) {
        Date date2;
        Date date1;
        Header dateHeader2;
        Header dateHeader1;
        if (message1 != null && message2 != null && (dateHeader1 = message1.getFirstHeader(headerName)) != null && (dateHeader2 = message2.getFirstHeader(headerName)) != null && (date1 = DateUtils.parseDate(dateHeader1.getValue())) != null && (date2 = DateUtils.parseDate(dateHeader2.getValue())) != null) {
            return date1.before(date2);
        }
        return false;
    }

    public static Date parseDate(String dateValue, String[] dateFormats) {
        return DateUtils.parseDate(dateValue, dateFormats, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) {
        Args.notNull(dateValue, "Date value");
        String[] localDateFormats = dateFormats != null ? dateFormats : DEFAULT_PATTERNS;
        Date localStartDate = startDate != null ? startDate : DEFAULT_TWO_DIGIT_YEAR_START;
        String v = dateValue;
        if (v.length() > 1 && v.startsWith("'") && v.endsWith("'")) {
            v = v.substring(1, v.length() - 1);
        }
        for (String dateFormat : localDateFormats) {
            SimpleDateFormat dateParser = DateFormatHolder.formatFor(dateFormat);
            dateParser.set2DigitYearStart(localStartDate);
            ParsePosition pos = new ParsePosition(0);
            Date result = dateParser.parse(v, pos);
            if (pos.getIndex() == 0) continue;
            return result;
        }
        return null;
    }

    public static String formatDate(Date date) {
        return DateUtils.formatDate(date, PATTERN_RFC1123);
    }

    public static String formatDate(Date date, String pattern) {
        Args.notNull(date, "Date");
        Args.notNull(pattern, "Pattern");
        SimpleDateFormat formatter = DateFormatHolder.formatFor(pattern);
        return formatter.format(date);
    }

    public static void clearThreadLocal() {
        DateFormatHolder.clearThreadLocal();
    }

    private DateUtils() {
    }

    static {
        GMT = TimeZone.getTimeZone("GMT");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(2000, 0, 1, 0, 0, 0);
        calendar.set(14, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    static final class DateFormatHolder {
        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal();

        DateFormatHolder() {
        }

        public static SimpleDateFormat formatFor(String pattern) {
            SimpleDateFormat format;
            Map<String, SimpleDateFormat> formats;
            SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
            Map<String, SimpleDateFormat> map = formats = ref == null ? null : ref.get();
            if (formats == null) {
                formats = new HashMap<String, SimpleDateFormat>();
                THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
            }
            if ((format = formats.get(pattern)) == null) {
                format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                formats.put(pattern, format);
            }
            return format;
        }

        public static void clearThreadLocal() {
            THREADLOCAL_FORMATS.remove();
        }
    }
}

