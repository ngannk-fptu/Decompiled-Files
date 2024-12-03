/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event;

import com.atlassian.confluence.extra.calendar3.rest.validators.event.EventValidator;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractEventValidator
implements EventValidator {
    protected final LocaleManager localeManager;
    protected final I18NBeanFactory i18NBeanFactory;

    protected AbstractEventValidator(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    protected void addFieldError(Map<String, List<String>> fieldErrors, String field, String msg) {
        List<String> errorMessages;
        if (fieldErrors.containsKey(field)) {
            errorMessages = fieldErrors.get(field);
        } else {
            errorMessages = new ArrayList<String>();
            fieldErrors.put(field, errorMessages);
        }
        msg = GeneralUtil.htmlEncode((String)msg);
        if (!errorMessages.contains(msg)) {
            errorMessages.add(msg);
        }
    }

    protected String getText(String s) {
        return this.getI18nBean().getText(s);
    }

    protected String getText(String i18nKey, List substitutions) {
        return this.getI18nBean().getText(i18nKey, substitutions);
    }

    protected I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.getUserLocale());
    }

    protected Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }
}

