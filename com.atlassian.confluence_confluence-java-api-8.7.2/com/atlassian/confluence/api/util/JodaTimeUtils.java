/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Duration
 *  org.joda.time.LocalDate
 */
package com.atlassian.confluence.api.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@Deprecated
public final class JodaTimeUtils {
    public static org.joda.time.LocalDate convert(LocalDate localDate) {
        return localDate == null ? null : new org.joda.time.LocalDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    public static LocalDate convert(org.joda.time.LocalDate localDate) {
        return localDate == null ? null : LocalDate.of(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    public static DateTime convert(OffsetDateTime dateTime) {
        return dateTime == null ? null : new DateTime(dateTime.toInstant().toEpochMilli(), DateTimeZone.forTimeZone((TimeZone)TimeZone.getTimeZone(dateTime.getOffset())));
    }

    public static OffsetDateTime convert(DateTime dateTime) {
        return dateTime == null ? null : OffsetDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getMillis()), dateTime.getZone().toTimeZone().toZoneId());
    }

    public static Duration convert(org.joda.time.Duration duration) {
        return duration == null ? null : Duration.ofMillis(duration.getMillis());
    }
}

