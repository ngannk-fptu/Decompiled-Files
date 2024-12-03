/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.util.Args;

public final class DateUtils {
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final DateTimeFormatter FORMATTER_RFC1123 = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern("EEE, dd MMM yyyy HH:mm:ss zzz").toFormatter(Locale.ENGLISH);
    public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
    public static final DateTimeFormatter FORMATTER_RFC1036 = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern("EEE, dd-MMM-yy HH:mm:ss zzz").toFormatter(Locale.ENGLISH);
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    public static final DateTimeFormatter FORMATTER_ASCTIME = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern("EEE MMM d HH:mm:ss yyyy").toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter[] STANDARD_PATTERNS = new DateTimeFormatter[]{FORMATTER_RFC1123, FORMATTER_RFC1036, FORMATTER_ASCTIME};
    static final ZoneId GMT_ID = ZoneId.of("GMT");
    @Deprecated
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    public static Date toDate(Instant instant) {
        return instant != null ? new Date(instant.toEpochMilli()) : null;
    }

    public static Instant toInstant(Date date) {
        return date != null ? Instant.ofEpochMilli(date.getTime()) : null;
    }

    public static LocalDateTime toUTC(Instant instant) {
        return instant != null ? instant.atZone(ZoneOffset.UTC).toLocalDateTime() : null;
    }

    public static LocalDateTime toUTC(Date date) {
        return DateUtils.toUTC(DateUtils.toInstant(date));
    }

    public static Instant parseDate(String dateValue, DateTimeFormatter ... dateFormatters) {
        Args.notNull(dateValue, "Date value");
        String v = dateValue;
        if (v.length() > 1 && v.startsWith("'") && v.endsWith("'")) {
            v = v.substring(1, v.length() - 1);
        }
        for (DateTimeFormatter dateFormatter : dateFormatters) {
            try {
                return Instant.from(dateFormatter.parse(v));
            }
            catch (DateTimeParseException dateTimeParseException) {
            }
        }
        return null;
    }

    public static Instant parseStandardDate(String dateValue) {
        return DateUtils.parseDate(dateValue, STANDARD_PATTERNS);
    }

    public static Instant parseStandardDate(MessageHeaders headers, String headerName) {
        if (headers == null) {
            return null;
        }
        Header header = headers.getFirstHeader(headerName);
        if (header == null) {
            return null;
        }
        return DateUtils.parseStandardDate(header.getValue());
    }

    public static String formatStandardDate(Instant instant) {
        return DateUtils.formatDate(instant, FORMATTER_RFC1123);
    }

    public static String formatDate(Instant instant, DateTimeFormatter dateTimeFormatter) {
        Args.notNull(instant, "Instant");
        Args.notNull(dateTimeFormatter, "DateTimeFormatter");
        return dateTimeFormatter.format(instant.atZone(GMT_ID));
    }

    @Deprecated
    public static Date parseDate(String dateValue) {
        return DateUtils.parseDate(dateValue, null, null);
    }

    @Deprecated
    public static Date parseDate(MessageHeaders headers, String headerName) {
        return DateUtils.toDate(DateUtils.parseStandardDate(headers, headerName));
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static Date parseDate(String dateValue, String[] dateFormats) {
        return DateUtils.parseDate(dateValue, dateFormats, null);
    }

    @Deprecated
    public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) {
        DateTimeFormatter[] dateTimeFormatters;
        if (dateFormats != null) {
            dateTimeFormatters = new DateTimeFormatter[dateFormats.length];
            for (int i = 0; i < dateFormats.length; ++i) {
                dateTimeFormatters[i] = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern(dateFormats[i]).toFormatter();
            }
        } else {
            dateTimeFormatters = STANDARD_PATTERNS;
        }
        return DateUtils.toDate(DateUtils.parseDate(dateValue, dateTimeFormatters));
    }

    @Deprecated
    public static String formatDate(Date date) {
        return DateUtils.formatStandardDate(DateUtils.toInstant(date));
    }

    @Deprecated
    public static String formatDate(Date date, String pattern) {
        Args.notNull(date, "Date");
        Args.notNull(pattern, "Pattern");
        return DateTimeFormatter.ofPattern(pattern).format(DateUtils.toInstant(date).atZone(GMT_ID));
    }

    @Deprecated
    public static void clearThreadLocal() {
    }

    private DateUtils() {
    }
}

