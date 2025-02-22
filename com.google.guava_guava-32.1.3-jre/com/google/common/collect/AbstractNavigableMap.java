/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ParametricNullness;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class AbstractNavigableMap<K, V>
extends Maps.IteratorBasedAbstractMap<K, V>
implements NavigableMap<K, V> {
    AbstractNavigableMap() {
    }

    @Override
    @CheckForNull
    public abstract V get(@CheckForNull Object var1);

    @Override
    @CheckForNull
    public Map.Entry<K, V> firstEntry() {
        return Iterators.getNext(this.entryIterator(), null);
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> lastEntry() {
        return Iterators.getNext(this.descendingEntryIterator(), null);
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> pollFirstEntry() {
        return Iterators.pollNext(this.entryIterator());
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> pollLastEntry() {
        return Iterators.pollNext(this.descendingEntryIterator());
    }

    @Override
    @ParametricNullness
    public K firstKey() {
        Map.Entry<K, V> entry = this.firstEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        }
        return entry.getKey();
    }

    @Override
    @ParametricNullness
    public K lastKey() {
        Map.Entry<K, V> entry = this.lastEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        }
        return entry.getKey();
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> lowerEntry(@ParametricNullness K key) {
        return this.headMap(key, false).lastEntry();
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> floorEntry(@ParametricNullness K key) {
        return this.headMap(key, true).lastEntry();
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> ceilingEntry(@ParametricNullness K key) {
        return this.tailMap(key, true).firstEntry();
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> higherEntry(@ParametricNullness K key) {
        return this.tailMap(key, false).firstEntry();
    }

    @Override
    @CheckForNull
    public K lowerKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.lowerEntry(key));
    }

    @Override
    @CheckForNull
    public K floorKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.floorEntry(key));
    }

    @Override
    @CheckForNull
    public K ceilingKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.ceilingEntry(key));
    }

    @Override
    @CheckForNull
    public K higherKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.higherEntry(key));
    }

    abstract Iterator<Map.Entry<K, V>> descendingEntryIterator();

    @Override
    public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
        return this.subMap(fromKey, true, toKey, false);
    }

    @Override
    public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
        return this.headMap(toKey, false);
    }

    @Override
    public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
        return this.tailMap(fromKey, true);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return new Maps.NavigableKeySet(this);
    }

    @Override
    public Set<K> keySet() {
        return this.navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return new DescendingMap();
    }

    private final class DescendingMap
    extends Maps.DescendingMap<K, V> {
        private DescendingMap() {
        }

        @Override
        NavigableMap<K, V> forward() {
            return AbstractNavigableMap.this;
        }

        @Override
        Iterator<Map.Entry<K, V>> entryIterator() {
            return AbstractNavigableMap.this.descendingEntryIterator();
        }
    }
}

