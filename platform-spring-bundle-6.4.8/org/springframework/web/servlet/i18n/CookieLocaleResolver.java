/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

public class CookieLocaleResolver
extends CookieGenerator
implements LocaleContextResolver {
    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";
    public static final String TIME_ZONE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".TIME_ZONE";
    public static final String DEFAULT_COOKIE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";
    private boolean languageTagCompliant = true;
    private boolean rejectInvalidCookies = true;
    @Nullable
    private Locale defaultLocale;
    @Nullable
    private TimeZone defaultTimeZone;

    public CookieLocaleResolver() {
        this.setCookieName(DEFAULT_COOKIE_NAME);
    }

    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        this.languageTagCompliant = languageTagCompliant;
    }

    public boolean isLanguageTagCompliant() {
        return this.languageTagCompliant;
    }

    public void setRejectInvalidCookies(boolean rejectInvalidCookies) {
        this.rejectInvalidCookies = rejectInvalidCookies;
    }

    public boolean isRejectInvalidCookies() {
        return this.rejectInvalidCookies;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    @Nullable
    protected TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        this.parseLocaleCookieIfNecessary(request);
        return (Locale)request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        this.parseLocaleCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext(){

            @Override
            @Nullable
            public Locale getLocale() {
                return (Locale)request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override
            @Nullable
            public TimeZone getTimeZone() {
                return (TimeZone)request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Cookie cookie;
            Locale locale = null;
            TimeZone timeZone = null;
            String cookieName = this.getCookieName();
            if (cookieName != null && (cookie = WebUtils.getCookie(request, cookieName)) != null) {
                block9: {
                    String value;
                    String localePart = value = cookie.getValue();
                    String timeZonePart = null;
                    int separatorIndex = localePart.indexOf(47);
                    if (separatorIndex == -1) {
                        separatorIndex = localePart.indexOf(32);
                    }
                    if (separatorIndex >= 0) {
                        localePart = value.substring(0, separatorIndex);
                        timeZonePart = value.substring(separatorIndex + 1);
                    }
                    try {
                        Locale locale2 = locale = !"-".equals(localePart) ? this.parseLocaleValue(localePart) : null;
                        if (timeZonePart != null) {
                            timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                        }
                    }
                    catch (IllegalArgumentException ex) {
                        if (this.isRejectInvalidCookies() && request.getAttribute("javax.servlet.error.exception") == null) {
                            throw new IllegalStateException("Encountered invalid locale cookie '" + cookieName + "': [" + value + "] due to: " + ex.getMessage());
                        }
                        if (!this.logger.isDebugEnabled()) break block9;
                        this.logger.debug((Object)("Ignoring invalid locale cookie '" + cookieName + "': [" + value + "] due to: " + ex.getMessage()));
                    }
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale + "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : "")));
                }
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale != null ? locale : this.determineDefaultLocale(request));
            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone != null ? timeZone : this.determineDefaultTimeZone(request));
        }
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        this.setLocaleContext(request, response, locale != null ? new SimpleLocaleContext(locale) : null);
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        Assert.notNull((Object)response, "HttpServletResponse is required for CookieLocaleResolver");
        Locale locale = null;
        TimeZone timeZone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext)localeContext).getTimeZone();
            }
            this.addCookie(response, (locale != null ? this.toLocaleValue(locale) : "-") + (timeZone != null ? '/' + timeZone.getID() : ""));
        } else {
            this.removeCookie(response);
        }
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, (Object)(locale != null ? locale : this.determineDefaultLocale(request)));
        request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, (Object)(timeZone != null ? timeZone : this.determineDefaultTimeZone(request)));
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }

    protected String toLocaleValue(Locale locale) {
        return this.isLanguageTagCompliant() ? locale.toLanguageTag() : locale.toString();
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

