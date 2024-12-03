/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

public class StringUtils {
    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        if ("".equals(str = str.trim())) {
            return null;
        }
        return str;
    }

    public static boolean isBlank(String str) {
        return str == null || "".equals(str) || "".equals(str.trim());
    }

    public static String notNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    public static String nl2br(String note) {
        if (note == null) {
            return null;
        }
        return note.replaceAll("\n", "<br />");
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }
}

