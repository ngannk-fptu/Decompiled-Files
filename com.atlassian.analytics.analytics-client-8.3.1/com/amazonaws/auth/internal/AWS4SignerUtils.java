/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.amazonaws.auth.internal;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class AWS4SignerUtils {
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern((String)"yyyyMMdd").withZoneUTC();
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern((String)"yyyyMMdd'T'HHmmss'Z'").withZoneUTC();

    public static String formatDateStamp(long timeMilli) {
        return dateFormatter.print(timeMilli);
    }

    public static String formatTimestamp(long timeMilli) {
        return timeFormatter.print(timeMilli);
    }
}

