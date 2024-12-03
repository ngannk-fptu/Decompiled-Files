/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.CollectSpliterators;
import com.google.common.collect.Collections2;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.NullnessCasts;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMapBasedMultimap<K, V>
extends AbstractMultimap<K, V>
implements Serializable {
    private transient Map<K, Collection<V>> map;
    private transient int totalSize;
    private static final long serialVersionUID = 2447537837011683357L;

    protected AbstractMapBasedMultimap(Map<K, Collection<V>> map) {
        Preconditions.checkArgument(map.isEmpty());
        this.map = map;
    }

    final void setMap(Map<K, Collection<V>> map) {
        this.map = map;
        this.totalSize = 0;
        for (Collection<Collection<V>> values : map.values()) {
            Preconditions.checkArgument(!values.isEmpty());
            this.totalSize += values.size();
        }
    }

    Collection<V> createUnmodifiableEmptyCollection() {
        return this.unmodifiableCollectionSubclass(this.createCollection());
    }

    abstract Collection<V> createCollection();

    Collection<V> createCollection(@ParametricNullness K key) {
        return this.createCollection();
    }

    Map<K, Collection<V>> backingMap() {
        return this.map;
    }

    @Override
    public int size() {
        return this.totalSize;
    }

    @Override
    public boolean containsKey(@CheckForNull Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
        Collection<V> collection = this.map.get(key);
        if (collection == null) {
            collection = this.createCollection(key);
            if (collection.add(value)) {
                ++this.totalSize;
                this.map.put(key, collection);
                return true;
            }
            throw new AssertionError((Object)"New Collection violated the Collection spec");
        }
        if (collection.add(value)) {
            ++this.totalSize;
            return true;
        }
        return false;
    }

    private Collection<V> getOrCreateCollection(@ParametricNullness K key) {
        Collection<V> collection = this.map.get(key);
        if (collection == null) {
            collection = this.createCollection(key);
            this.map.put(key, collection);
        }
        return collection;
    }

    @Override
    public Collection<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
        Iterator<V> iterator = values.iterator();
        if (!iterator.hasNext()) {
            return this.removeAll(key);
        }
        Collection<V> collection = this.getOrCreateCollection(key);
        Collection<V> oldValues = this.createCollection();
        oldValues.addAll(collection);
        this.totalSize -= collection.size();
        collection.clear();
        while (iterator.hasNext()) {
            if (!collection.add(iterator.next())) continue;
            ++this.totalSize;
        }
        return this.unmodifiableCollectionSubclass(oldValues);
    }

    @Override
    public Collection<V> removeAll(@CheckForNull Object key) {
        Collection<V> collection = this.map.remove(key);
        if (collection == null) {
            return this.createUnmodifiableEmptyCollection();
        }
        Collection<V> output = this.createCollection();
        output.addAll(collection);
        this.totalSize -= collection.size();
        collection.clear();
        return this.unmodifiableCollectionSubclass(output);
    }

    <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    @Override
    public void clear() {
        for (Collection<V> collection : this.map.values()) {
            collection.clear();
        }
        this.map.clear();
        this.totalSize = 0;
    }

    @Override
    public Collection<V> get(@ParametricNullness K key) {
        Collection<V> collection = this.map.get(key);
        if (collection == null) {
            collection = this.createCollection(key);
        }
        return this.wrapCollection(key, collection);
    }

    Collection<V> wrapCollection(@ParametricNullness K key, Collection<V> collection) {
        return new WrappedCollection(key, collection, null);
    }

    final List<V> wrapList(@ParametricNullness K key, List<V> list, @CheckForNull WrappedCollection ancestor) {
        return list instanceof RandomAccess ? new RandomAccessWrappedList(this, key, list, ancestor) : new WrappedList(key, list, ancestor);
    }

    private static <E> Iterator<E> iteratorOrListIterator(Collection<E> collection) {
        return collection instanceof List ? ((List)collection).listIterator() : collection.iterator();
    }

    @Override
    Set<K> createKeySet() {
        return new KeySet(this.map);
    }

    final Set<K> createMaybeNavigableKeySet() {
        if (this.map instanceof NavigableMap) {
            return new NavigableKeySet((NavigableMap)this.map);
        }
        if (this.map instanceof SortedMap) {
            return new SortedKeySet((SortedMap)this.map);
        }
        return new KeySet(this.map);
    }

    private void removeValuesForKey(@CheckForNull Object key) {
        Collection<V> collection = Maps.safeRemove(this.map, key);
        if (collection != null) {
            int count = collection.size();
            collection.clear();
            this.totalSize -= count;
        }
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    Collection<V> createValues() {
        return new AbstractMultimap.Values(this);
    }

    @Override
    Iterator<V> valueIterator() {
        return new Itr<V>(this){

            @Override
            @ParametricNullness
            V output(@ParametricNullness K key, @ParametricNullness V value) {
                return value;
            }
        };
    }

    @Override
    Spliterator<V> valueSpliterator() {
        return CollectSpliterators.flatMap(this.map.values().spliterator(), Collection::spliterator, 64, this.size());
    }

    @Override
    Multiset<K> createKeys() {
        return new Multimaps.Keys(this);
    }

    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return super.entries();
    }

    @Override
    Collection<Map.Entry<K, V>> createEntries() {
        if (this instanceof SetMultimap) {
            return new AbstractMultimap.EntrySet(this);
        }
        return new AbstractMultimap.Entries(this);
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new Itr<Map.Entry<K, V>>(this){

            @Override
            Map.Entry<K, V> output(@ParametricNullness K key, @ParametricNullness V value) {
                return Maps.immutableEntry(key, value);
            }
        };
    }

    @Override
    Spliterator<Map.Entry<K, V>> entrySpliterator() {
        return CollectSpliterators.flatMap(this.map.entrySet().spliterator(), keyToValueCollectionEntry -> {
            Object key = keyToValueCollectionEntry.getKey();
            Collection valueCollection = (Collection)keyToValueCollectionEntry.getValue();
            return CollectSpliterators.map(valueCollection.spliterator(), value -> Maps.immutableEntry(key, value));
        }, 64, this.size());
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        this.map.forEach((key, valueCollection) -> valueCollection.forEach((? super T value) -> action.accept((Object)key, (Object)value)));
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return new AsMap(this.map);
    }

    final Map<K, Collection<V>> createMaybeNavigableAsMap() {
        if (this.map instanceof NavigableMap) {
            return new NavigableAsMap((NavigableMap)this.map);
        }
        if (this.map instanceof SortedMap) {
            return new SortedAsMap((SortedMap)this.map);
        }
        return new AsMap(this.map);
    }

    class NavigableAsMap
    extends SortedAsMap
    implements NavigableMap<K, Collection<V>> {
        NavigableAsMap(NavigableMap<K, Collection<V>> submap) {
            super(submap);
        }

        NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap)super.sortedMap();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> lowerEntry(@ParametricNullness K key) {
            Map.Entry entry = this.sortedMap().lowerEntry(key);
            return entry == null ? null : this.wrapEntry(entry);
        }

        @Override
        @CheckForNull
        public K lowerKey(@ParametricNullness K key) {
            return this.sortedMap().lowerKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> floorEntry(@ParametricNullness K key) {
            Map.Entry entry = this.sortedMap().floorEntry(key);
            return entry == null ? null : this.wrapEntry(entry);
        }

        @Override
        @CheckForNull
        public K floorKey(@ParametricNullness K key) {
            return this.sortedMap().floorKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> ceilingEntry(@ParametricNullness K key) {
            Map.Entry entry = this.sortedMap().ceilingEntry(key);
            return entry == null ? null : this.wrapEntry(entry);
        }

        @Override
        @CheckForNull
        public K ceilingKey(@ParametricNullness K key) {
            return this.sortedMap().ceilingKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> higherEntry(@ParametricNullness K key) {
            Map.Entry entry = this.sortedMap().higherEntry(key);
            return entry == null ? null : this.wrapEntry(entry);
        }

        @Override
        @CheckForNull
        public K higherKey(@ParametricNullness K key) {
            return this.sortedMap().higherKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> firstEntry() {
            Map.Entry entry = this.sortedMap().firstEntry();
            return entry == null ? null : this.wrapEntry(entry);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> lastEntry() {
            Map.Entry entry = this.sortedMap().lastEntry();
            return entry == null ? null : this.wrapEntry(entry);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> pollFirstEntry() {
            return this.pollAsMapEntry(this.entrySet().iterator());
        }

        @Override
        @CheckForNull
        public Map.Entry<K, Collection<V>> pollLastEntry() {
            return this.pollAsMapEntry(this.descendingMap().entrySet().iterator());
        }

        @CheckForNull
        Map.Entry<K, Collection<V>> pollAsMapEntry(Iterator<Map.Entry<K, Collection<V>>> entryIterator) {
            if (!entryIterator.hasNext()) {
                return null;
            }
            Map.Entry entry = entryIterator.next();
            Collection output = AbstractMapBasedMultimap.this.createCollection();
            output.addAll(entry.getValue());
            entryIterator.remove();
            return Maps.immutableEntry(entry.getKey(), AbstractMapBasedMultimap.this.unmodifiableCollectionSubclass(output));
        }

        @Override
        public NavigableMap<K, Collection<V>> descendingMap() {
            return new NavigableAsMap(this.sortedMap().descendingMap());
        }

        @Override
        public NavigableSet<K> keySet() {
            return (NavigableSet)super.keySet();
        }

        @Override
        NavigableSet<K> createKeySet() {
            return new NavigableKeySet(this.sortedMap());
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return this.keySet();
        }

        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.descendingMap().navigableKeySet();
        }

        @Override
        public NavigableMap<K, Collection<V>> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return this.subMap(fromKey, true, toKey, false);
        }

        @Override
        public NavigableMap<K, Collection<V>> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
            return new NavigableAsMap(this.sortedMap().subMap(fromKey, fromInclusive, toKey, toInclusive));
        }

        @Override
        public NavigableMap<K, Collection<V>> headMap(@ParametricNullness K toKey) {
            return this.headMap(toKey, false);
        }

        @Override
        public NavigableMap<K, Collection<V>> headMap(@ParametricNullness K toKey, boolean inclusive) {
            return new NavigableAsMap(this.sortedMap().headMap(toKey, inclusive));
        }

        @Override
        public NavigableMap<K, Collection<V>> tailMap(@ParametricNullness K fromKey) {
            return this.tailMap(fromKey, true);
        }

        @Override
        public NavigableMap<K, Collection<V>> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
            return new NavigableAsMap(this.sortedMap().tailMap(fromKey, inclusive));
        }
    }

    private class SortedAsMap
    extends AsMap
    implements SortedMap<K, Collection<V>> {
        @CheckForNull
        SortedSet<K> sortedKeySet;

        SortedAsMap(SortedMap<K, Collection<V>> submap) {
            super(submap);
        }

        SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap)this.submap;
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }

        @Override
        @ParametricNullness
        public K firstKey() {
            return this.sortedMap().firstKey();
        }

        @Override
        @ParametricNullness
        public K lastKey() {
            return this.sortedMap().lastKey();
        }

        @Override
        public SortedMap<K, Collection<V>> headMap(@ParametricNullness K toKey) {
            return new SortedAsMap(this.sortedMap().headMap(toKey));
        }

        @Override
        public SortedMap<K, Collection<V>> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return new SortedAsMap(this.sortedMap().subMap(fromKey, toKey));
        }

        @Override
        public SortedMap<K, Collection<V>> tailMap(@ParametricNullness K fromKey) {
            return new SortedAsMap(this.sortedMap().tailMap(fromKey));
        }

        @Override
        public SortedSet<K> keySet() {
            SortedSet result = this.sortedKeySet;
            return result == null ? (this.sortedKeySet = this.createKeySet()) : result;
        }

        @Override
        SortedSet<K> createKeySet() {
            return new SortedKeySet(this.sortedMap());
        }
    }

    private class AsMap
    extends Maps.ViewCachingAbstractMap<K, Collection<V>> {
        final transient Map<K, Collection<V>> submap;

        AsMap(Map<K, Collection<V>> submap) {
            this.submap = submap;
        }

        @Override
        protected Set<Map.Entry<K, Collection<V>>> createEntrySet() {
            return new AsMapEntries();
        }

        @Override
        public boolean containsKey(@CheckForNull Object key) {
            return Maps.safeContainsKey(this.submap, key);
        }

        @Override
        @CheckForNull
        public Collection<V> get(@CheckForNull Object key) {
            Collection collection = Maps.safeGet(this.submap, key);
            if (collection == null) {
                return null;
            }
            Object k = key;
            return AbstractMapBasedMultimap.this.wrapCollection(k, collection);
        }

        @Override
        public Set<K> keySet() {
            return AbstractMapBasedMultimap.this.keySet();
        }

        @Override
        public int size() {
            return this.submap.size();
        }

        @Override
        @CheckForNull
        public Collection<V> remove(@CheckForNull Object key) {
            Collection collection = this.submap.remove(key);
            if (collection == null) {
                return null;
            }
            Collection output = AbstractMapBasedMultimap.this.createCollection();
            output.addAll(collection);
            AbstractMapBasedMultimap.this.totalSize -= collection.size();
            collection.clear();
            return output;
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            return this == object || this.submap.equals(object);
        }

        @Override
        public int hashCode() {
            return this.submap.hashCode();
        }

        @Override
        public String toString() {
            return this.submap.toString();
        }

        @Override
        public void clear() {
            if (this.submap == AbstractMapBasedMultimap.this.map) {
                AbstractMapBasedMultimap.this.clear();
            } else {
                Iterators.clear(new AsMapIterator());
            }
        }

        Map.Entry<K, Collection<V>> wrapEntry(Map.Entry<K, Collection<V>> entry) {
            Object key = entry.getKey();
            return Maps.immutableEntry(key, AbstractMapBasedMultimap.this.wrapCollection(key, entry.getValue()));
        }

        class AsMapIterator
        implements Iterator<Map.Entry<K, Collection<V>>> {
            final Iterator<Map.Entry<K, Collection<V>>> delegateIterator;
            @CheckForNull
            Collection<V> collection;

            AsMapIterator() {
                this.delegateIterator = AsMap.this.submap.entrySet().iterator();
            }

            @Override
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            @Override
            public Map.Entry<K, Collection<V>> next() {
                Map.Entry entry = this.delegateIterator.next();
                this.collection = entry.getValue();
                return AsMap.this.wrapEntry(entry);
            }

            @Override
            public void remove() {
                Preconditions.checkState(this.collection != null, "no calls to next() since the last call to remove()");
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.this.totalSize -= this.collection.size();
                this.collection.clear();
                this.collection = null;
            }
        }

        class AsMapEntries
        extends Maps.EntrySet<K, Collection<V>> {
            AsMapEntries() {
            }

            @Override
            Map<K, Collection<V>> map() {
                return AsMap.this;
            }

            @Override
            public Iterator<Map.Entry<K, Collection<V>>> iterator() {
                return new AsMapIterator();
            }

            @Override
            public Spliterator<Map.Entry<K, Collection<V>>> spliterator() {
                return CollectSpliterators.map(AsMap.this.submap.entrySet().spliterator(), AsMap.this::wrapEntry);
            }

            @Override
            public boolean contains(@CheckForNull Object o) {
                return Collections2.safeContains(AsMap.this.submap.entrySet(), o);
            }

            @Override
            public boolean remove(@CheckForNull Object o) {
                if (!this.contains(o)) {
                    return false;
                }
                Map.Entry entry = Objects.requireNonNull((Map.Entry)o);
                AbstractMapBasedMultimap.this.removeValuesForKey(entry.getKey());
                return true;
            }
        }
    }

    private abstract class Itr<T>
    implements Iterator<T> {
        final Iterator<Map.Entry<K, Collection<V>>> keyIterator;
        @CheckForNull
        K key;
        @CheckForNull
        Collection<V> collection;
        Iterator<V> valueIterator;

        Itr() {
            this.keyIterator = AbstractMapBasedMultimap.this.map.entrySet().iterator();
            this.key = null;
            this.collection = null;
            this.valueIterator = Iterators.emptyModifiableIterator();
        }

        abstract T output(@ParametricNullness K var1, @ParametricNullness V var2);

        @Override
        public boolean hasNext() {
            return this.keyIterator.hasNext() || this.valueIterator.hasNext();
        }

        @Override
        @ParametricNullness
        public T next() {
            if (!this.valueIterator.hasNext()) {
                Map.Entry mapEntry = this.keyIterator.next();
                this.key = mapEntry.getKey();
                this.collection = mapEntry.getValue();
                this.valueIterator = this.collection.iterator();
            }
            return this.output(NullnessCasts.uncheckedCastNullableTToT(this.key), this.valueIterator.next());
        }

        @Override
        public void remove() {
            this.valueIterator.remove();
            if (Objects.requireNonNull(this.collection).isEmpty()) {
                this.keyIterator.remove();
            }
            AbstractMapBasedMultimap.this.totalSize--;
        }
    }

    class NavigableKeySet
    extends SortedKeySet
    implements NavigableSet<K> {
        NavigableKeySet(NavigableMap<K, Collection<V>> subMap) {
            super(subMap);
        }

        NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap)super.sortedMap();
        }

        @Override
        @CheckForNull
        public K lower(@ParametricNullness K k) {
            return this.sortedMap().lowerKey(k);
        }

        @Override
        @CheckForNull
        public K floor(@ParametricNullness K k) {
            return this.sortedMap().floorKey(k);
        }

        @Override
        @CheckForNull
        public K ceiling(@ParametricNullness K k) {
            return this.sortedMap().ceilingKey(k);
        }

        @Override
        @CheckForNull
        public K higher(@ParametricNullness K k) {
            return this.sortedMap().higherKey(k);
        }

        @Override
        @CheckForNull
        public K pollFirst() {
            return Iterators.pollNext(this.iterator());
        }

        @Override
        @CheckForNull
        public K pollLast() {
            return Iterators.pollNext(this.descendingIterator());
        }

        @Override
        public NavigableSet<K> descendingSet() {
            return new NavigableKeySet(this.sortedMap().descendingMap());
        }

        @Override
        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }

        @Override
        public NavigableSet<K> headSet(@ParametricNullness K toElement) {
            return this.headSet((K)toElement, false);
        }

        @Override
        public NavigableSet<K> headSet(@ParametricNullness K toElement, boolean inclusive) {
            return new NavigableKeySet(this.sortedMap().headMap(toElement, inclusive));
        }

        @Override
        public NavigableSet<K> subSet(@ParametricNullness K fromElement, @ParametricNullness K toElement) {
            return this.subSet((K)fromElement, true, (K)toElement, false);
        }

        @Override
        public NavigableSet<K> subSet(@ParametricNullness K fromElement, boolean fromInclusive, @ParametricNullness K toElement, boolean toInclusive) {
            return new NavigableKeySet(this.sortedMap().subMap(fromElement, fromInclusive, toElement, toInclusive));
        }

        @Override
        public NavigableSet<K> tailSet(@ParametricNullness K fromElement) {
            return this.tailSet((K)fromElement, true);
        }

        @Override
        public NavigableSet<K> tailSet(@ParametricNullness K fromElement, boolean inclusive) {
            return new NavigableKeySet(this.sortedMap().tailMap(fromElement, inclusive));
        }
    }

    private class SortedKeySet
    extends KeySet
    implements SortedSet<K> {
        SortedKeySet(SortedMap<K, Collection<V>> subMap) {
            super(subMap);
        }

        SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap)super.map();
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }

        @Override
        @ParametricNullness
        public K first() {
            return this.sortedMap().firstKey();
        }

        @Override
        public SortedSet<K> headSet(@ParametricNullness K toElement) {
            return new SortedKeySet(this.sortedMap().headMap(toElement));
        }

        @Override
        @ParametricNullness
        public K last() {
            return this.sortedMap().lastKey();
        }

        @Override
        public SortedSet<K> subSet(@ParametricNullness K fromElement, @ParametricNullness K toElement) {
            return new SortedKeySet(this.sortedMap().subMap(fromElement, toElement));
        }

        @Override
        public SortedSet<K> tailSet(@ParametricNullness K fromElement) {
            return new SortedKeySet(this.sortedMap().tailMap(fromElement));
        }
    }

    private class KeySet
    extends Maps.KeySet<K, Collection<V>> {
        KeySet(Map<K, Collection<V>> subMap) {
            super(subMap);
        }

        @Override
        public Iterator<K> iterator() {
            final Iterator entryIterator = this.map().entrySet().iterator();
            return new Iterator<K>(){
                @CheckForNull
                Map.Entry<K, Collection<V>> entry;

                @Override
                public boolean hasNext() {
                    return entryIterator.hasNext();
                }

                @Override
                @ParametricNullness
                public K next() {
                    this.entry = (Map.Entry)entryIterator.next();
                    return this.entry.getKey();
                }

                @Override
                public void remove() {
                    Preconditions.checkState(this.entry != null, "no calls to next() since the last call to remove()");
                    Collection collection = this.entry.getValue();
                    entryIterator.remove();
                    AbstractMapBasedMultimap.this.totalSize -= collection.size();
                    collection.clear();
                    this.entry = null;
                }
            };
        }

        @Override
        public Spliterator<K> spliterator() {
            return this.map().keySet().spliterator();
        }

        @Override
        public boolean remove(@CheckForNull Object key) {
            int count = 0;
            Collection collection = (Collection)this.map().remove(key);
            if (collection != null) {
                count = collection.size();
                collection.clear();
                AbstractMapBasedMultimap.this.totalSize -= count;
            }
            return count > 0;
        }

        @Override
        public void clear() {
            Iterators.clear(this.iterator());
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.map().keySet().containsAll(c);
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            return this == object || this.map().keySet().equals(object);
        }

        @Override
        public int hashCode() {
            return this.map().keySet().hashCode();
        }
    }

    private class RandomAccessWrappedList
    extends WrappedList
    implements RandomAccess {
        RandomAccessWrappedList(@ParametricNullness AbstractMapBasedMultimap abstractMapBasedMultimap, K key, @CheckForNull List<V> delegate, WrappedCollection ancestor) {
            super(key, delegate, ancestor);
        }
    }

    class WrappedList
    extends WrappedCollection
    implements List<V> {
        WrappedList(K key, @CheckForNull List<V> delegate, WrappedCollection ancestor) {
            super(key, delegate, ancestor);
        }

        List<V> getListDelegate() {
            return (List)this.getDelegate();
        }

        @Override
        public boolean addAll(int index, Collection<? extends V> c) {
            if (c.isEmpty()) {
                return false;
            }
            int oldSize = this.size();
            boolean changed = this.getListDelegate().addAll(index, c);
            if (changed) {
                int newSize = this.getDelegate().size();
                AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
                if (oldSize == 0) {
                    this.addToMap();
                }
            }
            return changed;
        }

        @Override
        @ParametricNullness
        public V get(int index) {
            this.refreshIfEmpty();
            return this.getListDelegate().get(index);
        }

        @Override
        @ParametricNullness
        public V set(int index, @ParametricNullness V element) {
            this.refreshIfEmpty();
            return this.getListDelegate().set(index, element);
        }

        @Override
        public void add(int index, @ParametricNullness V element) {
            this.refreshIfEmpty();
            boolean wasEmpty = this.getDelegate().isEmpty();
            this.getListDelegate().add(index, element);
            AbstractMapBasedMultimap.this.totalSize++;
            if (wasEmpty) {
                this.addToMap();
            }
        }

        @Override
        @ParametricNullness
        public V remove(int index) {
            this.refreshIfEmpty();
            Object value = this.getListDelegate().remove(index);
            AbstractMapBasedMultimap.this.totalSize--;
            this.removeIfEmpty();
            return value;
        }

        @Override
        public int indexOf(@CheckForNull Object o) {
            this.refreshIfEmpty();
            return this.getListDelegate().indexOf(o);
        }

        @Override
        public int lastIndexOf(@CheckForNull Object o) {
            this.refreshIfEmpty();
            return this.getListDelegate().lastIndexOf(o);
        }

        @Override
        public ListIterator<V> listIterator() {
            this.refreshIfEmpty();
            return new WrappedListIterator();
        }

        @Override
        public ListIterator<V> listIterator(int index) {
            this.refreshIfEmpty();
            return new WrappedListIterator(index);
        }

        @Override
        public List<V> subList(int fromIndex, int toIndex) {
            this.refreshIfEmpty();
            return AbstractMapBasedMultimap.this.wrapList(this.getKey(), this.getListDelegate().subList(fromIndex, toIndex), this.getAncestor() == null ? this : this.getAncestor());
        }

        /*
         * Signature claims super is com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection.WrappedIterator, not com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection$WrappedIterator - discarding signature.
         */
        private class WrappedListIterator
        extends WrappedCollection.WrappedIterator
        implements ListIterator {
            WrappedListIterator() {
            }

            public WrappedListIterator(int index) {
                super(WrappedList.this.getListDelegate().listIterator(index));
            }

            private ListIterator<V> getDelegateListIterator() {
                return (ListIterator)this.getDelegateIterator();
            }

            @Override
            public boolean hasPrevious() {
                return this.getDelegateListIterator().hasPrevious();
            }

            @ParametricNullness
            public V previous() {
                return this.getDelegateListIterator().previous();
            }

            @Override
            public int nextIndex() {
                return this.getDelegateListIterator().nextIndex();
            }

            @Override
            public int previousIndex() {
                return this.getDelegateListIterator().previousIndex();
            }

            public void set(@ParametricNullness V value) {
                this.getDelegateListIterator().set(value);
            }

            public void add(@ParametricNullness V value) {
                boolean wasEmpty = WrappedList.this.isEmpty();
                this.getDelegateListIterator().add(value);
                AbstractMapBasedMultimap.this.totalSize++;
                if (wasEmpty) {
                    WrappedList.this.addToMap();
                }
            }
        }
    }

    class WrappedNavigableSet
    extends WrappedSortedSet
    implements NavigableSet<V> {
        WrappedNavigableSet(K key, @CheckForNull NavigableSet<V> delegate, WrappedCollection ancestor) {
            super(key, delegate, ancestor);
        }

        NavigableSet<V> getSortedSetDelegate() {
            return (NavigableSet)super.getSortedSetDelegate();
        }

        @Override
        @CheckForNull
        public V lower(@ParametricNullness V v) {
            return this.getSortedSetDelegate().lower(v);
        }

        @Override
        @CheckForNull
        public V floor(@ParametricNullness V v) {
            return this.getSortedSetDelegate().floor(v);
        }

        @Override
        @CheckForNull
        public V ceiling(@ParametricNullness V v) {
            return this.getSortedSetDelegate().ceiling(v);
        }

        @Override
        @CheckForNull
        public V higher(@ParametricNullness V v) {
            return this.getSortedSetDelegate().higher(v);
        }

        @Override
        @CheckForNull
        public V pollFirst() {
            return Iterators.pollNext(this.iterator());
        }

        @Override
        @CheckForNull
        public V pollLast() {
            return Iterators.pollNext(this.descendingIterator());
        }

        private NavigableSet<V> wrap(NavigableSet<V> wrapped) {
            return new WrappedNavigableSet(this.key, wrapped, this.getAncestor() == null ? this : this.getAncestor());
        }

        @Override
        public NavigableSet<V> descendingSet() {
            return this.wrap(this.getSortedSetDelegate().descendingSet());
        }

        @Override
        public Iterator<V> descendingIterator() {
            return new WrappedCollection.WrappedIterator(this.getSortedSetDelegate().descendingIterator());
        }

        @Override
        public NavigableSet<V> subSet(@ParametricNullness V fromElement, boolean fromInclusive, @ParametricNullness V toElement, boolean toInclusive) {
            return this.wrap(this.getSortedSetDelegate().subSet(fromElement, fromInclusive, toElement, toInclusive));
        }

        @Override
        public NavigableSet<V> headSet(@ParametricNullness V toElement, boolean inclusive) {
            return this.wrap(this.getSortedSetDelegate().headSet(toElement, inclusive));
        }

        @Override
        public NavigableSet<V> tailSet(@ParametricNullness V fromElement, boolean inclusive) {
            return this.wrap(this.getSortedSetDelegate().tailSet(fromElement, inclusive));
        }
    }

    class WrappedSortedSet
    extends WrappedCollection
    implements SortedSet<V> {
        WrappedSortedSet(K key, @CheckForNull SortedSet<V> delegate, WrappedCollection ancestor) {
            super(key, delegate, ancestor);
        }

        SortedSet<V> getSortedSetDelegate() {
            return (SortedSet)this.getDelegate();
        }

        @Override
        @CheckForNull
        public Comparator<? super V> comparator() {
            return this.getSortedSetDelegate().comparator();
        }

        @Override
        @ParametricNullness
        public V first() {
            this.refreshIfEmpty();
            return this.getSortedSetDelegate().first();
        }

        @Override
        @ParametricNullness
        public V last() {
            this.refreshIfEmpty();
            return this.getSortedSetDelegate().last();
        }

        @Override
        public SortedSet<V> headSet(@ParametricNullness V toElement) {
            this.refreshIfEmpty();
            return new WrappedSortedSet(this.getKey(), this.getSortedSetDelegate().headSet(toElement), this.getAncestor() == null ? this : this.getAncestor());
        }

        @Override
        public SortedSet<V> subSet(@ParametricNullness V fromElement, @ParametricNullness V toElement) {
            this.refreshIfEmpty();
            return new WrappedSortedSet(this.getKey(), this.getSortedSetDelegate().subSet(fromElement, toElement), this.getAncestor() == null ? this : this.getAncestor());
        }

        @Override
        public SortedSet<V> tailSet(@ParametricNullness V fromElement) {
            this.refreshIfEmpty();
            return new WrappedSortedSet(this.getKey(), this.getSortedSetDelegate().tailSet(fromElement), this.getAncestor() == null ? this : this.getAncestor());
        }
    }

    class WrappedSet
    extends WrappedCollection
    implements Set<V> {
        WrappedSet(K key, Set<V> delegate) {
            super(key, delegate, null);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (c.isEmpty()) {
                return false;
            }
            int oldSize = this.size();
            boolean changed = Sets.removeAllImpl((Set)this.delegate, c);
            if (changed) {
                int newSize = this.delegate.size();
                AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
                this.removeIfEmpty();
            }
            return changed;
        }
    }

    class WrappedCollection
    extends AbstractCollection<V> {
        @ParametricNullness
        final K key;
        Collection<V> delegate;
        @CheckForNull
        final WrappedCollection ancestor;
        @CheckForNull
        final Collection<V> ancestorDelegate;

        WrappedCollection(K key, @CheckForNull Collection<V> delegate, WrappedCollection ancestor) {
            this.key = key;
            this.delegate = delegate;
            this.ancestor = ancestor;
            this.ancestorDelegate = ancestor == null ? null : ancestor.getDelegate();
        }

        void refreshIfEmpty() {
            Collection newDelegate;
            if (this.ancestor != null) {
                this.ancestor.refreshIfEmpty();
                if (this.ancestor.getDelegate() != this.ancestorDelegate) {
                    throw new ConcurrentModificationException();
                }
            } else if (this.delegate.isEmpty() && (newDelegate = (Collection)AbstractMapBasedMultimap.this.map.get(this.key)) != null) {
                this.delegate = newDelegate;
            }
        }

        void removeIfEmpty() {
            if (this.ancestor != null) {
                this.ancestor.removeIfEmpty();
            } else if (this.delegate.isEmpty()) {
                AbstractMapBasedMultimap.this.map.remove(this.key);
            }
        }

        @ParametricNullness
        K getKey() {
            return this.key;
        }

        void addToMap() {
            if (this.ancestor != null) {
                this.ancestor.addToMap();
            } else {
                AbstractMapBasedMultimap.this.map.put(this.key, this.delegate);
            }
        }

        @Override
        public int size() {
            this.refreshIfEmpty();
            return this.delegate.size();
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object == this) {
                return true;
            }
            this.refreshIfEmpty();
            return this.delegate.equals(object);
        }

        @Override
        public int hashCode() {
            this.refreshIfEmpty();
            return this.delegate.hashCode();
        }

        @Override
        public String toString() {
            this.refreshIfEmpty();
            return this.delegate.toString();
        }

        Collection<V> getDelegate() {
            return this.delegate;
        }

        @Override
        public Iterator<V> iterator() {
            this.refreshIfEmpty();
            return new WrappedIterator();
        }

        @Override
        public Spliterator<V> spliterator() {
            this.refreshIfEmpty();
            return this.delegate.spliterator();
        }

        @Override
        public boolean add(@ParametricNullness V value) {
            this.refreshIfEmpty();
            boolean wasEmpty = this.delegate.isEmpty();
            boolean changed = this.delegate.add(value);
            if (changed) {
                AbstractMapBasedMultimap.this.totalSize++;
                if (wasEmpty) {
                    this.addToMap();
                }
            }
            return changed;
        }

        @CheckForNull
        WrappedCollection getAncestor() {
            return this.ancestor;
        }

        @Override
        public boolean addAll(Collection<? extends V> collection) {
            if (collection.isEmpty()) {
                return false;
            }
            int oldSize = this.size();
            boolean changed = this.delegate.addAll(collection);
            if (changed) {
                int newSize = this.delegate.size();
                AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
                if (oldSize == 0) {
                    this.addToMap();
                }
            }
            return changed;
        }

        @Override
        public boolean contains(@CheckForNull Object o) {
            this.refreshIfEmpty();
            return this.delegate.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            this.refreshIfEmpty();
            return this.delegate.containsAll(c);
        }

        @Override
        public void clear() {
            int oldSize = this.size();
            if (oldSize == 0) {
                return;
            }
            this.delegate.clear();
            AbstractMapBasedMultimap.this.totalSize -= oldSize;
            this.removeIfEmpty();
        }

        @Override
        public boolean remove(@CheckForNull Object o) {
            this.refreshIfEmpty();
            boolean changed = this.delegate.remove(o);
            if (changed) {
                AbstractMapBasedMultimap.this.totalSize--;
                this.removeIfEmpty();
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (c.isEmpty()) {
                return false;
            }
            int oldSize = this.size();
            boolean changed = this.delegate.removeAll(c);
            if (changed) {
                int newSize = this.delegate.size();
                AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
                this.removeIfEmpty();
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Preconditions.checkNotNull(c);
            int oldSize = this.size();
            boolean changed = this.delegate.retainAll(c);
            if (changed) {
                int newSize = this.delegate.size();
                AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
                this.removeIfEmpty();
            }
            return changed;
        }

        class WrappedIterator
        implements Iterator<V> {
            final Iterator<V> delegateIterator;
            final Collection<V> originalDelegate;

            WrappedIterator() {
                this.originalDelegate = WrappedCollection.this.delegate;
                this.delegateIterator = AbstractMapBasedMultimap.iteratorOrListIterator(WrappedCollection.this.delegate);
            }

            WrappedIterator(Iterator<V> delegateIterator) {
                this.originalDelegate = WrappedCollection.this.delegate;
                this.delegateIterator = delegateIterator;
            }

            void validateIterator() {
                WrappedCollection.this.refreshIfEmpty();
                if (WrappedCollection.this.delegate != this.originalDelegate) {
                    throw new ConcurrentModificationException();
                }
            }

            @Override
            public boolean hasNext() {
                this.validateIterator();
                return this.delegateIterator.hasNext();
            }

            @Override
            @ParametricNullness
            public V next() {
                this.validateIterator();
                return this.delegateIterator.next();
            }

            @Override
            public void remove() {
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.this.totalSize--;
                WrappedCollection.this.removeIfEmpty();
            }

            Iterator<V> getDelegateIterator() {
                this.validateIterator();
                return this.delegateIterator;
            }
        }
    }
}

