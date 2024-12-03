/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.xhtml.api.StorageFormatService;
import org.joda.time.DateTime;

@Deprecated(forRemoval=true)
public class DefaultStorageFormatService
implements StorageFormatService {
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;

    public DefaultStorageFormatService(UserPreferencesAccessor userPreferencesAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
    }

    @Override
    public String createStorageFormatForDate(DateTime date) {
        ConfluenceUserPreferences preferences = this.userPreferencesAccessor.getConfluenceUserPreferences(AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = preferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        return String.format("<time datetime=\"%s\"></time>", dateFormatter.formatGivenString("yyyy-MM-dd", date.toDate()));
    }
}

