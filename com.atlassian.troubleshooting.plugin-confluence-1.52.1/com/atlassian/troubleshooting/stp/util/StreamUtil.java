/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StreamUtil {
    private StreamUtil() {
    }

    public static <T> Predicate<T> distinctByField(Function<? super T, Object> keyExtractor) {
        ConcurrentHashMap map = new ConcurrentHashMap();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

