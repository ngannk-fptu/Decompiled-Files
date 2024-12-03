/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.Locale;

public class LocaleUtility {
    public static final char IETF_SEPARATOR = '-';
    public static final String EMPTY_STRING = "";

    public static Locale langToLocale(String lang) {
        if (lang == null || lang.equals(EMPTY_STRING)) {
            return Locale.getDefault();
        }
        String language = EMPTY_STRING;
        String country = EMPTY_STRING;
        String variant = EMPTY_STRING;
        int i1 = lang.indexOf(45);
        if (i1 < 0) {
            language = lang;
        } else {
            int i2;
            language = lang.substring(0, i1);
            if ((i2 = lang.indexOf(45, ++i1)) < 0) {
                country = lang.substring(i1);
            } else {
                country = lang.substring(i1, i2);
                variant = lang.substring(i2 + 1);
            }
        }
        language = language.length() == 2 ? language.toLowerCase() : EMPTY_STRING;
        country = country.length() == 2 ? country.toUpperCase() : EMPTY_STRING;
        variant = variant.length() > 0 && (language.length() == 2 || country.length() == 2) ? variant.toUpperCase() : EMPTY_STRING;
        return new Locale(language, country, variant);
    }
}

