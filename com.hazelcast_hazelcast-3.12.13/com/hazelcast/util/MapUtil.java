/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.mapreduce.impl.HashMapAdapter;
import com.hazelcast.util.collection.Int2ObjectHashMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MapUtil {
    private static final float HASHMAP_DEFAULT_LOAD_FACTOR = 0.75f;

    private MapUtil() {
    }

    public static <K, V> Map<K, V> createHashMap(int expectedMapSize) {
        int initialCapacity = MapUtil.calculateInitialCapacity(expectedMapSize);
        return new HashMap(initialCapacity, 0.75f);
    }

    public static <K, V> Map<K, V> createHashMapAdapter(int expectedMapSize) {
        int initialCapacity = MapUtil.calculateInitialCapacity(expectedMapSize);
        return new HashMapAdapter(initialCapacity, 0.75f);
    }

    public static <K, V> Map<K, V> createLinkedHashMap(int expectedMapSize) {
        int initialCapacity = MapUtil.calculateInitialCapacity(expectedMapSize);
        return new LinkedHashMap(initialCapacity, 0.75f);
    }

    public static <K, V> ConcurrentMap<K, V> createConcurrentHashMap(int expectedMapSize) {
        return new ConcurrentHashMap(expectedMapSize);
    }

    public static <V> Int2ObjectHashMap<V> createInt2ObjectHashMap(int expectedMapSize) {
        int initialCapacity = (int)((double)expectedMapSize / 0.6) + 1;
        return new Int2ObjectHashMap(initialCapacity, 0.6);
    }

    public static int calculateInitialCapacity(int expectedMapSize) {
        return (int)((float)expectedMapSize / 0.75f) + 1;
    }

    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static int toIntSize(long size) {
        assert (size >= 0L) : "Invalid size value: " + size;
        return size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)size;
    }
}

