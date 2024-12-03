/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.core.message.AbstractI18nResolver
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.core.message.AbstractI18nResolver;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public class ConfluenceI18nResolver
extends AbstractI18nResolver {
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;

    public ConfluenceI18nResolver(I18NBeanFactory i18nBeanFactory, LocaleManager localeManager) {
        this.i18NBeanFactory = i18nBeanFactory;
        this.localeManager = localeManager;
    }

    public String resolveText(String key, Serializable[] arguments) {
        return this.getI18nBeanForCurrentUserDefaultLocale().getText(key, (Object[])arguments);
    }

    public String resolveText(Locale locale, String key, Serializable[] arguments) {
        I18NBean localeBean = this.i18NBeanFactory.getI18NBean(locale);
        return localeBean.getText(key, (Object[])arguments);
    }

    public String getRawText(String key) {
        return this.getI18nBeanForCurrentUserDefaultLocale().getTextStrict(key);
    }

    public String getRawText(Locale locale, String key) {
        return this.i18NBeanFactory.getI18NBean(locale).getTextStrict(key);
    }

    private I18NBean getI18nBeanForCurrentUserDefaultLocale() {
        return this.i18NBeanFactory.getI18NBean(this.currentUserDefaultLocale());
    }

    private Locale currentUserDefaultLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale) {
        Preconditions.checkNotNull((Object)prefix);
        Preconditions.checkNotNull((Object)locale);
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(locale);
        return i18NBean.getTranslationsForPrefix(prefix);
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix) {
        return this.getAllTranslationsForPrefix(prefix, Locale.getDefault());
    }
}

