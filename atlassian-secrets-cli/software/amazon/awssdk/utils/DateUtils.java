/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@ThreadSafe
@SdkProtectedApi
public final class DateUtils {
    static final DateTimeFormatter ALTERNATE_ISO_8601_DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").toFormatter().withZone(ZoneOffset.UTC);
    static final DateTimeFormatter RFC_822_DATE_TIME = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient().appendPattern("EEE, dd MMM yyyy HH:mm:ss").appendLiteral(' ').appendOffset("+HHMM", "GMT").toFormatter().withLocale(Locale.US).withResolverStyle(ResolverStyle.SMART).withChronology(IsoChronology.INSTANCE);
    private static final List<DateTimeFormatter> ALTERNATE_ISO_8601_FORMATTERS = Arrays.asList(DateTimeFormatter.ISO_INSTANT, ALTERNATE_ISO_8601_DATE_FORMAT, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    private static final int MILLI_SECOND_PRECISION = 3;

    private DateUtils() {
    }

    public static Instant parseIso8601Date(String dateString) {
        if (dateString.endsWith("+0000")) {
            dateString = dateString.substring(0, dateString.length() - 5).concat("Z");
        }
        DateTimeParseException exception = null;
        for (DateTimeFormatter formatter : ALTERNATE_ISO_8601_FORMATTERS) {
            try {
                return DateUtils.parseInstant(dateString, formatter);
            }
            catch (DateTimeParseException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw exception;
        }
        throw new RuntimeException("Failed to parse date " + dateString);
    }

    public static String formatIso8601Date(Instant date) {
        return DateTimeFormatter.ISO_INSTANT.format(date);
    }

    public static Instant parseRfc822Date(String dateString) {
        if (dateString == null) {
            return null;
        }
        return DateUtils.parseInstant(dateString, RFC_822_DATE_TIME);
    }

    public static String formatRfc822Date(Instant instant) {
        return RFC_822_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    public static Instant parseRfc1123Date(String dateString) {
        if (dateString == null) {
            return null;
        }
        return DateUtils.parseInstant(dateString, DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public static String formatRfc1123Date(Instant instant) {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    public static long numberOfDaysSinceEpoch(long milliSinceEpoch) {
        return Duration.ofMillis(milliSinceEpoch).toDays();
    }

    private static Instant parseInstant(String dateString, DateTimeFormatter formatter) {
        if (formatter.equals(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) {
            return formatter.parse((CharSequence)dateString, Instant::from);
        }
        return formatter.withZone(ZoneOffset.UTC).parse((CharSequence)dateString, Instant::from);
    }

    public static Instant parseUnixTimestampInstant(String dateString) throws NumberFormatException {
        if (dateString == null) {
            return null;
        }
        DateUtils.validateTimestampLength(dateString);
        BigDecimal dateValue = new BigDecimal(dateString);
        return Instant.ofEpochMilli(dateValue.scaleByPowerOfTen(3).longValue());
    }

    public static Instant parseUnixTimestampMillisInstant(String dateString) throws NumberFormatException {
        if (dateString == null) {
            return null;
        }
        return Instant.ofEpochMilli(Long.parseLong(dateString));
    }

    public static String formatUnixTimestampInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        BigDecimal dateValue = BigDecimal.valueOf(instant.toEpochMilli());
        return dateValue.scaleByPowerOfTen(-3).toPlainString();
    }

    private static void validateTimestampLength(String timestamp) {
        if (timestamp.length() > 20) {
            throw new RuntimeException("Input timestamp string must be no longer than 20 characters");
        }
    }
}

