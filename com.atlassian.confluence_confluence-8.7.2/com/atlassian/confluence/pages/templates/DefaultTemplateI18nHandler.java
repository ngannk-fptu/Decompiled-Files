/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.templates.TemplateI18nHandler;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;

public class DefaultTemplateI18nHandler
implements TemplateI18nHandler {
    private final I18NBeanFactory i18nBeanFactory;
    private final LocaleManager localeManager;

    public DefaultTemplateI18nHandler(I18NBeanFactory i18nBeanFactory, LocaleManager localeManager) {
        this.i18nBeanFactory = i18nBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public String translate(Message message) {
        return this.getI18NBean().getText(message);
    }

    private I18NBean getI18NBean() {
        if (AuthenticatedUserThreadLocal.get() != null) {
            return this.i18nBeanFactory.getI18NBean(this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()));
        }
        return this.i18nBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
    }
}

