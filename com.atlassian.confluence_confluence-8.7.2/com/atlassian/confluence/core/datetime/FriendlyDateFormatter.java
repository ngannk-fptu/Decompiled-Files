/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Range
 */
package com.atlassian.confluence.core.datetime;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.util.i18n.Message;
import com.google.common.collect.Range;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public class FriendlyDateFormatter {
    private static final String FORMATTED = "date.friendly.formatted";
    private static final String NOW = "date.friendly.now";
    private static final String A_MOMENT_AGO = "date.friendly.a.moment.ago";
    private static final String LESS_THAN_A_MINUTE_AGO = "less.than.one.min";
    private static final String ONE_MINUTE_AGO = "one.min.ago";
    private static final String X_MINUTES_AGO = "x.mins.ago";
    private static final String ABOUT_ONE_HOUR_AGO = "date.friendly.about.one.hour.ago";
    private static final String ABOUT_X_HOURS_AGO = "date.friendly.about.x.hours.ago";
    private static final String YESTERDAY = "date.friendly.yesterday";
    private final Instant now;
    private final DateFormatter dateFormatter;

    public FriendlyDateFormatter(DateFormatter dateFormatter) {
        this(new Date(), dateFormatter);
    }

    public FriendlyDateFormatter(Date now, DateFormatter dateFormatter) {
        this.now = now.toInstant();
        this.dateFormatter = dateFormatter;
    }

    public Message getFormatMessage(Date date) {
        Instant instant = date.toInstant();
        if (instant.isAfter(this.now)) {
            return Message.getInstance(FORMATTED, this.dateFormatter.formatDateTime(date));
        }
        if (instant.equals(this.now)) {
            return Message.getInstance(NOW);
        }
        if (instant.isAfter(this.now.minus(Duration.ofSeconds(4L)))) {
            return Message.getInstance(A_MOMENT_AGO);
        }
        if (instant.isAfter(this.now.minus(Duration.ofMinutes(1L)))) {
            return Message.getInstance(LESS_THAN_A_MINUTE_AGO);
        }
        if (instant.isAfter(this.now.minus(Duration.ofMinutes(2L)))) {
            return Message.getInstance(ONE_MINUTE_AGO);
        }
        if (instant.isAfter(this.now.minus(Duration.ofMinutes(50L)))) {
            return Message.getInstance(X_MINUTES_AGO, Duration.between(instant, this.now).toMinutes());
        }
        if (instant.isAfter(this.now.minus(Duration.ofMinutes(90L)))) {
            return Message.getInstance(ABOUT_ONE_HOUR_AGO);
        }
        if (this.isYesterday(instant) && instant.isBefore(this.now.minus(Duration.ofHours(5L)))) {
            return Message.getInstance(YESTERDAY, this.dateFormatter.formatTime(date));
        }
        if (instant.isAfter(this.now.minus(Duration.ofDays(1L)))) {
            return Message.getInstance(ABOUT_X_HOURS_AGO, this.getRoundedHoursBetween(instant, this.now));
        }
        return Message.getInstance(FORMATTED, this.dateFormatter.format(date));
    }

    private boolean isYesterday(Instant date) {
        TimeZone timeZone = this.dateFormatter.getTimeZone().getWrappedTimeZone();
        ZonedDateTime yesterdayEnd = this.now.atZone(timeZone.toZoneId()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime yesterdayStart = yesterdayEnd.minusDays(1L);
        return Range.closedOpen((Comparable)yesterdayStart.toInstant(), (Comparable)yesterdayEnd.toInstant()).contains((Comparable)date);
    }

    private long getRoundedHoursBetween(Instant start, Instant end) {
        Duration period = Duration.between(start, end);
        long hours = period.toHours();
        if (period.toMinutesPart() >= 30) {
            ++hours;
        }
        return hours;
    }
}

