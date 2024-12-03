/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadablePartial
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.DateTimeFormatterBuilder
 *  org.joda.time.format.DateTimeParser
 */
package com.atlassian.confluence.extra.calendar3.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

public class RecurrenceIdJodaTimeHelper {
    private static final String DATETIME_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final String DATETIME_UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    private static final String DATE_PATTERN = "yyyyMMdd";
    public static final DateTimeParser[] parsers = new DateTimeParser[]{DateTimeFormat.forPattern((String)"yyyyMMdd'T'HHmmss").getParser(), DateTimeFormat.forPattern((String)"yyyyMMdd'T'HHmmss'Z'").getParser(), DateTimeFormat.forPattern((String)"yyyyMMdd").getParser()};
    private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

    private RecurrenceIdJodaTimeHelper() {
    }

    private static DateTimeFormatter getDateTimeFormatter(DateTimeZone dateTimeZone) {
        return dateTimeFormatter.withZone(dateTimeZone);
    }

    public static DateTime getJodaDateTimeFromRecurrenceId(String recurrenceId, DateTimeZone dateTimeZone) {
        DateTimeFormatter formatter = RecurrenceIdJodaTimeHelper.getDateTimeFormatter(dateTimeZone);
        return formatter.parseDateTime(recurrenceId);
    }

    public static int compareRecurrenceIds(String aRecurrenceId, String anotherRecurrenceId, DateTimeZone dateTimeZone) {
        DateTimeFormatter formatter = RecurrenceIdJodaTimeHelper.getDateTimeFormatter(dateTimeZone);
        return formatter.parseDateTime(aRecurrenceId).toLocalDate().compareTo((ReadablePartial)formatter.parseDateTime(anotherRecurrenceId).toLocalDate());
    }
}

