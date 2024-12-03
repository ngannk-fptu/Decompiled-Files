/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.BaseImmutableMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.TransformedIterator;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
final class Synchronized {
    private Synchronized() {
    }

    private static <E> Collection<E> collection(Collection<E> collection, @CheckForNull Object mutex) {
        return new SynchronizedCollection(collection, mutex);
    }

    @VisibleForTesting
    static <E> Set<E> set(Set<E> set, @CheckForNull Object mutex) {
        return new SynchronizedSet<E>(set, mutex);
    }

    private static <E> SortedSet<E> sortedSet(SortedSet<E> set, @CheckForNull Object mutex) {
        return new SynchronizedSortedSet<E>(set, mutex);
    }

    private static <E> List<E> list(List<E> list, @CheckForNull Object mutex) {
        return list instanceof RandomAccess ? new SynchronizedRandomAccessList<E>(list, mutex) : new SynchronizedList<E>(list, mutex);
    }

    static <E> Multiset<E> multiset(Multiset<E> multiset, @CheckForNull Object mutex) {
        if (multiset instanceof SynchronizedMultiset || multiset instanceof ImmutableMultiset) {
            return multiset;
        }
        return new SynchronizedMultiset<E>(multiset, mutex);
    }

    static <K, V> Multimap<K, V> multimap(Multimap<K, V> multimap, @CheckForNull Object mutex) {
        if (multimap instanceof SynchronizedMultimap || multimap instanceof BaseImmutableMultimap) {
            return multimap;
        }
        return new SynchronizedMultimap<K, V>(multimap, mutex);
    }

