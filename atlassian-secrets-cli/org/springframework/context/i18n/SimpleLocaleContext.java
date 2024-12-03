/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.i18n;

import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;

public class SimpleLocaleContext
implements LocaleContext {
    @Nullable
    private final Locale locale;

    public SimpleLocaleContext(@Nullable Locale locale) {
        this.locale = locale;
    }

    @Override
    @Nullable
    public Locale getLocale() {
        return this.locale;
    }

    public String toString() {
        return this.locale != null ? this.locale.toString() : "-";
    }
}

