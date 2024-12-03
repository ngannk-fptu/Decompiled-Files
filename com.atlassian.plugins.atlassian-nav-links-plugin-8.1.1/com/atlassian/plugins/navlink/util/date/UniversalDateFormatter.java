/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.navlink.util.date;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class UniversalDateFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXX");

    private UniversalDateFormatter() {
        throw new AssertionError((Object)"Don't instantiate me");
    }

    public static String format(ZonedDateTime date, ZoneId timeZone) {
        return FORMATTER.withZone(timeZone).format(date);
    }

    public static String formatUtc(ZonedDateTime date) {
        return UniversalDateFormatter.format(date, ZoneOffset.UTC);
    }

    public static ZonedDateTime parse(String date) {
        return ZonedDateTime.parse(date, FORMATTER);
    }
}

