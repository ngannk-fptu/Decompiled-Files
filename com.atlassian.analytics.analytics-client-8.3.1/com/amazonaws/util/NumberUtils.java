/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

public final class NumberUtils {
    private NumberUtils() {
    }

    public static Integer tryParseInt(String toParse) {
        try {
            return Integer.parseInt(toParse);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

