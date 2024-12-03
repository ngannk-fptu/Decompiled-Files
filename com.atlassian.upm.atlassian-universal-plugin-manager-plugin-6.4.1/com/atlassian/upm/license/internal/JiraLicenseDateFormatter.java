/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.datetime.DateTimeFormatter
 *  com.atlassian.jira.datetime.DateTimeStyle
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import java.util.Objects;
import org.joda.time.DateTime;

public class JiraLicenseDateFormatter
extends DefaultLicenseDateFormatter {
    private final DateTimeFormatter dateTimeFormatter;

    public JiraLicenseDateFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = Objects.requireNonNull(dateTimeFormatter, "dateTimeFormatter");
    }

    @Override
    public String formatDateTime(DateTime licenseDate) {
        return this.formatDateInternal(licenseDate, DateTimeStyle.COMPLETE);
    }

    @Override
    public String formatDate(DateTime licenseDate) {
        return this.formatDateInternal(licenseDate, DateTimeStyle.DATE);
    }

    private String formatDateInternal(DateTime licenseDate, DateTimeStyle style) {
        return this.dateTimeFormatter.forLoggedInUser().withStyle(style).format(licenseDate.toDate());
    }
}

