/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.util.date;

import java.time.ZonedDateTime;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JodaDateToJavaTimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(JodaDateToJavaTimeUtil.class);

    private JodaDateToJavaTimeUtil() {
        throw new AssertionError((Object)"Don't instantiate me");
    }

    public static DateTime javaTimeToJoda(@Nonnull ZonedDateTime zonedDateTime) {
        try {
            long instant = zonedDateTime.toInstant().toEpochMilli();
            DateTimeZone zone = DateTimeZone.forTimeZone((TimeZone)TimeZone.getTimeZone(zonedDateTime.getZone()));
            return new DateTime(instant, zone);
        }
        catch (IllegalArgumentException e) {
            logger.warn("Unrecognised zone id", (Throwable)e);
            throw e;
        }
    }

    public static ZonedDateTime jodaToJavaTime(@Nonnull DateTime dateTime) {
        return dateTime.toGregorianCalendar().toZonedDateTime();
    }
}

