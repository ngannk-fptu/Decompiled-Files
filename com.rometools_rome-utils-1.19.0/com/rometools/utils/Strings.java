/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

import java.util.Locale;

public final class Strings {
    private Strings() {
    }

    public static boolean isNull(String s) {
        return s == null;
    }

    public static boolean isEmpty(String s) {
        return Strings.isNull(s) || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return !Strings.isEmpty(s);
    }

    public static boolean isBlank(String s) {
        return Strings.isEmpty(s) || s.trim().isEmpty();
    }

    public static String trim(String s) {
        if (s == null) {
            return null;
        }
        return s.trim();
    }

    public static String trimToNull(String s) {
        String trimmed = Strings.trim(s);
        if (trimmed == null || trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    public static String trimToEmpty(String s) {
        String trimmed = Strings.trim(s);
        if (trimmed == null || trimmed.isEmpty()) {
            return "";
        }
        return trimmed;
    }

    public static String toLowerCase(String s) {
        if (s == null) {
            return null;
        }
        return s.toLowerCase(Locale.ENGLISH);
    }
}

