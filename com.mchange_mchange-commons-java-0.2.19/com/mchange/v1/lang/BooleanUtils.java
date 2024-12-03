/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang;

public final class BooleanUtils {
    public static boolean parseBoolean(String string) throws IllegalArgumentException {
        if (string.equals("true")) {
            return true;
        }
        if (string.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException("\"str\" is neither \"true\" nor \"false\".");
    }

    private BooleanUtils() {
    }
}

