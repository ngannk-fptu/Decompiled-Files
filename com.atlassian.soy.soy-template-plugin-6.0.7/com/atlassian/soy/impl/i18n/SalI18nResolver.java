/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.soy.spi.i18n.I18nResolver
 */
package com.atlassian.soy.impl.i18n;

import com.atlassian.soy.spi.i18n.I18nResolver;
import java.io.Serializable;
import java.util.Locale;

public class SalI18nResolver
implements I18nResolver {
    private final com.atlassian.sal.api.message.I18nResolver delegate;

    public SalI18nResolver(com.atlassian.sal.api.message.I18nResolver delegate) {
        this.delegate = delegate;
    }

    public String getText(String key) {
        return this.delegate.getText(key);
    }

    public String getText(String key, Serializable ... serializables) {
        return this.delegate.getText(key, serializables);
    }

    public String getText(Locale locale, String key) {
        return this.delegate.getText(locale, key);
    }

    public String getRawText(Locale locale, String key) {
        return this.delegate.getRawText(locale, key);
    }
}

