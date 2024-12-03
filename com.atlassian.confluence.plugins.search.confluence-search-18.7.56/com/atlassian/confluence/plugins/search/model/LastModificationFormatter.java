/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.search.model;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="lastModificationFormatter")
@Internal
public class LastModificationFormatter {
    private final I18NBeanFactory i18NBeanFactory;
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;

    @Autowired
    public LastModificationFormatter(@ComponentImport UserAccessor userAccessor, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport FormatSettingsManager formatSettingsManager) {
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
    }

    public String format(String lastModification, User user) {
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences(user);
        DateFormatter dateFormatter = userPreferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        FriendlyDateFormatter friendlyDateFormatter = new FriendlyDateFormatter(dateFormatter);
        Locale locale = this.localeManager.getLocale(user);
        if (StringUtils.isNotBlank((CharSequence)lastModification)) {
            Message formatMessage = friendlyDateFormatter.getFormatMessage(LuceneUtils.stringToDate((String)lastModification));
            return this.i18NBeanFactory.getI18NBean(locale).getText(formatMessage.getKey(), formatMessage.getArguments());
        }
        return "";
    }
}

