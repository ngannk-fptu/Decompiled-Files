/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.core.datetime;

import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.user.User;
import java.util.Date;

public class FriendlyDateFormatterHelper {
    private final FriendlyDateFormatter friendlyDateFormatter;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public FriendlyDateFormatterHelper(FriendlyDateFormatter friendlyDateFormatter, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.friendlyDateFormatter = friendlyDateFormatter;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public String format(Date date) {
        return this.format(date, AuthenticatedUserThreadLocal.get());
    }

    public String format(Date date, User user) {
        Message message = this.friendlyDateFormatter.getFormatMessage(date);
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(user));
        return i18NBean.getText(message.getKey(), message.getArguments());
    }
}

