/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Locale;

public class HttpDateTimeFormatter {
    private static DateTimeFormatter IMFFIXDATE = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH).withZone(ZoneOffset.UTC);
    private static DateTimeFormatter RFC850DATE = new DateTimeFormatterBuilder().appendPattern("EEEE, dd-MMM-").appendValueReduced((TemporalField)ChronoField.YEAR_OF_ERA, 2, 2, LocalDate.now().minusYears(50L)).appendPattern(" HH:mm:ss 'GMT'").toFormatter().withLocale(Locale.ENGLISH).withZone(ZoneOffset.UTC);
    private static DateTimeFormatter ASCTIMEDATE = new DateTimeFormatterBuilder().appendPattern("EEE MMM ").padNext(2, ' ').appendValue(ChronoField.DAY_OF_MONTH).appendPattern(" HH:mm:ss yyyy").toFormatter().withLocale(Locale.ENGLISH).withZone(ZoneOffset.UTC);

    public static long parseImfFixedDate(String fieldValue) {
        ZonedDateTime d = ZonedDateTime.parse(fieldValue, IMFFIXDATE);
        return d.toInstant().toEpochMilli();
    }

    public static long parseRfc850Date(String fieldValue) {
        ZonedDateTime d = ZonedDateTime.parse(fieldValue, RFC850DATE);
        return d.toInstant().toEpochMilli();
    }

    public static long parseAscTimeDate(String fieldValue) {
        ZonedDateTime d = ZonedDateTime.parse(fieldValue, ASCTIMEDATE);
        return d.toInstant().toEpochMilli();
    }

    public static long parse(String fieldValue) {
        try {
            return HttpDateTimeFormatter.parseImfFixedDate(fieldValue);
        }
        catch (DateTimeParseException ex) {
            try {
                return HttpDateTimeFormatter.parseRfc850Date(fieldValue);
            }
            catch (DateTimeParseException ex2) {
                try {
                    return HttpDateTimeFormatter.parseAscTimeDate(fieldValue);
                }
                catch (DateTimeParseException ex3) {
                    throw ex;
                }
            }
        }
    }

    public static String format(long millisSinceEpoch) {
        return IMFFIXDATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }

    public static String formatImfFixed(long millisSinceEpoch) {
        return IMFFIXDATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }

    public static String formatRfc850(long millisSinceEpoch) {
        return RFC850DATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }

    public static String formatAscTime(long millisSinceEpoch) {
        return ASCTIMEDATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }
}