    static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> multimap, @CheckForNull Object mutex) {
        if (multimap instanceof SynchronizedListMultimap || multimap instanceof BaseImmutableMultimap) {
            return multimap;
        }
        return new SynchronizedListMultimap<K, V>(multimap, mutex);
    }

    static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> multimap, @CheckForNull Object mutex) {
        if (multimap instanceof SynchronizedSetMultimap || multimap instanceof BaseImmutableMultimap) {
            return multimap;
        }
        return new SynchronizedSetMultimap<K, V>(multimap, mutex);
    }

    static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> multimap, @CheckForNull Object mutex) {
        if (multimap instanceof SynchronizedSortedSetMultimap) {
            return multimap;
        }
        return new SynchronizedSortedSetMultimap<K, V>(multimap, mutex);
    }

    private static <E> Collection<E> typePreservingCollection(Collection<E> collection, @CheckForNull Object mutex) {
        if (collection instanceof SortedSet) {
            return Synchronized.sortedSet((SortedSet)collection, mutex);
        }
        if (collection instanceof Set) {
            return Synchronized.set((Set)collection, mutex);
        }
        if (collection instanceof List) {
            return Synchronized.list((List)collection, mutex);
        }
        return Synchronized.collection(collection, mutex);
    }

    private static <E> Set<E> typePreservingSet(Set<E> set, @CheckForNull Object mutex) {
        if (set instanceof SortedSet) {
            return Synchronized.sortedSet((SortedSet)set, mutex);
        }
        return Synchronized.set(set, mutex);
    }

    @VisibleForTesting
    static <K, V> Map<K, V> map(Map<K, V> map, @CheckForNull Object mutex) {
        return new SynchronizedMap<K, V>(map, mutex);
    }

    static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> sortedMap, @CheckForNull Object mutex) {
        return new SynchronizedSortedMap<K, V>(sortedMap, mutex);
    }

    static <K, V> BiMap<K, V> biMap(BiMap<K, V> bimap, @CheckForNull Object mutex) {
        if (bimap instanceof SynchronizedBiMap || bimap instanceof ImmutableBiMap) {
            return bimap;
        }
        return new SynchronizedBiMap(bimap, mutex, null);
    }

    @GwtIncompatible
    static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet, @CheckForNull Object mutex) {
        return new SynchronizedNavigableSet<E>(navigableSet, mutex);
    }

    @GwtIncompatible
    static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet) {
        return Synchronized.navigableSet(navigableSet, null);
    }

    @GwtIncompatible
    static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap) {
        return Synchronized.navigableMap(navigableMap, null);
    }

    @GwtIncompatible
    static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap, @CheckForNull Object mutex) {
        return new SynchronizedNavigableMap<K, V>(navigableMap, mutex);
    }

    @CheckForNull
    @GwtIncompatible
    private static <K, V> Map.Entry<K, V> nullableSynchronizedEntry(@CheckForNull Map.Entry<K, V> entry, @CheckForNull Object mutex) {
        if (entry == null) {
            return null;
        }
        return new SynchronizedEntry<K, V>(entry, mutex);
    }

    static <E> Queue<E> queue(Queue<E> queue, @CheckForNull Object mutex) {
        return queue instanceof SynchronizedQueue ? queue : new SynchronizedQueue(queue, mutex);
    }

    static <E> Deque<E> deque(Deque<E> deque, @CheckForNull Object mutex) {
        return new SynchronizedDeque<E>(deque, mutex);
    }

    static <R, C, V> Table<R, C, V> table(Table<R, C, V> table, @CheckForNull Object mutex) {
        return new SynchronizedTable<R, C, V>(table, mutex);
    }

    private static final class SynchronizedTable<R, C, V>
    extends SynchronizedObject
    implements Table<R, C, V> {
        SynchronizedTable(Table<R, C, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Table<R, C, V> delegate() {
            return (Table)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(@CheckForNull Object rowKey, @CheckForNull Object columnKey) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().contains(rowKey, columnKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsRow(@CheckForNull Object rowKey) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsRow(rowKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsColumn(@CheckForNull Object columnKey) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsColumn(columnKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(@CheckForNull Object value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsValue(value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V get(@CheckForNull Object rowKey, @CheckForNull Object columnKey) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().get(rowKey, columnKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V put(@ParametricNullness R rowKey, @ParametricNullness C columnKey, @ParametricNullness V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().put(rowKey, columnKey, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().putAll(table);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V remove(@CheckForNull Object rowKey, @CheckForNull Object columnKey) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(rowKey, columnKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<C, V> row(@ParametricNullness R rowKey) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.map(this.delegate().row(rowKey), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<R, V> column(@ParametricNullness C columnKey) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.map(this.delegate().column(columnKey), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Table.Cell<R, C, V>> cellSet() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.set(this.delegate().cellSet(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<R> rowKeySet() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.set(this.delegate().rowKeySet(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<C> columnKeySet() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.set(this.delegate().columnKeySet(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> values() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.collection(this.delegate().values(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<R, Map<C, V>> rowMap() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.map(Maps.transformValues(this.delegate().rowMap(), new Function<Map<C, V>, Map<C, V>>(){

                    @Override
                    public Map<C, V> apply(Map<C, V> t) {
                        return Synchronized.map(t, mutex);
                    }
                }), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<C, Map<R, V>> columnMap() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.map(Maps.transformValues(this.delegate().columnMap(), new Function<Map<R, V>, Map<R, V>>(){

                    @Override
                    public Map<R, V> apply(Map<R, V> t) {
                        return Synchronized.map(t, mutex);
                    }
                }), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object obj) {
            if (this == obj) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(obj);
            }
        }
    }

    private static final class SynchronizedDeque<E>
    extends SynchronizedQueue<E>
    implements Deque<E> {
        private static final long serialVersionUID = 0L;

        SynchronizedDeque(Deque<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Deque<E> delegate() {
            return (Deque)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addFirst(E e) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().addFirst(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addLast(E e) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().addLast(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean offerFirst(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().offerFirst(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean offerLast(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().offerLast(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E removeFirst() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E removeLast() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeLast();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E pollFirst() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().pollFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E pollLast() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().pollLast();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E getFirst() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().getFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E getLast() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().getLast();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E peekFirst() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().peekFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E peekLast() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().peekLast();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeFirstOccurrence(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeFirstOccurrence(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeLastOccurrence(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeLastOccurrence(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void push(E e) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().push(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E pop() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().pop();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Iterator<E> descendingIterator() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().descendingIterator();
            }
        }
    }

    private static class SynchronizedQueue<E>
    extends SynchronizedCollection<E>
    implements Queue<E> {
        private static final long serialVersionUID = 0L;

        SynchronizedQueue(Queue<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Queue<E> delegate() {
            return (Queue)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E element() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().element();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean offer(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().offer(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E peek() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().peek();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E poll() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().poll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E remove() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove();
            }
        }
    }

    @GwtIncompatible
    private static class SynchronizedEntry<K, V>
    extends SynchronizedObject
    implements Map.Entry<K, V> {
        private static final long serialVersionUID = 0L;

        SynchronizedEntry(Map.Entry<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Map.Entry<K, V> delegate() {
            return (Map.Entry)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object obj) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K getKey() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().getKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V getValue() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().getValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V setValue(V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().setValue(value);
            }
        }
    }

    @GwtIncompatible
    @VisibleForTesting
    static class SynchronizedNavigableMap<K, V>
    extends SynchronizedSortedMap<K, V>
    implements NavigableMap<K, V> {
        @CheckForNull
        transient NavigableSet<K> descendingKeySet;
        @CheckForNull
        transient NavigableMap<K, V> descendingMap;
        @CheckForNull
        transient NavigableSet<K> navigableKeySet;
        private static final long serialVersionUID = 0L;

        SynchronizedNavigableMap(NavigableMap<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        NavigableMap<K, V> delegate() {
            return (NavigableMap)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> ceilingEntry(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().ceilingEntry(key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public K ceilingKey(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().ceilingKey(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableSet<K> descendingKeySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.descendingKeySet == null) {
                    this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), this.mutex);
                    return this.descendingKeySet;
                }
                return this.descendingKeySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableMap<K, V> descendingMap() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.descendingMap == null) {
                    this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), this.mutex);
                    return this.descendingMap;
                }
                return this.descendingMap;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> firstEntry() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().firstEntry(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> floorEntry(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().floorEntry(key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public K floorKey(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().floorKey(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.navigableMap(this.delegate().headMap(toKey, inclusive), this.mutex);
            }
        }

        @Override
        public SortedMap<K, V> headMap(K toKey) {
            return this.headMap(toKey, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> higherEntry(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().higherEntry(key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public K higherKey(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().higherKey(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> lastEntry() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().lastEntry(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> lowerEntry(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().lowerEntry(key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public K lowerKey(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().lowerKey(key);
            }
        }

        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableSet<K> navigableKeySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.navigableKeySet == null) {
                    this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), this.mutex);
                    return this.navigableKeySet;
                }
                return this.navigableKeySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> pollFirstEntry() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().pollFirstEntry(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Map.Entry<K, V> pollLastEntry() {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().pollLastEntry(), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.navigableMap(this.delegate().subMap(fromKey, fromInclusive, toKey, toInclusive), this.mutex);
            }
        }

        @Override
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return this.subMap(fromKey, true, toKey, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.navigableMap(this.delegate().tailMap(fromKey, inclusive), this.mutex);
            }
        }

        @Override
        public SortedMap<K, V> tailMap(K fromKey) {
            return this.tailMap(fromKey, true);
        }
    }

    @GwtIncompatible
    @VisibleForTesting
    static class SynchronizedNavigableSet<E>
    extends SynchronizedSortedSet<E>
    implements NavigableSet<E> {
        @CheckForNull
        transient NavigableSet<E> descendingSet;
        private static final long serialVersionUID = 0L;

        SynchronizedNavigableSet(NavigableSet<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        NavigableSet<E> delegate() {
            return (NavigableSet)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E ceiling(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().ceiling(e);
            }
        }

        @Override
        public Iterator<E> descendingIterator() {
            return this.delegate().descendingIterator();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableSet<E> descendingSet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.descendingSet == null) {
                    NavigableSet dS = Synchronized.navigableSet(this.delegate().descendingSet(), this.mutex);
                    this.descendingSet = dS;
                    return dS;
                }
                return this.descendingSet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E floor(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().floor(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.navigableSet(this.delegate().headSet(toElement, inclusive), this.mutex);
            }
        }

        @Override
        public SortedSet<E> headSet(E toElement) {
            return this.headSet(toElement, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E higher(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().higher(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E lower(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().lower(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E pollFirst() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().pollFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public E pollLast() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().pollLast();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.navigableSet(this.delegate().subSet(fromElement, fromInclusive, toElement, toInclusive), this.mutex);
            }
        }

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return this.subSet(fromElement, true, toElement, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.navigableSet(this.delegate().tailSet(fromElement, inclusive), this.mutex);
            }
        }

        @Override
        public SortedSet<E> tailSet(E fromElement) {
            return this.tailSet(fromElement, true);
        }
    }

    private static class SynchronizedAsMapValues<V>
    extends SynchronizedCollection<Collection<V>> {
        private static final long serialVersionUID = 0L;

        SynchronizedAsMapValues(Collection<Collection<V>> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        public Iterator<Collection<V>> iterator() {
            return new TransformedIterator<Collection<V>, Collection<V>>(super.iterator()){

                @Override
                Collection<V> transform(Collection<V> from) {
                    return Synchronized.typePreservingCollection(from, mutex);
                }
            };
        }
    }

    private static class SynchronizedAsMap<K, V>
    extends SynchronizedMap<K, Collection<V>> {
        @CheckForNull
        transient Set<Map.Entry<K, Collection<V>>> asMapEntrySet;
        @CheckForNull
        transient Collection<Collection<V>> asMapValues;
        private static final long serialVersionUID = 0L;

        SynchronizedAsMap(Map<K, Collection<V>> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Collection<V> get(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                Collection collection = (Collection)super.get(key);
                return collection == null ? null : Synchronized.typePreservingCollection(collection, this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Map.Entry<K, Collection<V>>> entrySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.asMapEntrySet == null) {
                    this.asMapEntrySet = new SynchronizedAsMapEntries(this.delegate().entrySet(), this.mutex);
                }
                return this.asMapEntrySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<Collection<V>> values() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.asMapValues == null) {
                    this.asMapValues = new SynchronizedAsMapValues(this.delegate().values(), this.mutex);
                }
                return this.asMapValues;
            }
        }

        @Override
        public boolean containsValue(@CheckForNull Object o) {
            return this.values().contains(o);
        }
    }

    @VisibleForTesting
    static class SynchronizedBiMap<K, V>
    extends SynchronizedMap<K, V>
    implements BiMap<K, V>,
    Serializable {
        @CheckForNull
        private transient Set<V> valueSet;
        @CheckForNull
        @RetainedWith
        private transient BiMap<V, K> inverse;
        private static final long serialVersionUID = 0L;

        private SynchronizedBiMap(BiMap<K, V> delegate, @CheckForNull Object mutex, @CheckForNull BiMap<V, K> inverse) {
            super(delegate, mutex);
            this.inverse = inverse;
        }

        @Override
        BiMap<K, V> delegate() {
            return (BiMap)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<V> values() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.valueSet == null) {
                    this.valueSet = Synchronized.set(this.delegate().values(), this.mutex);
                }
                return this.valueSet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V forcePut(@ParametricNullness K key, @ParametricNullness V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().forcePut(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BiMap<V, K> inverse() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.inverse == null) {
                    this.inverse = new SynchronizedBiMap<K, V>(this.delegate().inverse(), this.mutex, this);
                }
                return this.inverse;
            }
        }
    }

    static class SynchronizedSortedMap<K, V>
    extends SynchronizedMap<K, V>
    implements SortedMap<K, V> {
        private static final long serialVersionUID = 0L;

        SynchronizedSortedMap(SortedMap<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        SortedMap<K, V> delegate() {
            return (SortedMap)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().comparator();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K firstKey() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().firstKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedMap<K, V> headMap(K toKey) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedMap(this.delegate().headMap(toKey), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K lastKey() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().lastKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedMap(this.delegate().subMap(fromKey, toKey), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedMap<K, V> tailMap(K fromKey) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedMap(this.delegate().tailMap(fromKey), this.mutex);
            }
        }
    }

    private static class SynchronizedMap<K, V>
    extends SynchronizedObject
    implements Map<K, V> {
        @CheckForNull
        transient Set<K> keySet;
        @CheckForNull
        transient Collection<V> values;
        @CheckForNull
        transient Set<Map.Entry<K, V>> entrySet;
        private static final long serialVersionUID = 0L;

        SynchronizedMap(Map<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Map<K, V> delegate() {
            return (Map)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsKey(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(@CheckForNull Object value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsValue(value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entrySet(), this.mutex);
                }
                return this.entrySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V get(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().get(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V getOrDefault(@CheckForNull Object key, @CheckForNull V defaultValue) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<K> keySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.keySet == null) {
                    this.keySet = Synchronized.set(this.delegate().keySet(), this.mutex);
                }
                return this.keySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V put(K key, V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().put(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V putIfAbsent(K key, V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V replace(K key, V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends @Nullable V> remappingFunction) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V compute(K key, BiFunction<? super K, ? super @Nullable V, ? extends @Nullable V> remappingFunction) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends @Nullable V> remappingFunction) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().merge(key, value, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().putAll(map);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public V remove(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> values() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.values == null) {
                    this.values = Synchronized.collection(this.delegate().values(), this.mutex);
                }
                return this.values;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }
    }

    private static class SynchronizedAsMapEntries<K, V>
    extends SynchronizedSet<Map.Entry<K, Collection<V>>> {
        private static final long serialVersionUID = 0L;

        SynchronizedAsMapEntries(Set<Map.Entry<K, Collection<V>>> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        public Iterator<Map.Entry<K, Collection<V>>> iterator() {
            return new TransformedIterator<Map.Entry<K, Collection<V>>, Map.Entry<K, Collection<V>>>(super.iterator()){

                @Override
                Map.Entry<K, Collection<V>> transform(final Map.Entry<K, Collection<V>> entry) {
                    return new ForwardingMapEntry<K, Collection<V>>(){

                        @Override
                        protected Map.Entry<K, Collection<V>> delegate() {
                            return entry;
                        }

                        @Override
                        public Collection<V> getValue() {
                            return Synchronized.typePreservingCollection((Collection)entry.getValue(), mutex);
                        }
                    };
                }
            };
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public @Nullable Object[] toArray() {
            Object object = this.mutex;
            synchronized (object) {
                return ObjectArrays.toArrayImpl(this.delegate());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] array) {
            Object object = this.mutex;
            synchronized (object) {
                return ObjectArrays.toArrayImpl(this.delegate(), array);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return Maps.containsEntryImpl(this.delegate(), o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Object object = this.mutex;
            synchronized (object) {
                return Collections2.containsAllImpl(this.delegate(), c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return Sets.equalsImpl(this.delegate(), o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return Maps.removeEntryImpl(this.delegate(), o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Object object = this.mutex;
            synchronized (object) {
                return Iterators.removeAll(this.delegate().iterator(), c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Object object = this.mutex;
            synchronized (object) {
                return Iterators.retainAll(this.delegate().iterator(), c);
            }
        }
    }

    private static class SynchronizedSortedSetMultimap<K, V>
    extends SynchronizedSetMultimap<K, V>
    implements SortedSetMultimap<K, V> {
        private static final long serialVersionUID = 0L;

        SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        SortedSetMultimap<K, V> delegate() {
            return (SortedSetMultimap)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedSet<V> get(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedSet((SortedSet)this.delegate().get((Object)key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedSet<V> removeAll(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeAll(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedSet<V> replaceValues(K key, Iterable<? extends V> values) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().replaceValues((Object)key, (Iterable)values);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Comparator<? super V> valueComparator() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().valueComparator();
            }
        }
    }

    private static class SynchronizedSetMultimap<K, V>
    extends SynchronizedMultimap<K, V>
    implements SetMultimap<K, V> {
        @CheckForNull
        transient Set<Map.Entry<K, V>> entrySet;
        private static final long serialVersionUID = 0L;

        SynchronizedSetMultimap(SetMultimap<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        SetMultimap<K, V> delegate() {
            return (SetMultimap)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<V> get(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.set(this.delegate().get((Object)key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<V> removeAll(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeAll(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<V> replaceValues(K key, Iterable<? extends V> values) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().replaceValues((Object)key, (Iterable)values);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Map.Entry<K, V>> entries() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entries(), this.mutex);
                }
                return this.entrySet;
            }
        }
    }

    private static class SynchronizedListMultimap<K, V>
    extends SynchronizedMultimap<K, V>
    implements ListMultimap<K, V> {
        private static final long serialVersionUID = 0L;

        SynchronizedListMultimap(ListMultimap<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        ListMultimap<K, V> delegate() {
            return (ListMultimap)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<V> get(K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.list((List)this.delegate().get((Object)key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<V> removeAll(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeAll(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<V> replaceValues(K key, Iterable<? extends V> values) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().replaceValues((Object)key, (Iterable)values);
            }
        }
    }

    private static class SynchronizedMultimap<K, V>
    extends SynchronizedObject
    implements Multimap<K, V> {
        @CheckForNull
        transient Set<K> keySet;
        @CheckForNull
        transient Collection<V> valuesCollection;
        @CheckForNull
        transient Collection<Map.Entry<K, V>> entries;
        @CheckForNull
        transient Map<K, Collection<V>> asMap;
        @CheckForNull
        transient Multiset<K> keys;
        private static final long serialVersionUID = 0L;

        @Override
        Multimap<K, V> delegate() {
            return (Multimap)super.delegate();
        }

        SynchronizedMultimap(Multimap<K, V> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsKey(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(@CheckForNull Object value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsValue(value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsEntry(@CheckForNull Object key, @CheckForNull Object value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsEntry(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> get(@ParametricNullness K key) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.typePreservingCollection(this.delegate().get(key), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().put(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean putAll(@ParametricNullness K key, Iterable<? extends V> values) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().putAll(key, values);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().putAll(multimap);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().replaceValues(key, values);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> removeAll(@CheckForNull Object key) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeAll(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<K> keySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.keySet == null) {
                    this.keySet = Synchronized.typePreservingSet(this.delegate().keySet(), this.mutex);
                }
                return this.keySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> values() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.valuesCollection == null) {
                    this.valuesCollection = Synchronized.collection(this.delegate().values(), this.mutex);
                }
                return this.valuesCollection;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<Map.Entry<K, V>> entries() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = Synchronized.typePreservingCollection(this.delegate().entries(), this.mutex);
                }
                return this.entries;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<K, Collection<V>> asMap() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.asMap == null) {
                    this.asMap = new SynchronizedAsMap(this.delegate().asMap(), this.mutex);
                }
                return this.asMap;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Multiset<K> keys() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = Synchronized.multiset(this.delegate().keys(), this.mutex);
                }
                return this.keys;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }
    }

    private static class SynchronizedMultiset<E>
    extends SynchronizedCollection<E>
    implements Multiset<E> {
        @CheckForNull
        transient Set<E> elementSet;
        @CheckForNull
        transient Set<Multiset.Entry<E>> entrySet;
        private static final long serialVersionUID = 0L;

        SynchronizedMultiset(Multiset<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Multiset<E> delegate() {
            return (Multiset)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int count(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().count(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int add(@ParametricNullness E e, int n) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().add(e, n);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int remove(@CheckForNull Object o, int n) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(o, n);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int setCount(@ParametricNullness E element, int count) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().setCount(element, count);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean setCount(@ParametricNullness E element, int oldCount, int newCount) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().setCount(element, oldCount, newCount);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<E> elementSet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.elementSet == null) {
                    this.elementSet = Synchronized.typePreservingSet(this.delegate().elementSet(), this.mutex);
                }
                return this.elementSet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Multiset.Entry<E>> entrySet() {
            Object object = this.mutex;
            synchronized (object) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.typePreservingSet(this.delegate().entrySet(), this.mutex);
                }
                return this.entrySet;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }
    }

    private static class SynchronizedRandomAccessList<E>
    extends SynchronizedList<E>
    implements RandomAccess {
        private static final long serialVersionUID = 0L;

        SynchronizedRandomAccessList(List<E> list, @CheckForNull Object mutex) {
            super(list, mutex);
        }
    }

    private static class SynchronizedList<E>
    extends SynchronizedCollection<E>
    implements List<E> {
        private static final long serialVersionUID = 0L;

        SynchronizedList(List<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        List<E> delegate() {
            return (List)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int index, E element) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().add(index, element);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E get(int index) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().get(index);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().indexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().lastIndexOf(o);
            }
        }

        @Override
        public ListIterator<E> listIterator() {
            return this.delegate().listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return this.delegate().listIterator(index);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E remove(int index) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(index);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E set(int index, E element) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().set(index, element);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sort(@Nullable Comparator<? super E> c) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().sort(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.list(this.delegate().subList(fromIndex, toIndex), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }
    }

    static class SynchronizedSortedSet<E>
    extends SynchronizedSet<E>
    implements SortedSet<E> {
        private static final long serialVersionUID = 0L;

        SynchronizedSortedSet(SortedSet<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        SortedSet<E> delegate() {
            return (SortedSet)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @CheckForNull
        public Comparator<? super E> comparator() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().comparator();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedSet(this.delegate().subSet(fromElement, toElement), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedSet<E> headSet(E toElement) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedSet(this.delegate().headSet(toElement), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SortedSet<E> tailSet(E fromElement) {
            Object object = this.mutex;
            synchronized (object) {
                return Synchronized.sortedSet(this.delegate().tailSet(fromElement), this.mutex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E first() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E last() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().last();
            }
        }
    }

    static class SynchronizedSet<E>
    extends SynchronizedCollection<E>
    implements Set<E> {
        private static final long serialVersionUID = 0L;

        SynchronizedSet(Set<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Set<E> delegate() {
            return (Set)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().hashCode();
            }
        }
    }

    @VisibleForTesting
    static class SynchronizedCollection<E>
    extends SynchronizedObject
    implements Collection<E> {
        private static final long serialVersionUID = 0L;

        private SynchronizedCollection(Collection<E> delegate, @CheckForNull Object mutex) {
            super(delegate, mutex);
        }

        @Override
        Collection<E> delegate() {
            return (Collection)super.delegate();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(E e) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().add(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends E> c) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().contains(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().isEmpty();
            }
        }

        @Override
        public Iterator<E> iterator() {
            return this.delegate().iterator();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Spliterator<E> spliterator() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().spliterator();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Stream<E> stream() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().stream();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Stream<E> parallelStream() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().parallelStream();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(Consumer<? super E> action) {
            Object object = this.mutex;
            synchronized (object) {
                this.delegate().forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(@CheckForNull Object o) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().remove(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public @Nullable Object[] toArray() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().toArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate().toArray(a);
            }
        }
    }

    static class SynchronizedObject
    implements Serializable {
        final Object delegate;
        final Object mutex;
        @GwtIncompatible
        @J2ktIncompatible
        private static final long serialVersionUID = 0L;

        SynchronizedObject(Object delegate, @CheckForNull Object mutex) {
            this.delegate = Preconditions.checkNotNull(delegate);
            this.mutex = mutex == null ? this : mutex;
        }

        Object delegate() {
            return this.delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.mutex;
            synchronized (object) {
                return this.delegate.toString();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @GwtIncompatible
        @J2ktIncompatible
        private void writeObject(ObjectOutputStream stream) throws IOException {
            Object object = this.mutex;
            synchronized (object) {
                stream.defaultWriteObject();
            }
        }
    }
}

