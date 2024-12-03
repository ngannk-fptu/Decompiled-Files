/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Scope
 */
package com.atlassian.confluence.core.datetime;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatterHelper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.Date;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
class DateFormatterBeans {
    @Resource
    private I18NBeanFactory i18NBeanFactory;
    @Resource
    private LocaleManager localeManager;
    @Resource
    private FormatSettingsManager formatSettingsManager;
    @Resource
    private UserPreferencesAccessor userPreferencesAccessor;

    DateFormatterBeans() {
    }

    @Bean
    @Scope(value="prototype")
    FriendlyDateFormatterHelper friendlyDateFormatterHelper() {
        return this.createFriendlyDateFormatterHelper(AuthenticatedUserThreadLocal.get(), new Date());
    }

    private FriendlyDateFormatterHelper createFriendlyDateFormatterHelper(ConfluenceUser user, Date now) {
        return new FriendlyDateFormatterHelper(new FriendlyDateFormatter(now, this.userPreferencesAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager)), this.i18NBeanFactory, this.localeManager);
    }
}

