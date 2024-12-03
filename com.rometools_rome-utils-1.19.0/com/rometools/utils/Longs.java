/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

public final class Longs {
    private Longs() {
    }

    public static Long parseDecimal(String s) {
        Long parsed = null;
        try {
            if (s != null) {
                parsed = (long)Double.parseDouble(s);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return parsed;
    }
}

