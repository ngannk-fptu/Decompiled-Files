/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.datetime.DateFormatService
 *  org.joda.time.LocalDateTime
 */
package com.atlassian.confluence.api.impl.service.datetime;

import com.atlassian.confluence.api.service.datetime.DateFormatService;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import org.joda.time.LocalDateTime;

public class DateFormatServiceImpl
implements DateFormatService {
    private static final String ADG_DATE_FORMAT = "dd MMM yyyy";
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;

    public DateFormatServiceImpl(UserPreferencesAccessor userPreferencesAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
    }

    public String getFormattedDateByUserLocale(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Locale locale = this.localeManager.getLocale(user);
        if (this.getFormatForADG(locale)) {
            SimpleDateFormat formatter = new SimpleDateFormat(ADG_DATE_FORMAT, locale);
            return formatter.format(date.toDate());
        }
        ConfluenceUserPreferences preferences = this.userPreferencesAccessor.getConfluenceUserPreferences(user);
        DateFormatter dateFormatter = preferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        return dateFormatter.formatServerDateWithUserLocale(2, date.toDate());
    }

    public String getFormattedDateByUserLocale(LocalDate date) {
        if (date == null) {
            return null;
        }
        Locale locale = this.localeManager.getLocale(AuthenticatedUserThreadLocal.get());
        if (this.getFormatForADG(locale)) {
            return DateTimeFormatter.ofPattern(ADG_DATE_FORMAT, locale).format(date);
        }
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).format(date);
    }

    private boolean getFormatForADG(Locale locale) {
        return Locale.UK.equals(locale) || Locale.US.equals(locale);
    }

    public String getDateFormatPatternForUser() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Locale locale = this.localeManager.getLocale(user);
        if (this.getFormatForADG(locale)) {
            return ADG_DATE_FORMAT;
        }
        DateFormat format = DateFormat.getDateInstance(2, locale);
        return ((SimpleDateFormat)format).toPattern();
    }
}

