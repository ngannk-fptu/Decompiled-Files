/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.i18n;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

public class AcceptHeaderLocaleResolver
implements LocaleResolver {
    private final List<Locale> supportedLocales = new ArrayList<Locale>(4);
    @Nullable
    private Locale defaultLocale;

    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = this.getDefaultLocale();
        if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
            return defaultLocale;
        }
        Locale requestLocale = request.getLocale();
        List<Locale> supportedLocales = this.getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return requestLocale;
        }
        Locale supportedLocale = this.findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return supportedLocale;
        }
        return defaultLocale != null ? defaultLocale : requestLocale;
    }

    @Nullable
    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        Enumeration requestLocales = request.getLocales();
        Locale languageMatch = null;
        block0: while (requestLocales.hasMoreElements()) {
            Locale locale = (Locale)requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch != null && !languageMatch.getLanguage().equals(locale.getLanguage())) continue;
                return locale;
            }
            if (languageMatch != null) continue;
            for (Locale candidate : supportedLocales) {
                if (StringUtils.hasLength((String)candidate.getCountry()) || !candidate.getLanguage().equals(locale.getLanguage())) continue;
                languageMatch = candidate;
                continue block0;
            }
        }
        return languageMatch;
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        throw new UnsupportedOperationException("Cannot change HTTP Accept-Language header - use a different locale resolution strategy");
    }
}

