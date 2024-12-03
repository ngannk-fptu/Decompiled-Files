/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import java.util.Locale;

public class _CoreLocaleUtils {
    public static Locale getLessSpecificLocale(Locale locale) {
        String country = locale.getCountry();
        if (locale.getVariant().length() != 0) {
            String language = locale.getLanguage();
            return country != null ? new Locale(language, country) : new Locale(language);
        }
        if (country.length() != 0) {
            return new Locale(locale.getLanguage());
        }
        return null;
    }
}

