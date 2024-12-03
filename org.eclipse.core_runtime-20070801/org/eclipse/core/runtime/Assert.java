/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.core.runtime;

import org.eclipse.core.runtime.AssertionFailedException;

public final class Assert {
    private Assert() {
    }

    public static boolean isLegal(boolean expression) {
        return Assert.isLegal(expression, "");
    }

    public static boolean isLegal(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
        return expression;
    }

    public static void isNotNull(Object object) {
        Assert.isNotNull(object, "");
    }

    public static void isNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionFailedException("null argument:" + message);
        }
    }

    public static boolean isTrue(boolean expression) {
        return Assert.isTrue(expression, "");
    }

    public static boolean isTrue(boolean expression, String message) {
        if (!expression) {
            throw new AssertionFailedException("assertion failed: " + message);
        }
        return expression;
    }
}

