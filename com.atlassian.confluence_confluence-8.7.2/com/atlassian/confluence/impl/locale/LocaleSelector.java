/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.locale;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.Nullable;

final class LocaleSelector {
    private final LanguageManager languageManager;

    public LocaleSelector(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @VisibleForTesting
    @Nullable Locale getBestLanguage(Collection<Locale> preferredLocales) {
        List<Language> installedLanguages = this.languageManager.getLanguages();
        Locale goodEnoughMatch = null;
        String lastLanguage = null;
        for (Locale preferredLocale : preferredLocales) {
            if (goodEnoughMatch != null && !preferredLocale.getLanguage().equalsIgnoreCase(lastLanguage)) {
                return goodEnoughMatch;
            }
            for (Language installedLanguage : installedLanguages) {
                Locale installedLocale = installedLanguage.getLocale();
                if (!installedLocale.getLanguage().equalsIgnoreCase(preferredLocale.getLanguage())) continue;
                if (goodEnoughMatch == null) {
                    goodEnoughMatch = installedLocale;
                }
                if (!installedLanguage.getCountry().equalsIgnoreCase(preferredLocale.getCountry())) continue;
                return installedLocale;
            }
            lastLanguage = preferredLocale.getLanguage();
        }
        return goodEnoughMatch;
    }
}

