/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.context.i18n.LocaleContext
 *  org.springframework.context.i18n.SimpleLocaleContext
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

public abstract class AbstractLocaleContextResolver
extends AbstractLocaleResolver
implements LocaleContextResolver {
    @Nullable
    private TimeZone defaultTimeZone;

    public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    @Nullable
    public TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = this.resolveLocaleContext(request).getLocale();
        return locale != null ? locale : request.getLocale();
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        this.setLocaleContext(request, response, (LocaleContext)(locale != null ? new SimpleLocaleContext(locale) : null));
    }
}

