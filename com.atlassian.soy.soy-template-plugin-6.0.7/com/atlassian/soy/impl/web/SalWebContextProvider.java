/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.soy.spi.web.WebContextProvider
 */
package com.atlassian.soy.impl.web;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.soy.spi.web.WebContextProvider;
import java.util.Locale;

public class SalWebContextProvider
implements WebContextProvider {
    private final LocaleResolver localeResolver;
    private final ApplicationProperties applicationProperties;

    public SalWebContextProvider(ApplicationProperties applicationProperties, LocaleResolver localeResolver) {
        this.applicationProperties = applicationProperties;
        this.localeResolver = localeResolver;
    }

    public String getContextPath() {
        return this.applicationProperties.getBaseUrl(UrlMode.RELATIVE);
    }

    public Locale getLocale() {
        return this.localeResolver.getLocale();
    }
}

