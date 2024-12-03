/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.johnson.util;

import javax.annotation.Nullable;

public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException(this.getClass().getName() + " should not be instantiated");
    }

    public static boolean isBlank(@Nullable String inString) {
        return inString == null || inString.trim().length() == 0;
    }

    public static boolean isEmpty(@Nullable String inString) {
        return inString == null || inString.length() == 0;
    }

    public static String defaultIfEmpty(@Nullable String value, @Nullable String defaultValue) {
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }
}

