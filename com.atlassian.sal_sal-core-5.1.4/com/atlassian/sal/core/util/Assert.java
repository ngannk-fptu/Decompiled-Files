/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.core.util;

public class Assert {
    public static <T> T notNull(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }
        return reference;
    }

    public static <T> T notNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }
}

