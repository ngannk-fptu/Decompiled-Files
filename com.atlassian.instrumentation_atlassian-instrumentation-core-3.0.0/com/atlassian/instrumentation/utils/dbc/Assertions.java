/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.utils.dbc;

public class Assertions {
    public static <T> T notNull(String name, T obj) {
        if (obj == null) {
            throw new IllegalArgumentException(String.valueOf(name) + " must not be null");
        }
        return obj;
    }

    public static double notNegative(String name, double value) {
        if (value < 0.0) {
            throw new IllegalArgumentException(String.valueOf(name) + " must be >= 0");
        }
        return value;
    }
}

