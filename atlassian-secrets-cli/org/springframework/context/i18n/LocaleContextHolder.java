/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.i18n;

import java.util.Locale;
import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

public abstract class LocaleContextHolder {
    private static final ThreadLocal<LocaleContext> localeContextHolder = new NamedThreadLocal<LocaleContext>("LocaleContext");
    private static final ThreadLocal<LocaleContext> inheritableLocaleContextHolder = new NamedInheritableThreadLocal<LocaleContext>("LocaleContext");
    @Nullable
    private static Locale defaultLocale;
    @Nullable
    private static TimeZone defaultTimeZone;

    public static void resetLocaleContext() {
        localeContextHolder.remove();
        inheritableLocaleContextHolder.remove();
    }

    public static void setLocaleContext(@Nullable LocaleContext localeContext) {
        LocaleContextHolder.setLocaleContext(localeContext, false);
    }

    public static void setLocaleContext(@Nullable LocaleContext localeContext, boolean inheritable) {
        if (localeContext == null) {
            LocaleContextHolder.resetLocaleContext();
        } else if (inheritable) {
            inheritableLocaleContextHolder.set(localeContext);
            localeContextHolder.remove();
        } else {
            localeContextHolder.set(localeContext);
            inheritableLocaleContextHolder.remove();
        }
    }

    @Nullable
    public static LocaleContext getLocaleContext() {
        LocaleContext localeContext = localeContextHolder.get();
        if (localeContext == null) {
            localeContext = inheritableLocaleContextHolder.get();
        }
        return localeContext;
    }

    public static void setLocale(@Nullable Locale locale) {
        LocaleContextHolder.setLocale(locale, false);
    }

    public static void setLocale(@Nullable Locale locale, boolean inheritable) {
        TimeZone timeZone;
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        TimeZone timeZone2 = timeZone = localeContext instanceof TimeZoneAwareLocaleContext ? ((TimeZoneAwareLocaleContext)localeContext).getTimeZone() : null;
        localeContext = timeZone != null ? new SimpleTimeZoneAwareLocaleContext(locale, timeZone) : (locale != null ? new SimpleLocaleContext(locale) : null);
        LocaleContextHolder.setLocaleContext(localeContext, inheritable);
    }

    public static void setDefaultLocale(@Nullable Locale locale) {
        defaultLocale = locale;
    }

    public static Locale getLocale() {
        return LocaleContextHolder.getLocale(LocaleContextHolder.getLocaleContext());
    }

    public static Locale getLocale(@Nullable LocaleContext localeContext) {
        Locale locale;
        if (localeContext != null && (locale = localeContext.getLocale()) != null) {
            return locale;
        }
        return defaultLocale != null ? defaultLocale : Locale.getDefault();
    }

    public static void setTimeZone(@Nullable TimeZone timeZone) {
        LocaleContextHolder.setTimeZone(timeZone, false);
    }

    public static void setTimeZone(@Nullable TimeZone timeZone, boolean inheritable) {
        Locale locale;
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale2 = locale = localeContext != null ? localeContext.getLocale() : null;
        localeContext = timeZone != null ? new SimpleTimeZoneAwareLocaleContext(locale, timeZone) : (locale != null ? new SimpleLocaleContext(locale) : null);
        LocaleContextHolder.setLocaleContext(localeContext, inheritable);
    }

    public static void setDefaultTimeZone(@Nullable TimeZone timeZone) {
        defaultTimeZone = timeZone;
    }

    public static TimeZone getTimeZone() {
        return LocaleContextHolder.getTimeZone(LocaleContextHolder.getLocaleContext());
    }

    public static TimeZone getTimeZone(@Nullable LocaleContext localeContext) {
        TimeZone timeZone;
        if (localeContext instanceof TimeZoneAwareLocaleContext && (timeZone = ((TimeZoneAwareLocaleContext)localeContext).getTimeZone()) != null) {
            return timeZone;
        }
        return defaultTimeZone != null ? defaultTimeZone : TimeZone.getDefault();
    }
}

