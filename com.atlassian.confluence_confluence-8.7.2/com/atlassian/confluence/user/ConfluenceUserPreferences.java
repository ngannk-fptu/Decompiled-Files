/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceUserPreferences {
    private UserPreferences prefs = null;

    public ConfluenceUserPreferences() {
    }

    public ConfluenceUserPreferences(PropertySet properties) {
        this.prefs = new UserPreferences(properties);
    }

    public TimeZone getTimeZone() {
        if (this.prefs == null) {
            return TimeZone.getDefault();
        }
        return TimeZone.getInstance(this.prefs.getString("confluence.user.time.zone"));
    }

    public DateFormatter getDateFormatter(FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        return new DateFormatter(this.getTimeZone(), formatSettingsManager, localeManager);
    }

    public void setTimeZone(String timeZoneID) throws AtlassianCoreException {
        if (this.prefs == null) {
            return;
        }
        this.prefs.setString("confluence.user.time.zone", timeZoneID);
    }

    public Locale getLocale() {
        if (this.prefs == null) {
            return null;
        }
        String localePreference = this.prefs.getString("confluence.user.locale");
        if (StringUtils.isBlank((CharSequence)localePreference)) {
            return null;
        }
        return LocaleParser.toLocale(localePreference);
    }

    public boolean isShowDifferencesInNotificationEmails() {
        if (this.prefs == null) {
            return true;
        }
        return this.prefs.getBoolean("confluence.prefs.email.show.diff");
    }

    public boolean isWatchingOwnContent() {
        if (this.prefs == null) {
            return true;
        }
        return this.prefs.getBoolean("confluence.prefs.watch.my.own.content");
    }

    public UserPreferences getWrappedPreferences() {
        return this.prefs;
    }
}

