/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.spi.FormatPreferenceProvider;
import java.time.ZoneId;
import org.joda.time.DateTimeZone;

public class DefaultFormatPreferenceProvider
implements FormatPreferenceProvider {
    @Override
    public String getTimeFormatPreference() {
        return "h:mm a";
    }

    @Override
    public String getDateFormatPreference() {
        return "d MMM yyyy";
    }

    @Override
    public String getDateTimeFormatPreference() {
        return this.getDateFormatPreference() + " " + this.getTimeFormatPreference();
    }

    @Override
    @Deprecated
    public DateTimeZone getUserTimeZone() {
        return DateTimeZone.getDefault();
    }

    @Override
    public ZoneId getUserTimeZoneId() {
        return ZoneId.systemDefault();
    }

    @Override
    public boolean getDateRelativizePreference() {
        return true;
    }
}

