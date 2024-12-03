/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.message.MessageCollection
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.common;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;

public class StreamsI18nResolverImpl
implements StreamsI18nResolver {
    private final I18nResolver i18nResolver;
    private final LocaleResolver localeResolver;
    private final ThreadLocal<Locale> currentLocale;

    public StreamsI18nResolverImpl(@Qualifier(value="i18nResolver") I18nResolver i18nResolver, LocaleResolver localeResolver) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.localeResolver = (LocaleResolver)Preconditions.checkNotNull((Object)localeResolver, (Object)"localeResolver");
        this.currentLocale = new ThreadLocal();
    }

    public String getText(String key, Serializable ... arguments) {
        Iterator iterator = this.getCurrentLocale().iterator();
        if (iterator.hasNext()) {
            Locale locale = (Locale)iterator.next();
            return this.resolveText(key, arguments);
        }
        return this.i18nResolver.getText(key, arguments);
    }

    public String getText(Locale locale, String key, Serializable ... arguments) {
        return this.i18nResolver.getText(locale, key, arguments);
    }

    public String getText(String key) {
        Iterator iterator = this.getCurrentLocale().iterator();
        if (iterator.hasNext()) {
            Locale locale = (Locale)iterator.next();
            return this.resolveText(key, new Serializable[0]);
        }
        return this.i18nResolver.getText(key);
    }

    public String getText(Locale locale, String key) {
        return this.i18nResolver.getText(locale, key);
    }

    public String getText(Message message) {
        Iterator iterator = this.getCurrentLocale().iterator();
        if (iterator.hasNext()) {
            Locale locale = (Locale)iterator.next();
            return this.getText(message.getKey(), message.getArguments());
        }
        return this.i18nResolver.getText(message);
    }

    public String getText(Locale locale, Message message) {
        return this.i18nResolver.getText(locale, message);
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

    private String resolveText(String key, Serializable ... arguments) {
        String pattern = this.getTranslation(key);
        MessageFormat format = new MessageFormat(pattern, (Locale)this.getCurrentLocale().get());
        return format.format(arguments);
    }

    private String getTranslation(String key) {
        Map<String, String> translations = this.getAllTranslationsForPrefix(key, (Locale)this.getCurrentLocale().get());
        String translation = translations.get(key);
        return translation != null ? translation : key;
    }

    public void setRequestLanguages(String requestLanguages) {
        if (requestLanguages == null) {
            this.setCurrentLocale(null);
            return;
        }
        Iterable<String> languages = this.toLocaleNames(requestLanguages);
        Map<String, Locale> localeMap = this.getLocaleMap(this.localeResolver.getSupportedLocales());
        for (String lang : languages) {
            Locale locale = localeMap.get(lang);
            if (locale == null) continue;
            this.setCurrentLocale(locale);
            break;
        }
    }

    private Option<Locale> getCurrentLocale() {
        return Option.option((Object)this.currentLocale.get());
    }

    private void setCurrentLocale(Locale locale) {
        this.currentLocale.set(locale);
    }

    private Iterable<String> toLocaleNames(String acceptLanguage) {
        String[] languages = acceptLanguage.split(",|;");
        ArrayList<String> localeNames = new ArrayList<String>();
        for (String l : languages) {
            if (l.startsWith("q=")) continue;
            String[] langElem = l.trim().split("-");
            String language = langElem[0];
            String country = langElem.length > 1 ? langElem[1] : "";
            Locale locale = new Locale(language, country);
            localeNames.add(locale.toString());
        }
        return ImmutableList.copyOf(localeNames);
    }

    private Map<String, Locale> getLocaleMap(Set<Locale> locales) {
        HashMap<String, Locale> localeMap = new HashMap<String, Locale>();
        for (Locale locale : locales) {
            localeMap.put(locale.toString(), locale);
            localeMap.put(locale.getLanguage(), locale);
        }
        return localeMap;
    }
}

