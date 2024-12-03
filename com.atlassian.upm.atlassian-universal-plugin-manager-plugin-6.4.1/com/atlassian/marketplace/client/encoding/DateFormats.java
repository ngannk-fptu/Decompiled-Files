/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 */
package com.atlassian.marketplace.client.encoding;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public abstract class DateFormats {
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern((String)"yyyy-MM-dd");
    public static DateTimeFormatter DATE_TIME_FORMAT = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    private DateFormats() {
    }
}

