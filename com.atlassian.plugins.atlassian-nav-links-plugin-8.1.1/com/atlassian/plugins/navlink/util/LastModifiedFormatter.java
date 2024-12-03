/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.time.FastDateFormat
 */
package com.atlassian.plugins.navlink.util;

import java.util.Locale;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.time.FastDateFormat;

public class LastModifiedFormatter {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance((String)"EEE, dd MMM yyyy HH:mm:ss 'GMT'", (TimeZone)TimeZone.getTimeZone("GMT"), (Locale)Locale.ENGLISH);

    @Nonnull
    public static String format(long millis) {
        return DATE_FORMAT.format(millis);
    }

    @Nonnull
    public static String formatCurrentTimeMillis() {
        return LastModifiedFormatter.format(System.currentTimeMillis());
    }
}

