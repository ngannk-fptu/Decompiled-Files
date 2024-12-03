/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.streams.api;

import java.time.ZonedDateTime;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@Deprecated
public class DateUtil {
    private DateUtil() {
    }

    public static ZonedDateTime toZonedDate(DateTime dt) {
        if (dt == null) {
            return null;
        }
        return dt.toGregorianCalendar().toZonedDateTime();
    }

    @Deprecated
    public static DateTime fromZonedDate(ZonedDateTime zdt) {
        if (zdt == null) {
            return null;
        }
        DateTimeZone zone = DateTimeZone.forID((String)zdt.getZone().getId());
        return new DateTime(zdt.toInstant().toEpochMilli(), zone);
    }
}

