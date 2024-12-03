/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.util.i18n.Message
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Interval
 *  org.joda.time.Period
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePeriod
 */
package com.atlassian.confluence.plugins.inlinecomments.utils;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.util.i18n.Message;
import java.util.Date;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;

public class FriendlyDateFormatter {
    private static final String FORMATTED = "inline.comments.date.friendly.formatted";
    private static final String NOW = "inline.comments.date.friendly.now";
    private static final String ONE_MINUTE_AGO = "inline.comments.date.friendly.one.min.ago";
    private static final String X_MINUTES_AGO = "inline.comments.date.friendly.x.mins.ago";
    private static final String ABOUT_ONE_HOUR_AGO = "inline.comments.date.friendly.about.one.hour.ago";
    private static final String ABOUT_X_HOURS_AGO = "inline.comments.date.friendly.about.x.hours.ago";
    private static final String ABOUT_X_DAY_AGO = "inline.comments.date.friendly.about.x.days.ago";
    private static final String YESTERDAY = "inline.comments.date.friendly.yesterday";
    private final DateTime now;
    private final DateFormatter dateFormatter;

    public FriendlyDateFormatter(DateFormatter dateFormatter) {
        this(new Date(), dateFormatter);
    }

    public FriendlyDateFormatter(Date now, DateFormatter dateFormatter) {
        this.now = new DateTime((Object)now);
        this.dateFormatter = dateFormatter;
    }

    public Message getFormatMessage(Date date) {
        DateTime dateTime = new DateTime((Object)date);
        if (dateTime.isAfter((ReadableInstant)this.now)) {
            return Message.getInstance((String)FORMATTED, (Object[])new Object[]{this.dateFormatter.formatDateTime(date)});
        }
        if (dateTime.isEqual((ReadableInstant)this.now) || dateTime.isAfter((ReadableInstant)this.now.minus((ReadablePeriod)Period.minutes((int)1)))) {
            return Message.getInstance((String)NOW);
        }
        if (dateTime.isAfter((ReadableInstant)this.now.minus((ReadablePeriod)Period.minutes((int)2)))) {
            return Message.getInstance((String)ONE_MINUTE_AGO);
        }
        if (dateTime.isAfter((ReadableInstant)this.now.minus((ReadablePeriod)Period.minutes((int)60)))) {
            return Message.getInstance((String)X_MINUTES_AGO, (Object[])new Object[]{this.getMinutesBetween(dateTime, this.now)});
        }
        if (dateTime.isAfter((ReadableInstant)this.now.minus((ReadablePeriod)Period.minutes((int)120)))) {
            return Message.getInstance((String)ABOUT_ONE_HOUR_AGO);
        }
        if (this.isYesterday(dateTime)) {
            return Message.getInstance((String)YESTERDAY);
        }
        if (dateTime.isAfter((ReadableInstant)this.now.minus((ReadablePeriod)Period.days((int)1)))) {
            return Message.getInstance((String)ABOUT_X_HOURS_AGO, (Object[])new Object[]{this.getRoundedHoursBetween(dateTime, this.now)});
        }
        if (dateTime.isAfter((ReadableInstant)this.now.minus((ReadablePeriod)Period.days((int)7)))) {
            return Message.getInstance((String)ABOUT_X_DAY_AGO, (Object[])new Object[]{this.getRoundedDaysBetween(dateTime, this.now)});
        }
        return Message.getInstance((String)FORMATTED, (Object[])new Object[]{this.dateFormatter.format(date)});
    }

    private boolean isYesterday(DateTime date) {
        DateTimeZone timeZone = DateTimeZone.forTimeZone((TimeZone)this.dateFormatter.getTimeZone().getWrappedTimeZone());
        DateTime end = this.now.withZone(timeZone).withTime(0, 0, 0, 0);
        DateTime start = end.minusDays(1);
        Interval interval = new Interval((ReadableInstant)start, (ReadableInstant)end);
        return interval.contains((ReadableInstant)date);
    }

    private int getRoundedHoursBetween(DateTime start, DateTime end) {
        Period period = new Period((ReadableInstant)start, (ReadableInstant)end);
        int hours = period.getHours();
        if (period.getMinutes() >= 30) {
            ++hours;
        }
        return hours;
    }

    private int getRoundedDaysBetween(DateTime start, DateTime end) {
        Period period = new Period((ReadableInstant)start, (ReadableInstant)end);
        int days = period.getDays();
        if (period.getHours() >= 12) {
            ++days;
        }
        return days;
    }

    private int getMinutesBetween(DateTime start, DateTime end) {
        return new Period((ReadableInstant)start, (ReadableInstant)end).getMinutes();
    }
}

