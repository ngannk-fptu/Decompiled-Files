/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.context.i18n.LocaleContext
 *  org.springframework.context.i18n.TimeZoneAwareLocaleContext
 *  org.springframework.lang.Nullable
 *  org.springframework.web.util.WebUtils
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
import org.springframework.web.util.WebUtils;

public class SessionLocaleResolver
extends AbstractLocaleContextResolver {
    public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";
    public static final String TIME_ZONE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".TIME_ZONE";
    private String localeAttributeName = LOCALE_SESSION_ATTRIBUTE_NAME;
    private String timeZoneAttributeName = TIME_ZONE_SESSION_ATTRIBUTE_NAME;

    public void setLocaleAttributeName(String localeAttributeName) {
        this.localeAttributeName = localeAttributeName;
    }

    public void setTimeZoneAttributeName(String timeZoneAttributeName) {
        this.timeZoneAttributeName = timeZoneAttributeName;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale)WebUtils.getSessionAttribute((HttpServletRequest)request, (String)this.localeAttributeName);
        if (locale == null) {
            locale = this.determineDefaultLocale(request);
        }
        return locale;
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        return new TimeZoneAwareLocaleContext(){

            public Locale getLocale() {
                Locale locale = (Locale)WebUtils.getSessionAttribute((HttpServletRequest)request, (String)SessionLocaleResolver.this.localeAttributeName);
                if (locale == null) {
                    locale = SessionLocaleResolver.this.determineDefaultLocale(request);
                }
                return locale;
            }

            @Nullable
            public TimeZone getTimeZone() {
                TimeZone timeZone = (TimeZone)WebUtils.getSessionAttribute((HttpServletRequest)request, (String)SessionLocaleResolver.this.timeZoneAttributeName);
                if (timeZone == null) {
                    timeZone = SessionLocaleResolver.this.determineDefaultTimeZone(request);
                }
                return timeZone;
            }
        };
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        Locale locale = null;
        TimeZone timeZone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext)localeContext).getTimeZone();
            }
        }
        WebUtils.setSessionAttribute((HttpServletRequest)request, (String)this.localeAttributeName, (Object)locale);
        WebUtils.setSessionAttribute((HttpServletRequest)request, (String)this.timeZoneAttributeName, (Object)timeZone);
    }

    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = this.getDefaultLocale();
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
        return defaultLocale;
    }

    @Nullable
    protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
        return this.getDefaultTimeZone();
    }
}

