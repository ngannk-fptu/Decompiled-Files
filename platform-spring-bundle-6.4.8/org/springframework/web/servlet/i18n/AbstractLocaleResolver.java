/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.i18n;

import java.util.Locale;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

public abstract class AbstractLocaleResolver
implements LocaleResolver {
    @Nullable
    private Locale defaultLocale;

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }
}

