/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.languages;

import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.user.User;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface LocaleManager {
    public static final Locale DEFAULT_LOCALE = Locale.UK;

    public Locale getLocale(User var1);

    default public @NonNull LocaleInfo getLocaleInfo(@Nullable User user) {
        Locale defaulLocale = this.getSiteDefaultLocale();
        return new LocaleInfo(defaulLocale, defaulLocale, LocaleInfo.SelectionReason.GLOBAL);
    }

    default public void invalidateLocaleInfoCache(@Nullable User user) {
    }

    public void setRequestLanguages(String var1);

    public void setLanguage(String var1);

    public Locale getSiteDefaultLocale();
}

