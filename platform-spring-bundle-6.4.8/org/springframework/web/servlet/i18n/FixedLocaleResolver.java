/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;

public class FixedLocaleResolver
extends AbstractLocaleContextResolver {
    public FixedLocaleResolver() {
        this.setDefaultLocale(Locale.getDefault());
    }

    public FixedLocaleResolver(Locale locale) {
        this.setDefaultLocale(locale);
    }

    public FixedLocaleResolver(Locale locale, TimeZone timeZone) {
        this.setDefaultLocale(locale);
        this.setDefaultTimeZone(timeZone);
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = this.getDefaultLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        return new TimeZoneAwareLocaleContext(){

            @Override
            @Nullable
            public Locale getLocale() {
                return FixedLocaleResolver.this.getDefaultLocale();
            }

            @Override
            public TimeZone getTimeZone() {
                return FixedLocaleResolver.this.getDefaultTimeZone();
            }
        };
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        throw new UnsupportedOperationException("Cannot change fixed locale - use a different locale resolution strategy");
    }
}

