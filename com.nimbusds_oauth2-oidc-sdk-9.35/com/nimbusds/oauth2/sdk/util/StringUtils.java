/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

public final class StringUtils {
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (Character.isWhitespace(cs.charAt(i))) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    public static boolean isAlpha(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (Character.isAlphabetic(cs.charAt(i))) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (Character.isDigit(cs.charAt(i))) continue;
                return false;
            }
        }
        return true;
    }

    private StringUtils() {
    }
}

