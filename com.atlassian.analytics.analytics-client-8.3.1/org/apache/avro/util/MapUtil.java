/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class MapUtil {
    private MapUtil() {
    }

    public static <K, V> V computeIfAbsent(ConcurrentMap<K, V> map, K key, Function<K, V> mappingFunction) {
        Object value = map.get(key);
        if (value != null) {
            return value;
        }
        return (V)map.computeIfAbsent(key, mappingFunction::apply);
    }
}

