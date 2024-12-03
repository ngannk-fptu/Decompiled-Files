/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

public final class Alternatives {
    private Alternatives() {
    }

    public static <T> T firstNotNull(T ... objects) {
        for (T object : objects) {
            if (object == null) continue;
            return object;
        }
        return null;
    }
}

