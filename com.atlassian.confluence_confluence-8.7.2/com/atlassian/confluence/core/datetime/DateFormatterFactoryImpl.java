/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 */
package com.atlassian.confluence.core.datetime;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.util.Date;

public class DateFormatterFactoryImpl
implements DateFormatterFactory {
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final TimeZoneManager timeZoneManager;

    public DateFormatterFactoryImpl(FormatSettingsManager formatSettingsManager, LocaleManager localeManager, TimeZoneManager timeZoneManager) {
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.timeZoneManager = timeZoneManager;
    }

    @Override
    public DateFormatter createForUser() {
        return new DateFormatter(this.timeZoneManager.getUserTimeZone(), this.formatSettingsManager, this.localeManager);
    }

    @Override
    public DateFormatter createGlobal() {
        return new DateFormatter(this.timeZoneManager.getDefaultTimeZone(), this.formatSettingsManager, this.localeManager);
    }

    @Override
    public FriendlyDateFormatter createFriendlyForUser(Date now) {
        return new FriendlyDateFormatter(now, this.createForUser());
    }

    @Override
    public FriendlyDateFormatter createFriendlyForUser() {
        return this.createFriendlyForUser(new Date());
    }
}

