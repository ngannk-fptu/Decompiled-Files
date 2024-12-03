/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseDatePreferenceProvider;
import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LicenseDateFormatterImpl
implements LicenseDateFormatter {
    private LicenseDatePreferenceProvider licenseDatePreferenceProvider;

    public LicenseDateFormatterImpl(LicenseDatePreferenceProvider licenseDatePreferenceProvider) {
        this.licenseDatePreferenceProvider = Objects.requireNonNull(licenseDatePreferenceProvider, "licenseDatePreferenceProvider");
    }

    @Override
    public String formatDateTime(DateTime licenseDate) {
        return this.formatInternal(licenseDate, this.licenseDatePreferenceProvider.getDateTimeFormat());
    }

    @Override
    public String formatDate(DateTime licenseDate) {
        return this.formatInternal(licenseDate, this.licenseDatePreferenceProvider.getDateFormat());
    }

    private String formatInternal(DateTime licenseDate, String format) {
        Objects.requireNonNull(licenseDate, "licenseDate");
        DateTimeFormatter formatter = DateTimeFormat.forPattern((String)format).withZone(this.licenseDatePreferenceProvider.getUserTimeZone());
        return formatter.print(licenseDate.getMillis());
    }
}

