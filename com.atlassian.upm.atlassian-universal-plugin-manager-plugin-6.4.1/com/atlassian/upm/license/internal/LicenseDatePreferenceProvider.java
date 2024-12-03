/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.upm.license.internal;

import org.joda.time.DateTimeZone;

public interface LicenseDatePreferenceProvider {
    public String getDateTimeFormat();

    public String getDateFormat();

    public DateTimeZone getUserTimeZone();
}

