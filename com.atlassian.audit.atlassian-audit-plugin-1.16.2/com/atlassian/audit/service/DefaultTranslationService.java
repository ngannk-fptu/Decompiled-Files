/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 */
package com.atlassian.audit.service;

import com.atlassian.audit.service.TranslationService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import java.util.Locale;

public class DefaultTranslationService
implements TranslationService {
    private final I18nResolver i18nResolver;
    private final LocaleResolver localeResolver;

    public DefaultTranslationService(I18nResolver i18nResolver, LocaleResolver localeResolver) {
        this.i18nResolver = i18nResolver;
        this.localeResolver = localeResolver;
    }

    @Override
    public String getSiteLocaleText(String key) {
        return this.i18nResolver.getText(this.getSiteLocale(), key);
    }

    @Override
    public String getUserLocaleWithApplicationLocaleFallbackText(String key) {
        return this.i18nResolver.getText(this.localeResolver.getLocale(), key);
    }

    private Locale getSiteLocale() {
        return this.localeResolver.getApplicationLocale();
    }

    @Override
    public Locale getUserLocale() {
        return this.localeResolver.getLocale();
    }
}

