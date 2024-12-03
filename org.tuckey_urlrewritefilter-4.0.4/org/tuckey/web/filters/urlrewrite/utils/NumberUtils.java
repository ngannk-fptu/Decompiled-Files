/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

public class NumberUtils {
    public static int stringToInt(String str) {
        return NumberUtils.stringToInt(str, 0);
    }

    public static int stringToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static long stringToLong(String str) {
        return NumberUtils.stringToLong(str, 0L);
    }

    public static long stringToLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        }
        catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}

