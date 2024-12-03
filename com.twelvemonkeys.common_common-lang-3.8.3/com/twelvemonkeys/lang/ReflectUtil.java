/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

public final class ReflectUtil {
    private ReflectUtil() {
    }

    public static Class unwrapType(Class clazz) {
        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz == Byte.class) {
            return Byte.TYPE;
        }
        if (clazz == Character.class) {
            return Character.TYPE;
        }
        if (clazz == Double.class) {
            return Double.TYPE;
        }
        if (clazz == Float.class) {
            return Float.TYPE;
        }
        if (clazz == Integer.class) {
            return Integer.TYPE;
        }
        if (clazz == Long.class) {
            return Long.TYPE;
        }
        if (clazz == Short.class) {
            return Short.TYPE;
        }
        throw new IllegalArgumentException("Not a primitive wrapper: " + clazz);
    }

    public static Class wrapType(Class clazz) {
        if (clazz == Boolean.TYPE) {
            return Boolean.class;
        }
        if (clazz == Byte.TYPE) {
            return Byte.class;
        }
        if (clazz == Character.TYPE) {
            return Character.class;
        }
        if (clazz == Double.TYPE) {
            return Double.class;
        }
        if (clazz == Float.TYPE) {
            return Float.class;
        }
        if (clazz == Integer.TYPE) {
            return Integer.class;
        }
        if (clazz == Long.TYPE) {
            return Long.class;
        }
        if (clazz == Short.TYPE) {
            return Short.class;
        }
        throw new IllegalArgumentException("Not a primitive type: " + clazz);
    }

    public static boolean isPrimitiveWrapper(Class clazz) {
        return clazz == Boolean.class || clazz == Byte.class || clazz == Character.class || clazz == Double.class || clazz == Float.class || clazz == Integer.class || clazz == Long.class || clazz == Short.class;
    }
}

