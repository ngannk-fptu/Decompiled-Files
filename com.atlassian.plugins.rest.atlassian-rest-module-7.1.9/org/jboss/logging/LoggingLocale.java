/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

class LoggingLocale {
    private static final Locale LOCALE = LoggingLocale.getDefaultLocale();

    LoggingLocale() {
    }

    static Locale getLocale() {
        return LOCALE;
    }

    private static Locale getDefaultLocale() {
        String bcp47Tag = AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                return System.getProperty("org.jboss.logging.locale", "");
            }
        });
        if (bcp47Tag.trim().isEmpty()) {
            return Locale.getDefault();
        }
        return LoggingLocale.forLanguageTag(bcp47Tag);
    }

    private static Locale forLanguageTag(String locale) {
        if ("en-CA".equalsIgnoreCase(locale)) {
            return Locale.CANADA;
        }
        if ("fr-CA".equalsIgnoreCase(locale)) {
            return Locale.CANADA_FRENCH;
        }
        if ("zh".equalsIgnoreCase(locale)) {
            return Locale.CHINESE;
        }
        if ("en".equalsIgnoreCase(locale)) {
            return Locale.ENGLISH;
        }
        if ("fr-FR".equalsIgnoreCase(locale)) {
            return Locale.FRANCE;
        }
        if ("fr".equalsIgnoreCase(locale)) {
            return Locale.FRENCH;
        }
        if ("de".equalsIgnoreCase(locale)) {
            return Locale.GERMAN;
        }
        if ("de-DE".equalsIgnoreCase(locale)) {
            return Locale.GERMANY;
        }
        if ("it".equalsIgnoreCase(locale)) {
            return Locale.ITALIAN;
        }
        if ("it-IT".equalsIgnoreCase(locale)) {
            return Locale.ITALY;
        }
        if ("ja-JP".equalsIgnoreCase(locale)) {
            return Locale.JAPAN;
        }
        if ("ja".equalsIgnoreCase(locale)) {
            return Locale.JAPANESE;
        }
        if ("ko-KR".equalsIgnoreCase(locale)) {
            return Locale.KOREA;
        }
        if ("ko".equalsIgnoreCase(locale)) {
            return Locale.KOREAN;
        }
        if ("zh-CN".equalsIgnoreCase(locale)) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        if ("zh-TW".equalsIgnoreCase(locale)) {
            return Locale.TRADITIONAL_CHINESE;
        }
        if ("en-UK".equalsIgnoreCase(locale)) {
            return Locale.UK;
        }
        if ("en-US".equalsIgnoreCase(locale)) {
            return Locale.US;
        }
        String[] parts = locale.split("-");
        int len = parts.length;
        int index = 0;
        int count = 0;
        String language = parts[index++];
        String region = "";
        String variant = "";
        while (index < len && count++ != 2 && LoggingLocale.isAlpha(parts[index], 3, 3)) {
            ++index;
        }
        if (index != len && LoggingLocale.isAlpha(parts[index], 4, 4)) {
            ++index;
        }
        if (index != len && (LoggingLocale.isAlpha(parts[index], 2, 2) || LoggingLocale.isNumeric(parts[index], 3, 3))) {
            region = parts[index++];
        }
        if (index != len && LoggingLocale.isAlphaOrNumeric(parts[index], 5, 8)) {
            variant = parts[index];
        }
        return new Locale(language, region, variant);
    }

    private static boolean isAlpha(String value, int minLen, int maxLen) {
        int len = value.length();
        if (len < minLen || len > maxLen) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (Character.isLetter(value.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean isNumeric(String value, int minLen, int maxLen) {
        int len = value.length();
        if (len < minLen || len > maxLen) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (Character.isDigit(value.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean isAlphaOrNumeric(String value, int minLen, int maxLen) {
        int len = value.length();
        if (len < minLen || len > maxLen) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (Character.isLetterOrDigit(value.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

