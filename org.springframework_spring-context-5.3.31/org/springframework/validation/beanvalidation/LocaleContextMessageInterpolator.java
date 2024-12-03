/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.MessageInterpolator
 *  javax.validation.MessageInterpolator$Context
 *  org.springframework.util.Assert
 */
package org.springframework.validation.beanvalidation;

import java.util.Locale;
import javax.validation.MessageInterpolator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

public class LocaleContextMessageInterpolator
implements MessageInterpolator {
    private final MessageInterpolator targetInterpolator;

    public LocaleContextMessageInterpolator(MessageInterpolator targetInterpolator) {
        Assert.notNull((Object)targetInterpolator, (String)"Target MessageInterpolator must not be null");
        this.targetInterpolator = targetInterpolator;
    }

    public String interpolate(String message, MessageInterpolator.Context context) {
        return this.targetInterpolator.interpolate(message, context, LocaleContextHolder.getLocale());
    }

    public String interpolate(String message, MessageInterpolator.Context context, Locale locale) {
        return this.targetInterpolator.interpolate(message, context, locale);
    }
}

