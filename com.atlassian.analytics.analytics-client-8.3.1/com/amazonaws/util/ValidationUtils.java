/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import java.util.Collection;

public class ValidationUtils {
    public static <T> T assertNotNull(T object, String fieldName) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null", fieldName));
        }
        return object;
    }

    public static void assertAllAreNull(String messageIfNull, Object ... objects) throws IllegalArgumentException {
        for (Object object : objects) {
            if (object == null) continue;
            throw new IllegalArgumentException(messageIfNull);
        }
    }

    public static int assertIsPositive(int num, String fieldName) {
        if (num <= 0) {
            throw new IllegalArgumentException(String.format("%s must be positive", fieldName));
        }
        return num;
    }

    public static <T extends Collection<?>> T assertNotEmpty(T collection, String fieldName) throws IllegalArgumentException {
        ValidationUtils.assertNotNull(collection, fieldName);
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be empty", fieldName));
        }
        return collection;
    }

    public static <T> T[] assertNotEmpty(T[] array, String fieldName) throws IllegalArgumentException {
        ValidationUtils.assertNotNull(array, fieldName);
        if (array.length == 0) {
            throw new IllegalArgumentException(String.format("%s cannot be empty", fieldName));
        }
        return array;
    }

    public static String assertStringNotEmpty(String string, String fieldName) throws IllegalArgumentException {
        ValidationUtils.assertNotNull(string, fieldName);
        if (string.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be empty", fieldName));
        }
        return string;
    }
}

