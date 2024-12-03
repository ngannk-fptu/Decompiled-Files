/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class CollectionUtil {
    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !CollectionUtil.isEmpty(collection);
    }

    public static <K, V> List<V> addToValueList(Map<K, List<V>> map, K key, V value) {
        List<V> valueList = map.get(key);
        if (valueList == null) {
            valueList = new ArrayList<V>();
            map.put(key, valueList);
        }
        valueList.add(value);
        return valueList;
    }

    public static <T> T getItemAtPositionOrNull(Collection<T> collection, int position) {
        if (position >= collection.size()) {
            return null;
        }
        if (collection instanceof List) {
            return (T)((List)collection).get(position);
        }
        Iterator<T> iterator = collection.iterator();
        T item = null;
        for (int i = 0; i < position + 1; ++i) {
            item = iterator.next();
        }
        return item;
    }

    public static <C> Collection<Data> objectToDataCollection(Collection<C> collection, SerializationService serializationService) {
        ArrayList<Data> dataCollection = new ArrayList<Data>(collection.size());
        CollectionUtil.objectToDataCollection(collection, dataCollection, serializationService, null);
        return dataCollection;
    }

    public static <C> void objectToDataCollection(Collection<C> objectCollection, Collection<Data> dataCollection, SerializationService serializationService, String errorMessage) {
        for (C item : objectCollection) {
            Preconditions.checkNotNull(item, errorMessage);
            dataCollection.add((Data)serializationService.toData(item));
        }
    }

    public static int[] toIntArray(Collection<Integer> collection) {
        int[] collectionArray = new int[collection.size()];
        int index = 0;
        for (Integer item : collection) {
            collectionArray[index++] = item;
        }
        return collectionArray;
    }

    public static long[] toLongArray(Collection<Long> collection) {
        long[] collectionArray = new long[collection.size()];
        int index = 0;
        for (Long item : collection) {
            collectionArray[index++] = item;
        }
        return collectionArray;
    }

    public static List<Integer> toIntegerList(int[] array) {
        ArrayList<Integer> result = new ArrayList<Integer>(array.length);
        for (int partitionId : array) {
            result.add(partitionId);
        }
        return result;
    }

    public static <T> Collection<T> nullToEmpty(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }
}

