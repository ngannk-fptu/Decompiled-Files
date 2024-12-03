/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSubCalendarEventTransformer<T extends SubCalendarEventTransformerFactory.TransformParameters>
implements SubCalendarEventTransformerFactory.SubCalendarEventTransformer<T> {
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    protected AbstractSubCalendarEventTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    protected String getText(ConfluenceUser forUser, String i18nKey, List substitutions) {
        return this.getI18NBean(forUser).getText(i18nKey, substitutions);
    }

    private I18NBean getI18NBean(ConfluenceUser forUser) {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)forUser));
    }

    protected String getText(ConfluenceUser forUser, String i18nkey) {
        return this.getI18NBean(forUser).getText(i18nkey);
    }

    protected String getText(ConfluenceUser forUser, String i18nKey, Object ... substitutions) {
        return this.getI18NBean(forUser).getText(i18nKey, Arrays.asList(substitutions));
    }
}

