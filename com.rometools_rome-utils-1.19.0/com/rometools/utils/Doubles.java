/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

public class Doubles {
    private Doubles() {
    }

    public static Double parse(String s) {
        Double parsed = null;
        try {
            if (s != null) {
                parsed = Double.parseDouble(s);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return parsed;
    }
}

