/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.user.User;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateEntityFactoryImpl
implements DateEntityFactory {
    private static final String FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private I18NBeanFactory i18NBeanFactory;
    private FormatSettingsManager formatSettingsManager;
    private UserAccessor userAccessor;
    private LocaleManager localeManager;

    public DateEntityFactoryImpl(I18NBeanFactory i18nBeanFactory, FormatSettingsManager formatSettingsManager, UserAccessor userAccessor, LocaleManager localeManager) {
        this.i18NBeanFactory = i18nBeanFactory;
        this.formatSettingsManager = formatSettingsManager;
        this.userAccessor = userAccessor;
        this.localeManager = localeManager;
    }

    @Override
    public DateEntity buildDateEntity(Date date) {
        if (date == null) {
            return null;
        }
        DateEntity entity = new DateEntity();
        Message message = this.getFriendlyDateFormatter().getFormatMessage(date);
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
        entity.setFriendly(i18NBean.getText(message.getKey(), message.getArguments()));
        entity.setDate(this.getSimpleDateFormat().format(date));
        return entity;
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(FULL_DATE_FORMAT);
    }

    private FriendlyDateFormatter getFriendlyDateFormatter() {
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = userPreferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        return new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), dateFormatter);
    }
}

