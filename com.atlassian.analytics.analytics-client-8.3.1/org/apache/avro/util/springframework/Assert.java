/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util.springframework;

import org.apache.avro.reflect.Nullable;

class Assert {
    private Assert() {
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}

