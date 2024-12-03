/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.context.support.AbstractMessageSource
 */
package com.atlassian.plugin.web.springmvc.message;

import com.atlassian.sal.api.message.I18nResolver;
import java.text.MessageFormat;
import java.util.Locale;
import org.springframework.context.support.AbstractMessageSource;

public final class ApplicationMessageSource
extends AbstractMessageSource {
    private String keyPrefix = "";
    private I18nResolver i18nResolver;

    protected MessageFormat resolveCode(String code, Locale locale) {
        return new MessageFormat(this.i18nResolver.getText(this.keyPrefix + code));
    }

    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return this.i18nResolver.getText(this.keyPrefix + code);
    }

    public void setKeyPrefix(String propertyPrefix) {
        this.keyPrefix = propertyPrefix;
    }

    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }
}

