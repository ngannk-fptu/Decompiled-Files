/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.util.TimeZone;
import net.fortuna.ical4j.util.Configurator;

public final class TimeZones {
    public static final String UTC_ID = "Etc/UTC";
    public static final String IBM_UTC_ID = "GMT";
    public static final String GMT_ID = "Etc/GMT";
    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("Etc/UTC");

    private TimeZones() {
    }

    public static boolean isUtc(TimeZone timezone) {
        return UTC_ID.equals(timezone.getID()) || IBM_UTC_ID.equals(timezone.getID());
    }

    public static TimeZone getDefault() {
        if ("true".equals(Configurator.getProperty("net.fortuna.ical4j.timezone.default.utc").orElse("false"))) {
            return TimeZones.getUtcTimeZone();
        }
        return TimeZone.getDefault();
    }

    public static TimeZone getDateTimeZone() {
        if ("true".equals(Configurator.getProperty("net.fortuna.ical4j.timezone.date.floating").orElse("false"))) {
            return TimeZone.getDefault();
        }
        return TimeZones.getUtcTimeZone();
    }

    public static TimeZone getUtcTimeZone() {
        return UTC_TIMEZONE;
    }
}

