/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

public final class Numbers {
    private Numbers() {
    }

    public static int parseInt(String value) {
        if (value != null && value.charAt(0) == '+') {
            return Integer.parseInt(value.substring(1));
        }
        return Integer.parseInt(value);
    }
}

