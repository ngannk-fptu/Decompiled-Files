/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public final class Validate {
    private static final String UNSPECIFIED_PARAM_NAME = "method parameter";

    private Validate() {
    }

    public static <T> T notNull(T t) {
        return Validate.notNull(t, null);
    }

    public static <T> T notNull(T t, String string) {
        if (t == null) {
            throw new IllegalArgumentException(String.format("%s may not be null", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return t;
    }

    public static <T extends CharSequence> T notEmpty(T t) {
        return Validate.notEmpty(t, null);
    }

    public static <T extends CharSequence> T notEmpty(T t, String string) {
        if (t == null || t.length() == 0 || Validate.isOnlyWhiteSpace(t)) {
            throw new IllegalArgumentException(String.format("%s may not be blank", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return t;
    }

    private static <T extends CharSequence> boolean isOnlyWhiteSpace(T t) {
        for (int i = 0; i < t.length(); ++i) {
            if (Character.isWhitespace(t.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static <T> T[] notEmpty(T[] TArray) {
        return Validate.notEmpty(TArray, null);
    }

    public static <T> T[] notEmpty(T[] TArray, String string) {
        if (TArray == null || TArray.length == 0) {
            throw new IllegalArgumentException(String.format("%s may not be empty", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return TArray;
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection) {
        return Validate.notEmpty(collection, null);
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection, String string) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s may not be empty", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return collection;
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map) {
        return Validate.notEmpty(map, null);
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String string) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s may not be empty", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return map;
    }

    public static <T> T[] noNullElements(T[] TArray) {
        return Validate.noNullElements(TArray, null);
    }

    public static <T> T[] noNullElements(T[] TArray, String string) {
        Validate.noNullElements(TArray == null ? null : Arrays.asList(TArray), string);
        return TArray;
    }

    public static <T> Collection<T> noNullElements(Collection<T> collection) {
        return Validate.noNullElements(collection, null);
    }

    public static <T> Collection<T> noNullElements(Collection<T> collection, String string) {
        Validate.notNull(collection, string);
        for (T t : collection) {
            if (t != null) continue;
            throw new IllegalArgumentException(String.format("%s may not contain null elements", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return collection;
    }

    public static <K, V> Map<K, V> noNullValues(Map<K, V> map) {
        return Validate.noNullValues(map, null);
    }

    public static <K, V> Map<K, V> noNullValues(Map<K, V> map, String string) {
        Validate.notNull(map, string);
        for (V v : map.values()) {
            if (v != null) continue;
            throw new IllegalArgumentException(String.format("%s may not contain null values", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return map;
    }

    public static <K, V> Map<K, V> noNullKeys(Map<K, V> map) {
        return Validate.noNullKeys(map, null);
    }

    public static <K, V> Map<K, V> noNullKeys(Map<K, V> map, String string) {
        Validate.notNull(map, string);
        for (K k : map.keySet()) {
            if (k != null) continue;
            throw new IllegalArgumentException(String.format("%s may not contain null keys", string == null ? UNSPECIFIED_PARAM_NAME : string));
        }
        return map;
    }

    public static boolean isTrue(boolean bl, String string) {
        return Validate.isTrue(bl, bl, string);
    }

    public static <T> T isTrue(boolean bl, T t, String string) {
        if (!bl) {
            throw new IllegalArgumentException(String.format(string == null ? "expression may not be %s" : string, t));
        }
        return t;
    }
}

