/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

public class AcceptHeaderLocaleContextResolver
implements LocaleContextResolver {
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
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange2) {
        List<Locale> requestLocales = null;
        try {
            requestLocales = exchange2.getRequest().getHeaders().getAcceptLanguageAsLocales();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return new SimpleLocaleContext(this.resolveSupportedLocale(requestLocales));
    }

    @Nullable
    private Locale resolveSupportedLocale(@Nullable List<Locale> requestLocales) {
        if (CollectionUtils.isEmpty(requestLocales)) {
            return this.getDefaultLocale();
        }
        List<Locale> supportedLocales = this.getSupportedLocales();
        if (supportedLocales.isEmpty()) {
            return requestLocales.get(0);
        }
        Locale languageMatch = null;
        block0: for (Locale locale : requestLocales) {
            if (supportedLocales.contains(locale)) {
                if (languageMatch != null && !languageMatch.getLanguage().equals(locale.getLanguage())) continue;
                return locale;
            }
            if (languageMatch != null) continue;
            for (Locale candidate : supportedLocales) {
                if (StringUtils.hasLength(candidate.getCountry()) || !candidate.getLanguage().equals(locale.getLanguage())) continue;
                languageMatch = candidate;
                continue block0;
            }
        }
        if (languageMatch != null) {
            return languageMatch;
        }
        Locale defaultLocale = this.getDefaultLocale();
        return defaultLocale != null ? defaultLocale : requestLocales.get(0);
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange2, @Nullable LocaleContext locale) {
        throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale context resolution strategy");
    }
}

