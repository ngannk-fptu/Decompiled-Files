/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingSortedMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ParametricNullness;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.function.BiFunction;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingNavigableMap<K, V>
extends ForwardingSortedMap<K, V>
implements NavigableMap<K, V> {
    protected ForwardingNavigableMap() {
    }

    @Override
    protected abstract NavigableMap<K, V> delegate();

    @Override
    @CheckForNull
    public Map.Entry<K, V> lowerEntry(@ParametricNullness K key) {
        return this.delegate().lowerEntry(key);
    }

    @CheckForNull
    protected Map.Entry<K, V> standardLowerEntry(@ParametricNullness K key) {
        return this.headMap(key, false).lastEntry();
    }

    @Override
    @CheckForNull
    public K lowerKey(@ParametricNullness K key) {
        return this.delegate().lowerKey(key);
    }

    @CheckForNull
    protected K standardLowerKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.lowerEntry(key));
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> floorEntry(@ParametricNullness K key) {
        return this.delegate().floorEntry(key);
    }

    @CheckForNull
    protected Map.Entry<K, V> standardFloorEntry(@ParametricNullness K key) {
        return this.headMap(key, true).lastEntry();
    }

    @Override
    @CheckForNull
    public K floorKey(@ParametricNullness K key) {
        return this.delegate().floorKey(key);
    }

    @CheckForNull
    protected K standardFloorKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.floorEntry(key));
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> ceilingEntry(@ParametricNullness K key) {
        return this.delegate().ceilingEntry(key);
    }

    @CheckForNull
    protected Map.Entry<K, V> standardCeilingEntry(@ParametricNullness K key) {
        return this.tailMap(key, true).firstEntry();
    }

    @Override
    @CheckForNull
    public K ceilingKey(@ParametricNullness K key) {
        return this.delegate().ceilingKey(key);
    }

    @CheckForNull
    protected K standardCeilingKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.ceilingEntry(key));
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> higherEntry(@ParametricNullness K key) {
        return this.delegate().higherEntry(key);
    }

    @CheckForNull
    protected Map.Entry<K, V> standardHigherEntry(@ParametricNullness K key) {
        return this.tailMap(key, false).firstEntry();
    }

    @Override
    @CheckForNull
    public K higherKey(@ParametricNullness K key) {
        return this.delegate().higherKey(key);
    }

    @CheckForNull
    protected K standardHigherKey(@ParametricNullness K key) {
        return Maps.keyOrNull(this.higherEntry(key));
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> firstEntry() {
        return this.delegate().firstEntry();
    }

    @CheckForNull
    protected Map.Entry<K, V> standardFirstEntry() {
        return Iterables.getFirst(this.entrySet(), null);
    }

    protected K standardFirstKey() {
        Map.Entry<K, V> entry = this.firstEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        }
        return entry.getKey();
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> lastEntry() {
        return this.delegate().lastEntry();
    }

    @CheckForNull
    protected Map.Entry<K, V> standardLastEntry() {
        return Iterables.getFirst(this.descendingMap().entrySet(), null);
    }

    protected K standardLastKey() {
        Map.Entry<K, V> entry = this.lastEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        }
        return entry.getKey();
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> pollFirstEntry() {
        return this.delegate().pollFirstEntry();
    }

    @CheckForNull
    protected Map.Entry<K, V> standardPollFirstEntry() {
        return Iterators.pollNext(this.entrySet().iterator());
    }

    @Override
    @CheckForNull
    public Map.Entry<K, V> pollLastEntry() {
        return this.delegate().pollLastEntry();
    }

    @CheckForNull
    protected Map.Entry<K, V> standardPollLastEntry() {
        return Iterators.pollNext(this.descendingMap().entrySet().iterator());
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return this.delegate().descendingMap();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return this.delegate().navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return this.delegate().descendingKeySet();
    }

    protected NavigableSet<K> standardDescendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }

    @Override
    protected SortedMap<K, V> standardSubMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
        return this.subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap<K, V> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
        return this.delegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
        return this.delegate().headMap(toKey, inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
        return this.delegate().tailMap(fromKey, inclusive);
    }

    protected SortedMap<K, V> standardHeadMap(@ParametricNullness K toKey) {
        return this.headMap(toKey, false);
    }

    protected SortedMap<K, V> standardTailMap(@ParametricNullness K fromKey) {
        return this.tailMap(fromKey, true);
    }

    protected class StandardNavigableKeySet
    extends Maps.NavigableKeySet<K, V> {
        public StandardNavigableKeySet(ForwardingNavigableMap this$0) {
            super(this$0);
        }
    }

    protected class StandardDescendingMap
    extends Maps.DescendingMap<K, V> {
        @Override
        NavigableMap<K, V> forward() {
            return ForwardingNavigableMap.this;
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            this.forward().replaceAll(function);
        }

        @Override
        protected Iterator<Map.Entry<K, V>> entryIterator() {
            return new Iterator<Map.Entry<K, V>>(){
                @CheckForNull
                private Map.Entry<K, V> toRemove = null;
                @CheckForNull
                private Map.Entry<K, V> nextOrNull = StandardDescendingMap.this.forward().lastEntry();

                @Override
                public boolean hasNext() {
                    return this.nextOrNull != null;
                }

                @Override
                public Map.Entry<K, V> next() {
                    if (this.nextOrNull == null) {
                        throw new NoSuchElementException();
                    }
                    try {
                        Map.Entry entry = this.nextOrNull;
                        return entry;
                    }
                    finally {
                        this.toRemove = this.nextOrNull;
                        this.nextOrNull = StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
                    }
                }

                @Override
                public void remove() {
                    if (this.toRemove == null) {
                        throw new IllegalStateException("no calls to next() since the last call to remove()");
                    }
                    StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
                    this.toRemove = null;
                }
            };
        }
    }
}

