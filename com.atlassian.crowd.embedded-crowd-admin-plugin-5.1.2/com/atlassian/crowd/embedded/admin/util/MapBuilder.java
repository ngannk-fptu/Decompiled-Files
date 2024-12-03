/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.embedded.admin.util;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapBuilder<K, V> {
    private final Map<K, V> map = new LinkedHashMap();

    public static <K, V> Map<K, V> emptyMap() {
        return new MapBuilder<K, V>().toMap();
    }

    public static <K, V> MapBuilder<K, V> newBuilder() {
        return new MapBuilder<K, V>();
    }

    public static <K, V> MapBuilder<K, V> newBuilder(K key, V value) {
        return new MapBuilder<K, V>().add(key, value);
    }

    public static <K, V> MapBuilder<K, V> newBuilder(Map<? extends K, ? extends V> map) {
        MapBuilder<? extends K, ? extends V> builder = MapBuilder.newBuilder();
        builder.addAll(map);
        return builder;
    }

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        return Collections.singletonMap(key, value);
    }

    private MapBuilder() {
    }

    public static <K, V> Map<K, V> build(K key, V value) {
        MapBuilder<K, V> builder = MapBuilder.newBuilder();
        return builder.add(key, value).toMap();
    }

    public MapBuilder<K, V> add(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> addIfValueNotNull(K key, V value) {
        if (value != null) {
            this.add(key, value);
        }
        return this;
    }

    public MapBuilder<K, V> addAll(Map<? extends K, ? extends V> map) {
        if (map != null) {
            this.map.putAll(map);
        }
        return this;
    }

    public Map<K, V> toMap() {
        return Collections.unmodifiableMap(new HashMap<K, V>(this.map));
    }

    public HashMap<K, V> toHashMap() {
        return new HashMap<K, V>(this.map);
    }

    public LinkedHashMap<K, V> toLinkedHashMap() {
        return new LinkedHashMap<K, V>(this.map);
    }

    public Map<K, V> toListOrderedMap() {
        return Collections.unmodifiableMap(this.toLinkedHashMap());
    }

    public SortedMap<K, V> toSortedMap() {
        return Collections.unmodifiableSortedMap(this.toTreeMap());
    }

    public SortedMap<K, V> toSortedMap(Comparator<K> comparator) {
        return Collections.unmodifiableSortedMap(this.toTreeMap(comparator));
    }

    public TreeMap<K, V> toTreeMap() {
        return new TreeMap<K, V>(this.map);
    }

    public TreeMap<K, V> toTreeMap(Comparator<K> comparator) {
        TreeMap<K, V> result = new TreeMap<K, V>(comparator);
        result.putAll(this.map);
        return result;
    }

    public Map<K, V> toMutableMap() {
        return this.toHashMap();
    }

    public Map<K, V> toFastMap() {
        return ImmutableMap.copyOf(this.map);
    }

    @Deprecated
    public Map<K, V> toImmutableMap() {
        return this.toMap();
    }
}

