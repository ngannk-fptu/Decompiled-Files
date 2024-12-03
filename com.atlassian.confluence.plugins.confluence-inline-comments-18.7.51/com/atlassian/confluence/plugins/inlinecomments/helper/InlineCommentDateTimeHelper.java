/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.inlinecomments.helper;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.inlinecomments.utils.FriendlyDateFormatter;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.user.User;
import java.util.Date;
import java.util.Locale;

public class InlineCommentDateTimeHelper {
    private final I18NBeanFactory i18NBeanFactory;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final UserAccessor userAccessor;

    public InlineCommentDateTimeHelper(I18NBeanFactory i18NBeanFactory, FormatSettingsManager formatSettingsManager, LocaleManager localeManager, UserAccessor userAccessor) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
    }

    public String formatFriendlyDate(long time) {
        Date date = new Date(time);
        return this.formatFriendlyDate(date);
    }

    public String formatFriendlyDate(Date date) {
        Message message = this.getFriendlyDateFormatter().getFormatMessage(date);
        return this.getText(message.getKey(), message.getArguments());
    }

    private FriendlyDateFormatter getFriendlyDateFormatter() {
        return new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), this.getDateFormatter());
    }

    private DateFormatter getDateFormatter() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.userAccessor.getConfluenceUserPreferences((User)user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }

    private String getText(String key, Object ... args) {
        return this.i18NBeanFactory.getI18NBean(this.getUserLocale()).getText(key, args);
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }
}

