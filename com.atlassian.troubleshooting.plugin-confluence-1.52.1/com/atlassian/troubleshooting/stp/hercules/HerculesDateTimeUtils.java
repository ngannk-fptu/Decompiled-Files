/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeUtils
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Days
 *  org.joda.time.Months
 *  org.joda.time.ReadableInstant
 *  org.joda.time.Weeks
 *  org.joda.time.Years
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.google.common.annotations.VisibleForTesting;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.ReadableInstant;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class HerculesDateTimeUtils {
    @VisibleForTesting
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormat.forPattern((String)"d MMMM yyyy h:mma z");

    public static String getTimeInRelativeFormat(long logTimestamp) {
        long nowTimeStamp = DateTimeUtils.currentTimeMillis();
        DateTime logDateTime = new DateTime(logTimestamp, DateTimeZone.forTimeZone((TimeZone)TimeZone.getDefault()));
        DateTime nowDateTime = new DateTime(nowTimeStamp, DateTimeZone.forTimeZone((TimeZone)TimeZone.getDefault()));
        if (nowDateTime.isEqual((ReadableInstant)logDateTime) || nowDateTime.isAfter((ReadableInstant)logDateTime)) {
            return HerculesDateTimeUtils.convertToRelativeFormat(logDateTime, nowDateTime);
        }
        return DEFAULT_FORMATTER.print((ReadableInstant)logDateTime);
    }

    private static String convertToRelativeFormat(DateTime logDateTime, DateTime nowDateTime) {
        DateTime nowDateWithTimeAtStartOfDay;
        long secondsAgo = (nowDateTime.getMillis() - logDateTime.getMillis()) / 1000L;
        if (secondsAgo < 60L) {
            return "Just now";
        }
        long minutesAgo = secondsAgo / 60L;
        if (minutesAgo < 60L) {
            if (minutesAgo == 1L) {
                return "1 minute ago";
            }
            return minutesAgo + " minutes ago";
        }
        long hoursAgo = minutesAgo / 60L;
        if (hoursAgo < 24L) {
            if (hoursAgo == 1L) {
                return "1 hour ago";
            }
            return hoursAgo + " hours ago";
        }
        DateTime logDateWithTimeAtStartOfDay = logDateTime.withTimeAtStartOfDay();
        long daysAgo = Days.daysBetween((ReadableInstant)logDateWithTimeAtStartOfDay, (ReadableInstant)(nowDateWithTimeAtStartOfDay = nowDateTime.withTimeAtStartOfDay())).getDays();
        if (daysAgo < 7L) {
            if (daysAgo == 1L) {
                return "1 day ago";
            }
            return daysAgo + " days ago";
        }
        long weeksAgo = Weeks.weeksBetween((ReadableInstant)logDateWithTimeAtStartOfDay, (ReadableInstant)nowDateWithTimeAtStartOfDay).getWeeks();
        if (nowDateWithTimeAtStartOfDay.minusMonths(1).isBefore((ReadableInstant)logDateWithTimeAtStartOfDay)) {
            if (weeksAgo == 1L) {
                return "1 week ago";
            }
            return weeksAgo + " weeks ago";
        }
        long monthsAgo = Months.monthsBetween((ReadableInstant)logDateWithTimeAtStartOfDay, (ReadableInstant)nowDateWithTimeAtStartOfDay).getMonths();
        if (monthsAgo < 12L) {
            if (monthsAgo == 1L) {
                return "1 month ago";
            }
            return monthsAgo + " months ago";
        }
        long yearsAgo = Years.yearsBetween((ReadableInstant)logDateWithTimeAtStartOfDay, (ReadableInstant)nowDateWithTimeAtStartOfDay).getYears();
        if (yearsAgo == 1L) {
            return "1 year ago";
        }
        return yearsAgo + " years ago";
    }
}

