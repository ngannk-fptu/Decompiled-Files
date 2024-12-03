/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Assert {
    private static void fail(String message) {
        throw new IllegalArgumentException(message);
    }

    public static <T> T notNull(T object, String message) {
        if (object == null) {
            Assert.fail(message);
        }
        return object;
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            Assert.fail(message);
        }
    }

    public static <T> T isInstanceOf(Class<T> clazz, Object object) {
        if (!clazz.isInstance(object)) {
            Assert.fail(object + " must be an instance of " + clazz);
        }
        return clazz.cast(object);
    }

    public static <T> T isInstanceOf(Class<T> clazz, Object object, String message) {
        if (!clazz.isInstance(object)) {
            Assert.fail(message);
        }
        return clazz.cast(object);
    }
}

