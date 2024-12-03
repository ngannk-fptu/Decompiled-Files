/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.v2.macro.BaseMacro
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.v2.macro.BaseMacro;
import java.util.List;

public abstract class LocaleAwareMacro
extends BaseMacro {
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    protected LocaleAwareMacro(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(AuthenticatedUserThreadLocal.getUser()));
    }

    public String getText(String key) {
        return this.getI18nBean().getText(key);
    }

    public String getText(String key, Object[] substitutions) {
        return this.getI18nBean().getText(key, substitutions);
    }

    public String getText(String key, List list) {
        return this.getI18nBean().getText(key, list);
    }

    public String getTextStrict(String key) {
        return this.getI18nBean().getTextStrict(key);
    }
}

