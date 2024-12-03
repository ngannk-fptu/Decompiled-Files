/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import java.util.TimeZone;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JiraDateStamp {
    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern((String)"yyyy-MM-dd").withZone(DateTimeZone.UTC);
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern((String)"yyyy-MM-dd'T'HH:mm:ss.sssZ");

    public static String fromDate(Date date) {
        return JiraDateStamp.getDateStamp(date, null);
    }

    public static String fromDateAndTimeZoneId(Date date, String timeZoneId) {
        return JiraDateStamp.getDateStamp(date, DateTimeZone.forID((String)timeZoneId));
    }

    private static String getDateStamp(Date date, DateTimeZone timeZone) {
        if (date instanceof DateTime) {
            return dateTimeFormat.withZone(timeZone != null ? timeZone : DateTimeZone.forTimeZone((TimeZone)JiraDateStamp.getTimeZoneOrDefault((DateTime)date))).print(date.getTime());
        }
        return dateFormat.print(date.getTime());
    }

    private static TimeZone getTimeZoneOrDefault(DateTime dateTime) {
        if (dateTime.getTimeZone() != null) {
            return dateTime.getTimeZone();
        }
        return TimeZone.getTimeZone("GMT");
    }
}

