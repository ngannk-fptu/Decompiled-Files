/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.impresence2.util;

import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultLocaleSupport
implements LocaleSupport {
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    @Autowired
    public DefaultLocaleSupport(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(AuthenticatedUserThreadLocal.getUser()));
    }

    @Override
    public String getText(String key) {
        return this.getI18NBean().getText(key);
    }

    @Override
    public String getText(String key, Object[] substitutions) {
        return this.getI18NBean().getText(key, substitutions);
    }

    @Override
    public String getText(String key, List list) {
        return this.getI18NBean().getText(key, list);
    }

    @Override
    public String getTextStrict(String key) {
        return this.getI18NBean().getTextStrict(key);
    }
}

