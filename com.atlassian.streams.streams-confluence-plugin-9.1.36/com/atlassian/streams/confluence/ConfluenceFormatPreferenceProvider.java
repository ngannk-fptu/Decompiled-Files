/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.streams.spi.FormatPreferenceProvider
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.streams.spi.FormatPreferenceProvider;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.time.ZoneId;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;

public class ConfluenceFormatPreferenceProvider
implements FormatPreferenceProvider {
    private final FormatSettingsManager formatSettingsManager;
    private final UserAccessor userAccessor;

    public ConfluenceFormatPreferenceProvider(FormatSettingsManager formatSettingsManager, UserAccessor userAccessor) {
        this.formatSettingsManager = (FormatSettingsManager)Preconditions.checkNotNull((Object)formatSettingsManager, (Object)"formatSettingsManager");
        this.userAccessor = (UserAccessor)Preconditions.checkNotNull((Object)userAccessor, (Object)"userAccessor");
    }

    public String getTimeFormatPreference() {
        return this.formatSettingsManager.getTimeFormat();
    }

    public String getDateFormatPreference() {
        return this.formatSettingsManager.getDateFormat();
    }

    public String getDateTimeFormatPreference() {
        return this.formatSettingsManager.getDateTimeFormat();
    }

    public DateTimeZone getUserTimeZone() {
        User user = AuthenticatedUserThreadLocal.getUser();
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences(user);
        try {
            return DateTimeZone.forTimeZone((TimeZone)userPreferences.getTimeZone().getWrappedTimeZone());
        }
        catch (IllegalArgumentException e) {
            return DateTimeZone.getDefault();
        }
    }

    public ZoneId getUserTimeZoneId() {
        User user = AuthenticatedUserThreadLocal.getUser();
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences(user);
        return userPreferences.getTimeZone().getWrappedTimeZone().toZoneId();
    }

    public boolean getDateRelativizePreference() {
        return true;
    }
}

