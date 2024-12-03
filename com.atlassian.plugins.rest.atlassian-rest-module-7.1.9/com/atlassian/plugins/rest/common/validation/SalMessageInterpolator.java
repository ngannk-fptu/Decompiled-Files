/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.validation.MessageInterpolator
 *  javax.validation.MessageInterpolator$Context
 */
package com.atlassian.plugins.rest.common.validation;

import com.atlassian.sal.api.message.I18nResolver;
import java.util.Locale;
import javax.validation.MessageInterpolator;

public class SalMessageInterpolator
implements MessageInterpolator {
    private final I18nResolver i18nResolver;

    public SalMessageInterpolator(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public String interpolate(String s, MessageInterpolator.Context context) {
        String message = this.i18nResolver.getText(s);
        message = message != null ? message : s;
        return message;
    }

    public String interpolate(String s, MessageInterpolator.Context context, Locale locale) {
        return this.interpolate(s, context);
    }
}

