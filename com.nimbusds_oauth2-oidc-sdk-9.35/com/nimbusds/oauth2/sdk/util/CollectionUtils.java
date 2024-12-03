/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import java.util.Collection;

public final class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static <T> boolean contains(Collection<T> collection, T item) {
        return CollectionUtils.isNotEmpty(collection) && collection.contains(item);
    }

    public static <T> boolean intersect(Collection<T> a, Collection<T> b) {
        if (CollectionUtils.isEmpty(a) || CollectionUtils.isEmpty(b)) {
            return false;
        }
        for (T item : a) {
            if (!b.contains(item)) continue;
            return true;
        }
        return false;
    }

    private CollectionUtils() {
    }
}

