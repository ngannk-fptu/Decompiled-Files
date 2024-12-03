/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.utils;

import java.util.ArrayList;
import java.util.List;

public final class Lists {
    private Lists() {
    }

    public static <T> List<T> createWhenNull(List<T> list) {
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    public static <T> List<T> create(T item) {
        ArrayList<T> list = new ArrayList<T>();
        list.add(item);
        return list;
    }

    public static <T> T firstEntry(List<T> list) {
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(List<?> list) {
        return !Lists.isEmpty(list);
    }

    public static boolean sizeIs(List<?> list, int size) {
        if (size == 0) {
            return list == null || list.isEmpty();
        }
        return list != null && list.size() == size;
    }

    public static <T> List<T> emptyToNull(List<T> list) {
        if (Lists.isEmpty(list)) {
            return null;
        }
        return list;
    }
}

