/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public final class StringUtils {
    private static final String EMPTY_STRING = "";

    private StringUtils() {
    }

    public static String join(String[] array) {
        if (array == null) {
            return EMPTY_STRING;
        }
        return StringUtils.join(Arrays.asList(array));
    }

    public static void join(String[] array, char separator, StringBuilder sb) {
        if (array == null) {
            return;
        }
        StringUtils.join(Arrays.asList(array), separator, sb);
    }

    public static String join(Collection<String> collection) {
        return StringUtils.join(collection, ',');
    }

    public static String join(Collection<String> collection, char separator) {
        if (collection == null || collection.isEmpty()) {
            return EMPTY_STRING;
        }
        StringBuilder result = new StringBuilder();
        StringUtils.join(collection, separator, result);
        return result.toString();
    }

    public static void join(Iterable<String> iterable, char separator, StringBuilder sb) {
        StringUtils.join(iterable, separator, (T x) -> x, sb);
    }

    public static <T> void join(T[] array, char separator, Function<T, String> function, StringBuilder sb) {
        if (array == null) {
            return;
        }
        StringUtils.join(Arrays.asList(array), separator, function, sb);
    }

    public static <T> void join(Iterable<T> iterable, char separator, Function<T, String> function, StringBuilder sb) {
        if (iterable == null) {
            return;
        }
        boolean first = true;
        for (T value : iterable) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(function.apply(value));
        }
    }
}

