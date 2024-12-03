/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.streams.spi;

import java.time.ZoneId;
import org.joda.time.DateTimeZone;

public interface FormatPreferenceProvider {
    public String getTimeFormatPreference();

    public String getDateFormatPreference();

    public String getDateTimeFormatPreference();

    @Deprecated
    public DateTimeZone getUserTimeZone();

    default public ZoneId getUserTimeZoneId() {
        return ZoneId.systemDefault();
    }

    public boolean getDateRelativizePreference();
}

