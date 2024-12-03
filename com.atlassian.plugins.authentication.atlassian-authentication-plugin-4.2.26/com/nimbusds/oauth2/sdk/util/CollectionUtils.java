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

    private CollectionUtils() {
    }
}

