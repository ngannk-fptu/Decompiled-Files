/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.message.MessageCollection
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.notifications.spi.salext;

import com.atlassian.plugin.notifications.spi.salext.UserI18nResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractUserI18nResolverImpl
implements UserI18nResolver {
    private final I18nResolver i18nResolver;
    private final ThreadLocal<Locale> currentLocale;
    private final LocaleResolver localeResolver;

    public AbstractUserI18nResolverImpl(I18nResolver i18nResolver, LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.currentLocale = new ThreadLocal();
    }

    public String getText(Locale locale, String key, Serializable ... arguments) {
        return this.i18nResolver.getText(locale, key, arguments);
    }

    public String getText(Locale locale, String key) {
        return this.i18nResolver.getText(locale, key);
    }

    public String getText(Locale locale, Message message) {
        return this.i18nResolver.getText(locale, message);
    }

    public String getText(String key, Serializable ... arguments) {
        Locale locale = this.currentLocale.get();
        if (locale != null) {
            return this.resolveText(locale, key, arguments);
        }
        return this.i18nResolver.getText(key, arguments);
    }

    public String getText(String key) {
        Locale locale = this.currentLocale.get();
        if (locale != null) {
            return this.resolveText(locale, key, new Serializable[0]);
        }
        return this.i18nResolver.getText(key);
    }

    public String getText(Message message) {
        if (this.currentLocale.get() != null) {
            return this.getText(message.getKey(), message.getArguments());
        }
        return this.i18nResolver.getText(message);
    }

    public Message createMessage(String key, Serializable ... arguments) {
        return this.i18nResolver.createMessage(key, arguments);
    }

    public MessageCollection createMessageCollection() {
        return this.i18nResolver.createMessageCollection();
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale) {
        return this.i18nResolver.getAllTranslationsForPrefix(prefix, locale);
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix) {
        return this.i18nResolver.getAllTranslationsForPrefix(prefix);
    }

    public String getRawText(String key) {
        return this.i18nResolver.getRawText(key);
    }

    public String getRawText(Locale locale, String key) {
        return this.i18nResolver.getRawText(locale, key);
    }

    private String resolveText(Locale locale, String key, Serializable ... arguments) {
        String pattern = this.getTranslation(locale, key);
        MessageFormat format = new MessageFormat(pattern, locale);
        return format.format(arguments);
    }

    private String getTranslation(Locale locale, String key) {
        Map<String, String> translations = this.getAllTranslationsForPrefix(key, locale);
        String translation = translations.get(key);
        return translation != null ? translation : key;
    }

    @Override
    public void setUser(UserKey userKey) {
        Locale locale = this.getLocaleForUser(userKey);
        if (locale != null && this.localeResolver.getSupportedLocales().contains(locale)) {
            this.currentLocale.set(locale);
        } else {
            this.currentLocale.set(null);
        }
    }

    protected abstract Locale getLocaleForUser(UserKey var1);
}

