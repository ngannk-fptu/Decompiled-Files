/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

public final class Integers {
    private Integers() {
    }

    public static Integer parse(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

