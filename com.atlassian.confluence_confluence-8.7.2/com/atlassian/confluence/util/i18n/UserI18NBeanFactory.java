/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserI18NBeanFactory
implements I18NBeanFactory {
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;

    @Override
    public @NonNull I18NBean getI18NBean(@NonNull Locale locale) {
        return this.i18NBeanFactory.getI18NBean(locale);
    }

    @Override
    public @NonNull I18NBean getI18NBean() {
        return this.getI18NBean(this.getUserLocale());
    }

    @Override
    public @NonNull String getStateHash() {
        return this.i18NBeanFactory.getStateHash();
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale(this.getRemoteUser());
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    private User getRemoteUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

