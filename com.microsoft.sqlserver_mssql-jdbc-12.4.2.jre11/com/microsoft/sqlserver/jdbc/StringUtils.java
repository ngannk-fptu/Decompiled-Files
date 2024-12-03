/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

public class StringUtils {
    public static final String SPACE = " ";
    public static final String EMPTY = "";

    private StringUtils() {
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean isNumeric(String str) {
        return !StringUtils.isEmpty(str) && str.matches("\\d+(\\.\\d+)?");
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
    }
}

