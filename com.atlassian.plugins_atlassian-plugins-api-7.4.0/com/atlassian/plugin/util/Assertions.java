/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

public class Assertions {
    public static <T> T notNull(String name, T notNull) {
        if (notNull == null) {
            throw new NullArgumentException(name);
        }
        return notNull;
    }

    public static void isTrue(String name, boolean check) {
        if (!check) {
            throw new IllegalArgumentException(name);
        }
    }

    private Assertions() {
    }

    static class NullArgumentException
    extends IllegalArgumentException {
        private static final long serialVersionUID = 6178592463723624585L;

        NullArgumentException(String name) {
            super(name + " should not be null!");
        }
    }
}

