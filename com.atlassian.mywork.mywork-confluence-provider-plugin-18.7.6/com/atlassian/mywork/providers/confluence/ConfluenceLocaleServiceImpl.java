/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.Language
 *  com.atlassian.confluence.languages.LanguageManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.mywork.service.LocaleService
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.mywork.service.LocaleService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;

public class ConfluenceLocaleServiceImpl
implements LocaleService {
    private final LanguageManager languageManager;

    public ConfluenceLocaleServiceImpl(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public Iterable<Locale> getLocales() {
        return Lists.transform((List)this.languageManager.getLanguages(), (Function)new Function<Language, Locale>(){

            public Locale apply(Language language) {
                return language.getLocale();
            }
        });
    }

    public Locale getDefaultLocale() {
        return LocaleManager.DEFAULT_LOCALE;
    }
}

