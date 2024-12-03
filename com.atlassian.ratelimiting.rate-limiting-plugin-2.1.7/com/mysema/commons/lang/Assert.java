/*
 * Decompiled with CFR 0.152.
 */
package com.mysema.commons.lang;

import java.util.Collection;
import java.util.Map;

public final class Assert {
    private static final String IS_FALSE = "is false";
    private static final String IS_TRUE = "is true";
    private static final String IS_EMPTY = "should not be empty";
    private static final String IS_NULL = "should not be null";
    private static final String HAS_NO_TEXT = "should have text";

    private Assert() {
    }

    public static String hasText(String str, String propOrMsg) {
        boolean hasText = false;
        int strLen = Assert.hasLength(str, propOrMsg).length();
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            hasText = true;
        }
        return Assert.assertThat(hasText, propOrMsg, HAS_NO_TEXT, str);
    }

    public static <T> T notNull(T object, String propOrMsg) {
        return Assert.assertThat(object != null, propOrMsg, IS_NULL, object);
    }

    public static String hasLength(String str, String propOrMsg) {
        return Assert.assertThat(str != null && str.length() > 0, propOrMsg, IS_EMPTY, str);
    }

    public static <T> T[] notEmpty(T[] objects, String propOrMsg) {
        return Assert.assertThat(objects != null && objects.length > 0, propOrMsg, IS_EMPTY, objects);
    }

    public static <M extends Map<?, ?>> M notEmpty(M map, String propOrMsg) {
        return Assert.assertThat(!map.isEmpty(), propOrMsg, IS_EMPTY, map);
    }

    public static <C extends Collection<?>> C notEmpty(C col, String propOrMsg) {
        return Assert.assertThat(!col.isEmpty(), propOrMsg, IS_EMPTY, col);
    }

    public static boolean isTrue(boolean condition, String propOrMsg) {
        return Assert.assertThat(condition, propOrMsg, IS_TRUE, condition);
    }

    public static boolean isFalse(boolean condition, String propOrMsg) {
        return Assert.assertThat(!condition, propOrMsg, IS_FALSE, condition);
    }

    public static <T> T assertThat(boolean condition, String propOrMsg, String msgSuffix, T rv) {
        if (!condition) {
            if (propOrMsg.contains(" ")) {
                throw new IllegalArgumentException(propOrMsg);
            }
            throw new IllegalArgumentException(propOrMsg + " " + msgSuffix);
        }
        return rv;
    }
}

