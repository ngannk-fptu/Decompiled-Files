/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractSortedSetMultimap;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap<K, V>
extends AbstractSortedSetMultimap<K, V> {
    AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> map) {
        super(map);
    }

    @Override
    public SortedMap<K, Collection<V>> asMap() {
        return (SortedMap)super.asMap();
    }

    @Override
    SortedMap<K, Collection<V>> backingMap() {
        return (SortedMap)super.backingMap();
    }

    @Override
    public SortedSet<K> keySet() {
        return (SortedSet)super.keySet();
    }

    @Override
    Set<K> createKeySet() {
        return this.createMaybeNavigableKeySet();
    }
}

