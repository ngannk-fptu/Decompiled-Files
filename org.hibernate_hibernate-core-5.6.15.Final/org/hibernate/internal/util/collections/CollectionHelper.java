/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class CollectionHelper {
    public static final int MINIMUM_INITIAL_CAPACITY = 16;
    public static final float LOAD_FACTOR = 0.75f;
    @Deprecated
    public static final List EMPTY_LIST = Collections.EMPTY_LIST;
    @Deprecated
    public static final Collection EMPTY_COLLECTION = Collections.EMPTY_LIST;
    @Deprecated
    public static final Map EMPTY_MAP = Collections.EMPTY_MAP;

    private CollectionHelper() {
    }

    public static <K, V> Map<K, V> mapOfSize(int size) {
        return new HashMap(CollectionHelper.determineProperSizing(size), 0.75f);
    }

    public static int determineProperSizing(Map original) {
        return CollectionHelper.determineProperSizing(original.size());
    }

    public static <X, Y> Map<X, Y> makeCopy(Map<X, Y> map) {
        Map<X, Y> copy = CollectionHelper.mapOfSize(map.size() + 1);
        copy.putAll(map);
        return copy;
    }

    public static <K, V> HashMap<K, V> makeCopy(Map<K, V> original, Function<K, K> keyTransformer, Function<V, V> valueTransformer) {
        if (original == null) {
            return null;
        }
        HashMap copy = new HashMap(CollectionHelper.determineProperSizing(original));
        original.forEach((key, value) -> copy.put(keyTransformer.apply(key), valueTransformer.apply(value)));
        return copy;
    }

    public static <K, V> Map<K, V> makeMap(Collection<V> collection, Function<V, K> keyProducer) {
        return CollectionHelper.makeMap(collection, keyProducer, v -> v);
    }

    public static <K, V, E> Map<K, V> makeMap(Collection<E> collection, Function<E, K> keyProducer, Function<E, V> valueProducer) {
        if (CollectionHelper.isEmpty(collection)) {
            return Collections.emptyMap();
        }
        HashMap<K, V> map = new HashMap<K, V>(CollectionHelper.determineProperSizing(collection.size()));
        for (E element : collection) {
            map.put(keyProducer.apply(element), valueProducer.apply(element));
        }
        return map;
    }

    public static int determineProperSizing(Set original) {
        return CollectionHelper.determineProperSizing(original.size());
    }

    public static int determineProperSizing(int numberOfElements) {
        int actual = (int)((float)numberOfElements / 0.75f) + 1;
        return Math.max(actual, 16);
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentMap(int expectedNumberOfElements) {
        return CollectionHelper.concurrentMap(expectedNumberOfElements, 0.75f);
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentMap(int expectedNumberOfElements, float loadFactor) {
        int size = expectedNumberOfElements + 1 + (int)((float)expectedNumberOfElements * loadFactor);
        return new ConcurrentHashMap(size, loadFactor);
    }

    public static <T> ArrayList<T> arrayList(int anticipatedSize) {
        return new ArrayList(anticipatedSize);
    }

    public static <T> Set<T> makeCopy(Set<T> source) {
        if (source == null) {
            return null;
        }
        int size = source.size();
        HashSet<T> copy = new HashSet<T>(size + 1);
        copy.addAll(source);
        return copy;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !CollectionHelper.isEmpty(collection);
    }

    public static boolean isNotEmpty(Map map) {
        return !CollectionHelper.isEmpty(map);
    }

    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    public static <T> Set<T> toSmallSet(Set<T> set) {
        switch (set.size()) {
            case 0: {
                return Collections.EMPTY_SET;
            }
            case 1: {
                return Collections.singleton(set.iterator().next());
            }
        }
        return set;
    }

    public static <K, V> Map<K, V> toSmallMap(Map<K, V> map) {
        switch (map.size()) {
            case 0: {
                return Collections.EMPTY_MAP;
            }
            case 1: {
                Map.Entry<K, V> entry = map.entrySet().iterator().next();
                return Collections.singletonMap(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public static <V> List<V> toSmallList(ArrayList<V> arrayList) {
        switch (arrayList.size()) {
            case 0: {
                return Collections.EMPTY_LIST;
            }
            case 1: {
                return Collections.singletonList(arrayList.get(0));
            }
        }
        arrayList.trimToSize();
        return arrayList;
    }
}

