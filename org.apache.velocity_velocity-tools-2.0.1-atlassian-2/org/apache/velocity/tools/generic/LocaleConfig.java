/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.Locale;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

public class LocaleConfig
extends SafeConfig {
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private Locale locale = DEFAULT_LOCALE;

    @Override
    protected void configure(ValueParser values) {
        Locale locale = values.getLocale("locale");
        if (locale != null) {
            this.setLocale(locale);
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    protected void setLocale(Locale locale) {
        this.locale = locale;
    }
}

