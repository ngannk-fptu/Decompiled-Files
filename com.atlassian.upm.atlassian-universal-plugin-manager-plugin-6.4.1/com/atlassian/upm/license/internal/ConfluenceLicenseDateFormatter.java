/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.upm.license.internal;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import com.atlassian.user.User;
import java.util.Objects;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ConfluenceLicenseDateFormatter
extends DefaultLicenseDateFormatter {
    private final FormatSettingsManager formatSettingsManager;
    private final UserAccessor userAccessor;

    public ConfluenceLicenseDateFormatter(FormatSettingsManager formatSettingsManager, UserAccessor userAccessor) {
        this.formatSettingsManager = Objects.requireNonNull(formatSettingsManager, "formatSettingsManager");
        this.userAccessor = Objects.requireNonNull(userAccessor, "userAccessor");
    }

    @Override
    public String formatDateTime(DateTime licenseDate) {
        return this.formatInternal(licenseDate, this.formatSettingsManager.getDateTimeFormat());
    }

    @Override
    public String formatDate(DateTime licenseDate) {
        return this.formatInternal(licenseDate, this.formatSettingsManager.getDateFormat());
    }

    @Override
    protected DateTimeZone getUserTimeZone() {
        User user = AuthenticatedUserThreadLocal.getUser();
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences(user);
        try {
            return DateTimeZone.forTimeZone((TimeZone)userPreferences.getTimeZone().getWrappedTimeZone());
        }
        catch (IllegalArgumentException e) {
            return DateTimeZone.getDefault();
        }
    }
}

