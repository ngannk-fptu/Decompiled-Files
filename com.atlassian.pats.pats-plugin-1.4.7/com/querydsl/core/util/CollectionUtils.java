/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.querydsl.core.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CollectionUtils {
    public static <T> List<T> add(List<T> list, T element) {
        int size = list.size();
        if (size == 0) {
            return ImmutableList.of(element);
        }
        if (list instanceof ImmutableList) {
            if (size == 1) {
                T val = list.get(0);
                list = Lists.newArrayList();
                list.add(val);
            } else {
                list = Lists.newArrayList(list);
            }
        }
        list.add(element);
        return list;
    }

    public static <T> List<T> copyOf(List<T> list) {
        if (list instanceof ImmutableList) {
            return list;
        }
        return Lists.newArrayList(list);
    }

    public static <T> Set<T> add(Set<T> set, T element) {
        int size = set.size();
        if (size == 0) {
            return ImmutableSet.of(element);
        }
        if (set instanceof ImmutableSet) {
            if (size == 1) {
                T val = set.iterator().next();
                set = Sets.newHashSet();
                set.add(val);
            } else {
                set = Sets.newHashSet(set);
            }
        }
        set.add(element);
        return set;
    }

    public static <T> Set<T> copyOf(Set<T> set) {
        if (set instanceof ImmutableSet) {
            return set;
        }
        return Sets.newHashSet(set);
    }

    public static <T> Set<T> addSorted(Set<T> set, T element) {
        int size = set.size();
        if (size == 0) {
            return ImmutableSet.of(element);
        }
        if (set instanceof ImmutableSet) {
            if (size == 1) {
                T val = set.iterator().next();
                set = Sets.newLinkedHashSet();
                set.add(val);
            } else {
                set = Sets.newLinkedHashSet(set);
            }
        }
        set.add(element);
        return set;
    }

    public static <T> Set<T> removeSorted(Set<T> set, T element) {
        int size = set.size();
        if (size == 0 || size == 1 && set.contains(element)) {
            return ImmutableSet.of();
        }
        set.remove(element);
        return set;
    }

    public static <T> Set<T> copyOfSorted(Set<T> set) {
        if (set instanceof ImmutableSet) {
            return set;
        }
        return Sets.newLinkedHashSet(set);
    }

    public static <K, V> Map<K, V> put(Map<K, V> map, K key, V value) {
        int size = map.size();
        if (size == 0) {
            return ImmutableMap.of(key, value);
        }
        if (map instanceof ImmutableMap) {
            map = Maps.newHashMap(map);
        }
        map.put(key, value);
        return map;
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> map) {
        if (map instanceof ImmutableMap) {
            return map;
        }
        return Maps.newHashMap(map);
    }

    private CollectionUtils() {
    }
}

