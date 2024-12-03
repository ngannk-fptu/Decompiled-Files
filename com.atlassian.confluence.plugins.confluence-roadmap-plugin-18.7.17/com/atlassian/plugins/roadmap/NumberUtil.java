/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap;

public final class NumberUtil {
    public static long parseLongString(String longString) {
        try {
            return Long.parseLong(longString);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Not a valid content ID: " + longString);
        }
    }
}

