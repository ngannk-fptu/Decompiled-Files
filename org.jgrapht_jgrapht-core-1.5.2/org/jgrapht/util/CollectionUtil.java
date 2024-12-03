/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class CollectionUtil {
    private CollectionUtil() {
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap(CollectionUtil.capacityForSize(expectedSize));
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
        return new LinkedHashMap(CollectionUtil.capacityForSize(expectedSize));
    }

    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet(CollectionUtil.capacityForSize(expectedSize));
    }

    public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
        return new LinkedHashSet(CollectionUtil.capacityForSize(expectedSize));
    }

    private static int capacityForSize(int size) {
        return (int)((float)size / 0.75f + 1.0f);
    }

    public static <E> E getElement(Iterable<E> iterable, int index) {
        if (iterable instanceof List) {
            return ((List)iterable).get(index);
        }
        Iterator<E> it = iterable.iterator();
        for (int i = 0; i < index && it.hasNext(); ++i) {
            it.next();
        }
        if (it.hasNext()) {
            return it.next();
        }
        throw new IndexOutOfBoundsException(index);
    }
}

