/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ValueCollectionFactory {
    private ValueCollectionFactory() {
    }

    public static <T> Collection<T> createCollection(MultiMapConfig.ValueCollectionType collectionType) {
        return ValueCollectionFactory.createCollection(collectionType, -1);
    }

    public static <T> Collection<T> createCollection(Collection collection) {
        MultiMapConfig.ValueCollectionType collectionType = ValueCollectionFactory.findCollectionType(collection);
        if (collection.isEmpty()) {
            return ValueCollectionFactory.emptyCollection(collectionType);
        }
        return ValueCollectionFactory.createCollection(collectionType, collection.size());
    }

    public static <T> Collection<T> createCollection(MultiMapConfig.ValueCollectionType collectionType, int initialCapacity) {
        switch (collectionType) {
            case SET: {
                return initialCapacity <= 0 ? new HashSet() : SetUtil.createHashSet(initialCapacity);
            }
            case LIST: {
                return new LinkedList();
            }
        }
        throw new IllegalArgumentException("[" + (Object)((Object)collectionType) + "] is not a known MultiMapConfig.ValueCollectionType!");
    }

    public static <T> Collection<T> emptyCollection(MultiMapConfig.ValueCollectionType collectionType) {
        switch (collectionType) {
            case SET: {
                return Collections.emptySet();
            }
            case LIST: {
                return Collections.emptyList();
            }
        }
        throw new IllegalArgumentException("[" + (Object)((Object)collectionType) + "] is not a known MultiMapConfig.ValueCollectionType!");
    }

    private static MultiMapConfig.ValueCollectionType findCollectionType(Collection collection) {
        if (collection instanceof Set) {
            return MultiMapConfig.ValueCollectionType.SET;
        }
        if (collection instanceof List) {
            return MultiMapConfig.ValueCollectionType.LIST;
        }
        throw new IllegalArgumentException("[" + collection.getClass() + "] is not a known MultiMapConfig.ValueCollectionType!");
    }
}

