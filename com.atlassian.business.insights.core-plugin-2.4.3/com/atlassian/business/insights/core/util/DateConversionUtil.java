/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.util;

import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class DateConversionUtil {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String SYSTEM_TIME_FORMAT = "yyyy-MM-dd'T'HH-mm-ss-SSSX";
    private final TimeZoneManager timeZoneManager;

    public DateConversionUtil(@Nonnull TimeZoneManager timeZoneManager) {
        Objects.requireNonNull(timeZoneManager);
        this.timeZoneManager = timeZoneManager;
    }

    @Nonnull
    public static Instant parseIsoOffsetDatetime(@Nonnull String fromDatetime) {
        Objects.requireNonNull(fromDatetime);
        return ISO_DATE_FORMATTER.parse((CharSequence)fromDatetime, Instant::from);
    }

    @Nonnull
    public static ZonedDateTime parseIsoOffsetDatetimeToZonedDateTime(@Nonnull String dateTime) {
        Objects.requireNonNull(dateTime);
        return ZonedDateTime.parse(dateTime, ISO_DATE_FORMATTER);
    }

    @Nonnull
    public static Instant truncateToMinutes(@Nonnull Instant instant) {
        return Objects.requireNonNull(instant).truncatedTo(ChronoUnit.MINUTES);
    }

    @Nonnull
    public static LocalTime parseTimeAsLocalTime(@Nonnull String time) {
        Objects.requireNonNull(time);
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    @Nonnull
    public static String formatToIso(@Nonnull Instant instant, @Nonnull ZoneId zoneId) {
        Objects.requireNonNull(instant, "instant must not be null");
        Objects.requireNonNull(zoneId, "zoneId must not be null");
        return ISO_DATE_FORMATTER.withZone(zoneId).format(instant);
    }

    @Nonnull
    public Instant parseDateAsLocalStartOfDay(@Nonnull String date) {
        Objects.requireNonNull(date);
        ZoneId systemTimeZone = this.getSystemTimeZoneId();
        return Instant.from(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(systemTimeZone)).atStartOfDay(systemTimeZone));
    }

    @Nonnull
    public String formatToIso(@Nonnull Instant instant) {
        Objects.requireNonNull(instant);
        return DateConversionUtil.formatToIso(instant, this.getSystemTimeZoneId());
    }

    @Nonnull
    public String formatToSystemTimeZone(@Nonnull Instant instant) {
        return DateTimeFormatter.ofPattern(SYSTEM_TIME_FORMAT).withZone(this.getSystemTimeZoneId()).format(Objects.requireNonNull(instant));
    }

    private ZoneId getSystemTimeZoneId() {
        return this.timeZoneManager.getDefaultTimeZone().toZoneId();
    }
}

