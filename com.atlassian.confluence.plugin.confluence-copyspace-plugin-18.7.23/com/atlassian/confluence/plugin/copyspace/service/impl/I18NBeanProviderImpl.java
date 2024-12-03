/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.stereotype.Component;

@Component(value="i18NBeanProviderImpl")
public class I18NBeanProviderImpl
implements I18NBeanProvider {
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public I18NBeanProviderImpl(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }
}

