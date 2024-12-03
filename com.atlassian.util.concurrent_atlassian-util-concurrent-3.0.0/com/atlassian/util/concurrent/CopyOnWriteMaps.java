/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.CopyOnWriteMap;
import com.atlassian.util.concurrent.CopyOnWriteSortedMap;
import java.util.Comparator;
import java.util.Map;

@Deprecated
public class CopyOnWriteMaps {
    @Deprecated
    public static <K, V> CopyOnWriteMap<K, V> newHashMap() {
        return CopyOnWriteMap.newHashMap();
    }

    @Deprecated
    public static <K, V> CopyOnWriteMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return CopyOnWriteMap.newHashMap(map);
    }

    @Deprecated
    public static <K, V> CopyOnWriteMap<K, V> newLinkedMap() {
        return CopyOnWriteMap.newLinkedMap();
    }

    @Deprecated
    public static <K, V> CopyOnWriteMap<K, V> newLinkedMap(Map<? extends K, ? extends V> map) {
        return CopyOnWriteMap.newLinkedMap(map);
    }

    @Deprecated
    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap() {
        return CopyOnWriteSortedMap.newTreeMap();
    }

    @Deprecated
    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap(Map<? extends K, ? extends V> map) {
        return CopyOnWriteSortedMap.newTreeMap(map);
    }

    @Deprecated
    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap(Comparator<? super K> comparator) {
        return CopyOnWriteSortedMap.newTreeMap(comparator);
    }

    @Deprecated
    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
        return CopyOnWriteSortedMap.newTreeMap(map, comparator);
    }
}

