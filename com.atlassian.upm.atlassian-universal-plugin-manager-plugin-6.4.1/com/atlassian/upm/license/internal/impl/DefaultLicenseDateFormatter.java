/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.license.internal.LicenseDateFormatter;
import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DefaultLicenseDateFormatter
implements LicenseDateFormatter {
    private static final String DEFAULT_DATE_TIME_FORMAT = "d MMM yyyy";
    private static final String DEFAULT_DATE_FORMAT = "d MMM yyyy";

    @Override
    public String formatDateTime(DateTime licenseDate) {
        return this.formatInternal(licenseDate, "d MMM yyyy");
    }

    @Override
    public String formatDate(DateTime licenseDate) {
        return this.formatInternal(licenseDate, "d MMM yyyy");
    }

    protected String formatInternal(DateTime licenseDate, String format) {
        Objects.requireNonNull(licenseDate, "licenseDate");
        DateTimeFormatter formatter = DateTimeFormat.forPattern((String)format).withZone(this.getUserTimeZone());
        return formatter.print(licenseDate.getMillis());
    }

    protected DateTimeZone getUserTimeZone() {
        try {
            return DateTimeZone.forID((String)System.getProperty("user.timezone"));
        }
        catch (IllegalArgumentException e) {
            return DateTimeZone.getDefault();
        }
    }
}

