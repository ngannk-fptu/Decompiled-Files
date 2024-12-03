/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.map.HashedMap
 */
package com.atlassian.core.util.map;

import java.util.Map;
import org.apache.commons.collections.map.HashedMap;

@Deprecated
public class EasyMap {
    public static <K, V> Map<K, V> build() {
        return EasyMap.createMap(1);
    }

    public static <K, V> Map<K, V> build(K key1, V value1) {
        Map<K, V> map = EasyMap.createMap(1);
        map.put(key1, value1);
        return map;
    }

    public static <K, V> Map<K, V> build(K key1, V value1, K key2, V value2) {
        Map<K, V> map = EasyMap.createMap(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static <K, V> Map<K, V> build(K key1, V value1, K key2, V value2, K key3, V value3) {
        Map<K, V> map = EasyMap.createMap(3);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    public static <K, V> Map<K, V> build(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        Map<K, V> map = EasyMap.createMap(4);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }

    public static <K, V> Map<K, V> build(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        Map<K, V> map = EasyMap.createMap(5);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return map;
    }

    public static <K, V> Map<K, V> build(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6) {
        Map<K, V> map = EasyMap.createMap(6);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        return map;
    }

    public static <K, V> Map<K, V> build(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7) {
        Map<K, V> map = EasyMap.createMap(7);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        return map;
    }

    public static <K, V> Map<K, V> createMap(int size) {
        return new HashedMap(size);
    }

    public static <K, V> Map<K, V> build(Object ... objects) {
        Map<Object, Object> map = EasyMap.createMap(1);
        if (objects.length % 2 != 0) {
            throw new RuntimeException("The number of parameters should be even when building a map");
        }
        for (int i = 0; i < objects.length; i += 2) {
            Object key = objects[i];
            Object value = objects[i + 1];
            map.put(key, value);
        }
        return map;
    }
}

