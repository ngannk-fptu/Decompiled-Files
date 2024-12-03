/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatterHelper;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public class DateFormattersContextProvider
implements ContextProvider {
    private UserAccessor userAccessor;
    private FormatSettingsManager formatSettingsManager;
    private LocaleManager localeManager;
    private I18NBeanFactory i18NBeanFactory;

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences(AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = userPreferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        FriendlyDateFormatter friendlyDateFormatter = new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), dateFormatter);
        context.put("dateFormatter", dateFormatter);
        context.put("friendlyDateFormatterHelper", new FriendlyDateFormatterHelper(friendlyDateFormatter, this.i18NBeanFactory, this.localeManager));
        return context;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }
}

