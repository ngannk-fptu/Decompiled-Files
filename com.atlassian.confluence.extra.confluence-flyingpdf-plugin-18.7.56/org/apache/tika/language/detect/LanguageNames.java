/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.detect;

import java.util.Locale;

public class LanguageNames {
    public static String makeName(String language, String script, String region) {
        Locale locale = new Locale.Builder().setLanguage(language).setScript(script).setRegion(region).build();
        return locale.toLanguageTag();
    }

    public static String normalizeName(String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);
        return locale.toLanguageTag();
    }

    public static boolean isMacroLanguage(String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);
        return false;
    }

    public static boolean hasMacroLanguage(String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);
        return false;
    }

    public static String getMacroLanguage(String languageTag) {
        return languageTag;
    }

    public static boolean equals(String languageTagA, String languageTagB) {
        Locale localeA = Locale.forLanguageTag(languageTagA);
        Locale localeB = Locale.forLanguageTag(languageTagB);
        return localeA.equals(localeB);
    }
}

