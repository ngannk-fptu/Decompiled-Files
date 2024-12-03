/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.ISODateTimeFormat
 */
package com.atlassian.confluence.rest.serialization;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.ISODateTimeFormat;

public class Dates {
    @Deprecated
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    @Deprecated
    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

    public static String asTimeString(DateTime date) {
        return date != null ? ISODateTimeFormat.dateTime().print((ReadableInstant)date) : null;
    }

    public static DateTime fromTimeString(String time) throws IllegalArgumentException {
        return time != null ? ISODateTimeFormat.dateTime().parseDateTime(time) : null;
    }

    public static String asDateString(DateTime date) {
        return date != null ? ISODateTimeFormat.date().print((ReadableInstant)date) : null;
    }

    public static DateTime fromDateString(String date) throws IllegalArgumentException {
        return date != null ? ISODateTimeFormat.date().parseDateTime(date) : null;
    }

    private Dates() {
    }
}

