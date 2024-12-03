/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  com.google.j2objc.annotations.RetainedWith
 *  com.google.j2objc.annotations.Weak
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.AbstractNavigableMap;
import com.google.common.collect.BiMap;
import com.google.common.collect.BoundType;
import com.google.common.collect.CollectCollectors;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.CollectSpliterators;
import com.google.common.collect.Collections2;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingNavigableSet;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ForwardingSortedMap;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.ImmutableEntry;
import com.google.common.collect.ImmutableEnumMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.NullnessCasts;
import com.google.common.collect.Ordering;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedMapDifference;
import com.google.common.collect.Synchronized;
import com.google.common.collect.TransformedIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collector;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
public final class Maps {
    private Maps() {
    }

    static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
        return EntryFunction.KEY;
    }

    static <V> Function<Map.Entry<?, V>, V> valueFunction() {
        return EntryFunction.VALUE;
    }

    static <K, V> Iterator<K> keyIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        return new TransformedIterator<Map.Entry<K, V>, K>(entryIterator){

            @Override
            @ParametricNullness
            K transform(Map.Entry<K, V> entry) {
                return entry.getKey();
            }
        };
    }

    static <K, V> Iterator<V> valueIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        return new TransformedIterator<Map.Entry<K, V>, V>(entryIterator){

            @Override
            @ParametricNullness
            V transform(Map.Entry<K, V> entry) {
                return entry.getValue();
            }
        };
    }

    @GwtCompatible(serializable=true)
    @J2ktIncompatible
    public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
        if (map instanceof ImmutableEnumMap) {
            ImmutableEnumMap result = (ImmutableEnumMap)map;
            return result;
        }
        Iterator<Map.Entry<K, V>> entryItr = map.entrySet().iterator();
        if (!entryItr.hasNext()) {
            return ImmutableMap.of();
        }
        Map.Entry<K, V> entry1 = entryItr.next();
        Enum key1 = (Enum)entry1.getKey();
        V value1 = entry1.getValue();
        CollectPreconditions.checkEntryNotNull(key1, value1);
        EnumMap<Enum, V> enumMap = new EnumMap<Enum, V>(Collections.singletonMap(key1, value1));
        while (entryItr.hasNext()) {
            Map.Entry<K, V> entry = entryItr.next();
            Enum key = (Enum)entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            enumMap.put(key, value);
        }
        return ImmutableEnumMap.asImmutable(enumMap);
    }

    @J2ktIncompatible
    public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(java.util.function.Function<? super T, ? extends K> keyFunction, java.util.function.Function<? super T, ? extends V> valueFunction) {
        return CollectCollectors.toImmutableEnumMap(keyFunction, valueFunction);
    }

    @J2ktIncompatible
    public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(java.util.function.Function<? super T, ? extends K> keyFunction, java.util.function.Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
        return CollectCollectors.toImmutableEnumMap(keyFunction, valueFunction, mergeFunction);
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap();
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap(Maps.capacity(expectedSize));
    }

    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize < 0x40000000) {
            return (int)Math.ceil((double)expectedSize / 0.75);
        }
        return Integer.MAX_VALUE;
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<K, V>(map);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
        return new LinkedHashMap(Maps.capacity(expectedSize));
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap();
    }

    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
        return new TreeMap<K, V>(map);
    }

    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@CheckForNull Comparator<C> comparator) {
        return new TreeMap(comparator);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return new EnumMap(Preconditions.checkNotNull(type));
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<K, V>(map);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap();
    }

    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        if (left instanceof SortedMap) {
            SortedMap sortedLeft = (SortedMap)left;
            return Maps.difference(sortedLeft, right);
        }
        return Maps.difference(left, right, Equivalence.equals());
    }

    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super @NonNull V> valueEquivalence) {
        Preconditions.checkNotNull(valueEquivalence);
        LinkedHashMap<K, V> onlyOnLeft = Maps.newLinkedHashMap();
        LinkedHashMap<? extends K, ? extends V> onlyOnRight = new LinkedHashMap<K, V>(right);
        LinkedHashMap<K, V> onBoth = Maps.newLinkedHashMap();
        LinkedHashMap<K, V> differences = Maps.newLinkedHashMap();
        Maps.doDifference(left, right, valueEquivalence, onlyOnLeft, onlyOnRight, onBoth, differences);
        return new MapDifferenceImpl<K, V>(onlyOnLeft, onlyOnRight, onBoth, differences);
    }

    public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        Comparator<K> comparator = Maps.orNaturalOrder(left.comparator());
        TreeMap<K, V> onlyOnLeft = Maps.newTreeMap(comparator);
        TreeMap<? extends K, ? extends V> onlyOnRight = Maps.newTreeMap(comparator);
        onlyOnRight.putAll(right);
        TreeMap<K, V> onBoth = Maps.newTreeMap(comparator);
        TreeMap<K, V> differences = Maps.newTreeMap(comparator);
        Maps.doDifference(left, right, Equivalence.equals(), onlyOnLeft, onlyOnRight, onBoth, differences);
        return new SortedMapDifferenceImpl<K, V>(onlyOnLeft, onlyOnRight, onBoth, differences);
    }

    private static <K, V> void doDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super @NonNull V> valueEquivalence, Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences) {
        for (Map.Entry<K, V> entry : left.entrySet()) {
            K leftKey = entry.getKey();
            V leftValue = entry.getValue();
            if (right.containsKey(leftKey)) {
                V rightValue = NullnessCasts.uncheckedCastNullableTToT(onlyOnRight.remove(leftKey));
                if (valueEquivalence.equivalent(leftValue, rightValue)) {
                    onBoth.put(leftKey, leftValue);
                    continue;
                }
                differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
                continue;
            }
            onlyOnLeft.put(leftKey, leftValue);
        }
    }

    private static <K, V> Map<K, V> unmodifiableMap(Map<K, ? extends V> map) {
        if (map instanceof SortedMap) {
            return Collections.unmodifiableSortedMap((SortedMap)map);
        }
        return Collections.unmodifiableMap(map);
    }

    static <E> Comparator<? super E> orNaturalOrder(@CheckForNull Comparator<? super E> comparator) {
        if (comparator != null) {
            return comparator;
        }
        return Ordering.natural();
    }

    public static <K, V> Map<K, V> asMap(Set<K> set, Function<? super K, V> function) {
        return new AsMapView<K, V>(set, function);
    }

    public static <K, V> SortedMap<K, V> asMap(SortedSet<K> set, Function<? super K, V> function) {
        return new SortedAsMapView<K, V>(set, function);
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> asMap(NavigableSet<K> set, Function<? super K, V> function) {
        return new NavigableAsMapView<K, V>(set, function);
    }

    static <K, V> Iterator<Map.Entry<K, V>> asMapEntryIterator(Set<K> set, final Function<? super K, V> function) {
        return new TransformedIterator<K, Map.Entry<K, V>>(set.iterator()){

            @Override
            Map.Entry<K, V> transform(@ParametricNullness K key) {
                return Maps.immutableEntry(key, function.apply(key));
            }
        };
    }

    private static <E> Set<E> removeOnlySet(final Set<E> set) {
        return new ForwardingSet<E>(){

            @Override
            protected Set<E> delegate() {
                return set;
            }

            @Override
            public boolean add(@ParametricNullness E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> es) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> set) {
        return new ForwardingSortedSet<E>(){

            @Override
            protected SortedSet<E> delegate() {
                return set;
            }

            @Override
            public boolean add(@ParametricNullness E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> es) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SortedSet<E> headSet(@ParametricNullness E toElement) {
                return Maps.removeOnlySortedSet(super.headSet(toElement));
            }

            @Override
            public SortedSet<E> subSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
                return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
            }

            @Override
            public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
                return Maps.removeOnlySortedSet(super.tailSet(fromElement));
            }
        };
    }

    @GwtIncompatible
    private static <E> NavigableSet<E> removeOnlyNavigableSet(final NavigableSet<E> set) {
        return new ForwardingNavigableSet<E>(){

            @Override
            protected NavigableSet<E> delegate() {
                return set;
            }

            @Override
            public boolean add(@ParametricNullness E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> es) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SortedSet<E> headSet(@ParametricNullness E toElement) {
                return Maps.removeOnlySortedSet(super.headSet(toElement));
            }

            @Override
            public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
                return Maps.removeOnlyNavigableSet(super.headSet(toElement, inclusive));
            }

            @Override
            public SortedSet<E> subSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
                return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
            }

            @Override
            public NavigableSet<E> subSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
                return Maps.removeOnlyNavigableSet(super.subSet(fromElement, fromInclusive, toElement, toInclusive));
            }

            @Override
            public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
                return Maps.removeOnlySortedSet(super.tailSet(fromElement));
            }

            @Override
            public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
                return Maps.removeOnlyNavigableSet(super.tailSet(fromElement, inclusive));
            }

            @Override
            public NavigableSet<E> descendingSet() {
                return Maps.removeOnlyNavigableSet(super.descendingSet());
            }
        };
    }

    public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> keys, Function<? super K, V> valueFunction) {
        return Maps.toMap(keys.iterator(), valueFunction);
    }

    public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> keys, Function<? super K, V> valueFunction) {
        Preconditions.checkNotNull(valueFunction);
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        while (keys.hasNext()) {
            K key = keys.next();
            builder.put(key, valueFunction.apply(key));
        }
        return builder.buildKeepingLast();
    }

    @CanIgnoreReturnValue
    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction) {
        if (values instanceof Collection) {
            return Maps.uniqueIndex(values.iterator(), keyFunction, ImmutableMap.builderWithExpectedSize(((Collection)values).size()));
        }
        return Maps.uniqueIndex(values.iterator(), keyFunction);
    }

    @CanIgnoreReturnValue
    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> values, Function<? super V, K> keyFunction) {
        return Maps.uniqueIndex(values, keyFunction, ImmutableMap.builder());
    }

    private static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> values, Function<? super V, K> keyFunction, ImmutableMap.Builder<K, V> builder) {
        Preconditions.checkNotNull(keyFunction);
        while (values.hasNext()) {
            V value = values.next();
            builder.put(keyFunction.apply(value), value);
        }
        try {
            return builder.buildOrThrow();
        }
        catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException(duplicateKeys.getMessage() + ". To index multiple values under a key, use Multimaps.index.");
        }
    }

    @J2ktIncompatible
    @GwtIncompatible
    public static ImmutableMap<String, String> fromProperties(Properties properties) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)Objects.requireNonNull(e.nextElement());
            builder.put(key, Objects.requireNonNull(properties.getProperty(key)));
        }
        return builder.buildOrThrow();
    }

    @GwtCompatible(serializable=true)
    public static <K, V> Map.Entry<K, V> immutableEntry(@ParametricNullness K key, @ParametricNullness V value) {
        return new ImmutableEntry<K, V>(key, value);
    }

    static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(Set<Map.Entry<K, V>> entrySet) {
        return new UnmodifiableEntrySet<K, V>(Collections.unmodifiableSet(entrySet));
    }

    static <K, V> Map.Entry<K, V> unmodifiableEntry(final Map.Entry<? extends K, ? extends V> entry) {
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V>(){

            @Override
            @ParametricNullness
            public K getKey() {
                return entry.getKey();
            }

            @Override
            @ParametricNullness
            public V getValue() {
                return entry.getValue();
            }
        };
    }

    static <K, V> UnmodifiableIterator<Map.Entry<K, V>> unmodifiableEntryIterator(final Iterator<Map.Entry<K, V>> entryIterator) {
        return new UnmodifiableIterator<Map.Entry<K, V>>(){

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
                return Maps.unmodifiableEntry((Map.Entry)entryIterator.next());
            }
        };
    }

    public static <A, B> Converter<A, B> asConverter(BiMap<A, B> bimap) {
        return new BiMapConverter<A, B>(bimap);
    }

    public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
        return Synchronized.biMap(bimap, null);
    }

    public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap) {
        return new UnmodifiableBiMap<K, V>(bimap, null);
    }

    public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> fromMap, Function<? super V1, V2> function) {
        return Maps.transformEntries(fromMap, Maps.asEntryTransformer(function));
    }

    public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> fromMap, Function<? super V1, V2> function) {
        return Maps.transformEntries(fromMap, Maps.asEntryTransformer(function));
    }

    @GwtIncompatible
    public static <K, V1, V2> NavigableMap<K, V2> transformValues(NavigableMap<K, V1> fromMap, Function<? super V1, V2> function) {
        return Maps.transformEntries(fromMap, Maps.asEntryTransformer(function));
    }

    public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesMap<K, V1, V2>(fromMap, transformer);
    }

    public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesSortedMap<K, V1, V2>(fromMap, transformer);
    }

    @GwtIncompatible
    public static <K, V1, V2> NavigableMap<K, V2> transformEntries(NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesNavigableMap<K, V1, V2>(fromMap, transformer);
    }

    static <K, V1, V2> EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        return new EntryTransformer<K, V1, V2>(){

            @Override
            @ParametricNullness
            public V2 transformEntry(@ParametricNullness K key, @ParametricNullness V1 value) {
                return function.apply(value);
            }
        };
    }

    static <K, V1, V2> Function<V1, V2> asValueToValueFunction(final EntryTransformer<? super K, V1, V2> transformer, final @ParametricNullness K key) {
        Preconditions.checkNotNull(transformer);
        return new Function<V1, V2>(){

            @Override
            @ParametricNullness
            public V2 apply(@ParametricNullness V1 v1) {
                return transformer.transformEntry(key, v1);
            }
        };
    }

    static <K, V1, V2> Function<Map.Entry<K, V1>, V2> asEntryToValueFunction(final EntryTransformer<? super K, ? super V1, V2> transformer) {
        Preconditions.checkNotNull(transformer);
        return new Function<Map.Entry<K, V1>, V2>(){

            @Override
            @ParametricNullness
            public V2 apply(Map.Entry<K, V1> entry) {
                return transformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <V2, K, V1> Map.Entry<K, V2> transformEntry(final EntryTransformer<? super K, ? super V1, V2> transformer, final Map.Entry<K, V1> entry) {
        Preconditions.checkNotNull(transformer);
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V2>(){

            @Override
            @ParametricNullness
            public K getKey() {
                return entry.getKey();
            }

            @Override
            @ParametricNullness
            public V2 getValue() {
                return transformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> asEntryToEntryFunction(final EntryTransformer<? super K, ? super V1, V2> transformer) {
        Preconditions.checkNotNull(transformer);
        return new Function<Map.Entry<K, V1>, Map.Entry<K, V2>>(){

            @Override
            public Map.Entry<K, V2> apply(Map.Entry<K, V1> entry) {
                return Maps.transformEntry(transformer, entry);
            }
        };
    }

    static <K> Predicate<Map.Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> keyPredicate) {
        return Predicates.compose(keyPredicate, Maps.keyFunction());
    }

    static <V> Predicate<Map.Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> valuePredicate) {
        return Predicates.compose(valuePredicate, Maps.valueFunction());
    }

    public static <K, V> Map<K, V> filterKeys(Map<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        Preconditions.checkNotNull(keyPredicate);
        Predicate<Map.Entry<? super K, ?>> entryPredicate = Maps.keyPredicateOnEntries(keyPredicate);
        return unfiltered instanceof AbstractFilteredMap ? Maps.filterFiltered((AbstractFilteredMap)unfiltered, entryPredicate) : new FilteredKeyMap<K, V>(Preconditions.checkNotNull(unfiltered), keyPredicate, entryPredicate);
    }

    public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        return Maps.filterEntries(unfiltered, Maps.keyPredicateOnEntries(keyPredicate));
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> filterKeys(NavigableMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        return Maps.filterEntries(unfiltered, Maps.keyPredicateOnEntries(keyPredicate));
    }

    public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        Preconditions.checkNotNull(keyPredicate);
        return Maps.filterEntries(unfiltered, Maps.keyPredicateOnEntries(keyPredicate));
    }

    public static <K, V> Map<K, V> filterValues(Map<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> filterValues(NavigableMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> Map<K, V> filterEntries(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Preconditions.checkNotNull(entryPredicate);
        return unfiltered instanceof AbstractFilteredMap ? Maps.filterFiltered((AbstractFilteredMap)unfiltered, entryPredicate) : new FilteredEntryMap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
    }

    public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Preconditions.checkNotNull(entryPredicate);
        return unfiltered instanceof FilteredEntrySortedMap ? Maps.filterFiltered((FilteredEntrySortedMap)unfiltered, entryPredicate) : new FilteredEntrySortedMap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> filterEntries(NavigableMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Preconditions.checkNotNull(entryPredicate);
        return unfiltered instanceof FilteredEntryNavigableMap ? Maps.filterFiltered((FilteredEntryNavigableMap)unfiltered, entryPredicate) : new FilteredEntryNavigableMap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
    }

    public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Preconditions.checkNotNull(unfiltered);
        Preconditions.checkNotNull(entryPredicate);
        return unfiltered instanceof FilteredEntryBiMap ? Maps.filterFiltered((FilteredEntryBiMap)unfiltered, entryPredicate) : new FilteredEntryBiMap<K, V>(unfiltered, entryPredicate);
    }

    private static <K, V> Map<K, V> filterFiltered(AbstractFilteredMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        return new FilteredEntryMap(map.unfiltered, Predicates.and(map.predicate, entryPredicate));
    }

    private static <K, V> SortedMap<K, V> filterFiltered(FilteredEntrySortedMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
        return new FilteredEntrySortedMap<K, V>(map.sortedMap(), predicate);
    }

    @GwtIncompatible
    private static <K, V> NavigableMap<K, V> filterFiltered(FilteredEntryNavigableMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(((FilteredEntryNavigableMap)map).entryPredicate, entryPredicate);
        return new FilteredEntryNavigableMap(((FilteredEntryNavigableMap)map).unfiltered, predicate);
    }

    private static <K, V> BiMap<K, V> filterFiltered(FilteredEntryBiMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
        return new FilteredEntryBiMap<K, V>(map.unfiltered(), predicate);
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> map) {
        Preconditions.checkNotNull(map);
        if (map instanceof UnmodifiableNavigableMap) {
            NavigableMap<K, ? extends V> result = map;
            return result;
        }
        return new UnmodifiableNavigableMap<K, V>(map);
    }

    @CheckForNull
    private static <K, V> Map.Entry<K, V> unmodifiableOrNull(@CheckForNull Map.Entry<K, ? extends V> entry) {
        return entry == null ? null : Maps.unmodifiableEntry(entry);
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> navigableMap) {
        return Synchronized.navigableMap(navigableMap);
    }

    @CheckForNull
    static <V> V safeGet(Map<?, V> map, @CheckForNull Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.get(key);
        }
        catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    static boolean safeContainsKey(Map<?, ?> map, @CheckForNull Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.containsKey(key);
        }
        catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @CheckForNull
    static <V> V safeRemove(Map<?, V> map, @CheckForNull Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.remove(key);
        }
        catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    static boolean containsKeyImpl(Map<?, ?> map, @CheckForNull Object key) {
        return Iterators.contains(Maps.keyIterator(map.entrySet().iterator()), key);
    }

    static boolean containsValueImpl(Map<?, ?> map, @CheckForNull Object value) {
        return Iterators.contains(Maps.valueIterator(map.entrySet().iterator()), value);
    }

    static <K, V> boolean containsEntryImpl(Collection<Map.Entry<K, V>> c, @CheckForNull Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        return c.contains(Maps.unmodifiableEntry((Map.Entry)o));
    }

    static <K, V> boolean removeEntryImpl(Collection<Map.Entry<K, V>> c, @CheckForNull Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        return c.remove(Maps.unmodifiableEntry((Map.Entry)o));
    }

    static boolean equalsImpl(Map<?, ?> map, @CheckForNull Object object) {
        if (map == object) {
            return true;
        }
        if (object instanceof Map) {
            Map o = (Map)object;
            return map.entrySet().equals(o.entrySet());
        }
        return false;
    }

    static String toStringImpl(Map<?, ?> map) {
        StringBuilder sb = Collections2.newStringBuilderForCollection(map.size()).append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.append('}').toString();
    }

    static <K, V> void putAllImpl(Map<K, V> self, Map<? extends K, ? extends V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            self.put(entry.getKey(), entry.getValue());
        }
    }

    @CheckForNull
    static <K> K keyOrNull(@CheckForNull Map.Entry<K, ?> entry) {
        return entry == null ? null : (K)entry.getKey();
    }

    @CheckForNull
    static <V> V valueOrNull(@CheckForNull Map.Entry<?, V> entry) {
        return entry == null ? null : (V)entry.getValue();
    }

    static <E> ImmutableMap<E, Integer> indexMap(Collection<E> list) {
        ImmutableMap.Builder<E, Integer> builder = new ImmutableMap.Builder<E, Integer>(list.size());
        int i = 0;
        for (E e : list) {
            builder.put(e, i++);
        }
        return builder.buildOrThrow();
    }

    @GwtIncompatible
    public static <K extends Comparable<? super K>, V> NavigableMap<K, V> subMap(NavigableMap<K, V> map, Range<K> range) {
        if (map.comparator() != null && map.comparator() != Ordering.natural() && range.hasLowerBound() && range.hasUpperBound()) {
            Preconditions.checkArgument(map.comparator().compare(range.lowerEndpoint(), range.upperEndpoint()) <= 0, "map is using a custom comparator which is inconsistent with the natural ordering.");
        }
        if (range.hasLowerBound() && range.hasUpperBound()) {
            return map.subMap(range.lowerEndpoint(), range.lowerBoundType() == BoundType.CLOSED, range.upperEndpoint(), range.upperBoundType() == BoundType.CLOSED);
        }
        if (range.hasLowerBound()) {
            return map.tailMap(range.lowerEndpoint(), range.lowerBoundType() == BoundType.CLOSED);
        }
        if (range.hasUpperBound()) {
            return map.headMap(range.upperEndpoint(), range.upperBoundType() == BoundType.CLOSED);
        }
        return Preconditions.checkNotNull(map);
    }

    @GwtIncompatible
    static abstract class DescendingMap<K, V>
    extends ForwardingMap<K, V>
    implements NavigableMap<K, V> {
        @LazyInit
        @CheckForNull
        private transient Comparator<? super K> comparator;
        @LazyInit
        @CheckForNull
        private transient Set<Map.Entry<K, V>> entrySet;
        @LazyInit
        @CheckForNull
        private transient NavigableSet<K> navigableKeySet;

        DescendingMap() {
        }

        abstract NavigableMap<K, V> forward();

        @Override
        protected final Map<K, V> delegate() {
            return this.forward();
        }

        @Override
        public Comparator<? super K> comparator() {
            Comparator<? super K> result = this.comparator;
            if (result == null) {
                Comparator forwardCmp = this.forward().comparator();
                if (forwardCmp == null) {
                    forwardCmp = Ordering.natural();
                }
                result = this.comparator = DescendingMap.reverse(forwardCmp);
            }
            return result;
        }

        private static <T> Ordering<T> reverse(Comparator<T> forward) {
            return Ordering.from(forward).reverse();
        }

        @Override
        @ParametricNullness
        public K firstKey() {
            return this.forward().lastKey();
        }

        @Override
        @ParametricNullness
        public K lastKey() {
            return this.forward().firstKey();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> lowerEntry(@ParametricNullness K key) {
            return this.forward().higherEntry(key);
        }

        @Override
        @CheckForNull
        public K lowerKey(@ParametricNullness K key) {
            return this.forward().higherKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> floorEntry(@ParametricNullness K key) {
            return this.forward().ceilingEntry(key);
        }

        @Override
        @CheckForNull
        public K floorKey(@ParametricNullness K key) {
            return this.forward().ceilingKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> ceilingEntry(@ParametricNullness K key) {
            return this.forward().floorEntry(key);
        }

        @Override
        @CheckForNull
        public K ceilingKey(@ParametricNullness K key) {
            return this.forward().floorKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> higherEntry(@ParametricNullness K key) {
            return this.forward().lowerEntry(key);
        }

        @Override
        @CheckForNull
        public K higherKey(@ParametricNullness K key) {
            return this.forward().lowerKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> firstEntry() {
            return this.forward().lastEntry();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> lastEntry() {
            return this.forward().firstEntry();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> pollFirstEntry() {
            return this.forward().pollLastEntry();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> pollLastEntry() {
            return this.forward().pollFirstEntry();
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            return this.forward();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            Set<Map.Entry<K, V>> result = this.entrySet;
            return result == null ? (this.entrySet = this.createEntrySet()) : result;
        }

        abstract Iterator<Map.Entry<K, V>> entryIterator();

        Set<Map.Entry<K, V>> createEntrySet() {
            class EntrySetImpl
            extends EntrySet<K, V> {
                EntrySetImpl() {
                }

                @Override
                Map<K, V> map() {
                    return DescendingMap.this;
                }

                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return DescendingMap.this.entryIterator();
                }
            }
            return new EntrySetImpl();
        }

        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            NavigableSet<K> result = this.navigableKeySet;
            return result == null ? (this.navigableKeySet = new NavigableKeySet(this)) : result;
        }

        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.forward().navigableKeySet();
        }

        @Override
        public NavigableMap<K, V> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
            return this.forward().subMap(toKey, toInclusive, fromKey, fromInclusive).descendingMap();
        }

        @Override
        public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return this.subMap(fromKey, true, toKey, false);
        }

        @Override
        public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
            return this.forward().tailMap(toKey, inclusive).descendingMap();
        }

        @Override
        public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
            return this.headMap(toKey, false);
        }

        @Override
        public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
            return this.forward().headMap(fromKey, inclusive).descendingMap();
        }

        @Override
        public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
            return this.tailMap(fromKey, true);
        }

        @Override
        public Collection<V> values() {
            return new Values(this);
        }

        @Override
        public String toString() {
            return this.standardToString();
        }
    }

    static abstract class EntrySet<K, V>
    extends Sets.ImprovedAbstractSet<Map.Entry<K, V>> {
        EntrySet() {
        }

        abstract Map<K, V> map();

        @Override
        public int size() {
            return this.map().size();
        }

        @Override
        public void clear() {
            this.map().clear();
        }

        @Override
        public boolean contains(@CheckForNull Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)o;
                Object key = entry.getKey();
                V value = Maps.safeGet(this.map(), key);
                return com.google.common.base.Objects.equal(value, entry.getValue()) && (value != null || this.map().containsKey(key));
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }

        @Override
        public boolean remove(@CheckForNull Object o) {
            if (this.contains(o) && o instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)o;
                return this.map().keySet().remove(entry.getKey());
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            try {
                return super.removeAll(Preconditions.checkNotNull(c));
            }
            catch (UnsupportedOperationException e) {
                return Sets.removeAllImpl(this, c.iterator());
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            try {
                return super.retainAll(Preconditions.checkNotNull(c));
            }
            catch (UnsupportedOperationException e) {
                HashSet<@Nullable K> keys = Sets.newHashSetWithExpectedSize(c.size());
                for (Object o : c) {
                    if (!this.contains(o) || !(o instanceof Map.Entry)) continue;
                    Map.Entry entry = (Map.Entry)o;
                    keys.add(entry.getKey());
                }
                return this.map().keySet().retainAll(keys);
            }
        }
    }

    static class Values<K, V>
    extends AbstractCollection<V> {
        @Weak
        final Map<K, V> map;

        Values(Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }

        final Map<K, V> map() {
            return this.map;
        }

        @Override
        public Iterator<V> iterator() {
            return Maps.valueIterator(this.map().entrySet().iterator());
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            Preconditions.checkNotNull(action);
            this.map.forEach((? super K k, ? super V v) -> action.accept((Object)v));
        }

        @Override
        public boolean remove(@CheckForNull Object o) {
            try {
                return super.remove(o);
            }
            catch (UnsupportedOperationException e) {
                for (Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (!com.google.common.base.Objects.equal(o, entry.getValue())) continue;
                    this.map().remove(entry.getKey());
                    return true;
                }
                return false;
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            try {
                return super.removeAll(Preconditions.checkNotNull(c));
            }
            catch (UnsupportedOperationException e) {
                HashSet<K> toRemove = Sets.newHashSet();
                for (Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (!c.contains(entry.getValue())) continue;
                    toRemove.add(entry.getKey());
                }
                return this.map().keySet().removeAll(toRemove);
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            try {
                return super.retainAll(Preconditions.checkNotNull(c));
            }
            catch (UnsupportedOperationException e) {
                HashSet<K> toRetain = Sets.newHashSet();
                for (Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (!c.contains(entry.getValue())) continue;
                    toRetain.add(entry.getKey());
                }
                return this.map().keySet().retainAll(toRetain);
            }
        }

        @Override
        public int size() {
            return this.map().size();
        }

        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }

        @Override
        public boolean contains(@CheckForNull Object o) {
            return this.map().containsValue(o);
        }

        @Override
        public void clear() {
            this.map().clear();
        }
    }

    @GwtIncompatible
    static class NavigableKeySet<K, V>
    extends SortedKeySet<K, V>
    implements NavigableSet<K> {
        NavigableKeySet(NavigableMap<K, V> map) {
            super(map);
        }

        @Override
        NavigableMap<K, V> map() {
            return (NavigableMap)this.map;
        }

        @Override
        @CheckForNull
        public K lower(@ParametricNullness K e) {
            return this.map().lowerKey(e);
        }

        @Override
        @CheckForNull
        public K floor(@ParametricNullness K e) {
            return this.map().floorKey(e);
        }

        @Override
        @CheckForNull
        public K ceiling(@ParametricNullness K e) {
            return this.map().ceilingKey(e);
        }

        @Override
        @CheckForNull
        public K higher(@ParametricNullness K e) {
            return this.map().higherKey(e);
        }

        @Override
        @CheckForNull
        public K pollFirst() {
            return Maps.keyOrNull(this.map().pollFirstEntry());
        }

        @Override
        @CheckForNull
        public K pollLast() {
            return Maps.keyOrNull(this.map().pollLastEntry());
        }

        @Override
        public NavigableSet<K> descendingSet() {
            return this.map().descendingKeySet();
        }

        @Override
        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }

        @Override
        public NavigableSet<K> subSet(@ParametricNullness K fromElement, boolean fromInclusive, @ParametricNullness K toElement, boolean toInclusive) {
            return this.map().subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
        }

        @Override
        public SortedSet<K> subSet(@ParametricNullness K fromElement, @ParametricNullness K toElement) {
            return this.subSet(fromElement, true, toElement, false);
        }

        @Override
        public NavigableSet<K> headSet(@ParametricNullness K toElement, boolean inclusive) {
            return this.map().headMap(toElement, inclusive).navigableKeySet();
        }

        @Override
        public SortedSet<K> headSet(@ParametricNullness K toElement) {
            return this.headSet(toElement, false);
        }

        @Override
        public NavigableSet<K> tailSet(@ParametricNullness K fromElement, boolean inclusive) {
            return this.map().tailMap(fromElement, inclusive).navigableKeySet();
        }

        @Override
        public SortedSet<K> tailSet(@ParametricNullness K fromElement) {
            return this.tailSet(fromElement, true);
        }
    }

    static class SortedKeySet<K, V>
    extends KeySet<K, V>
    implements SortedSet<K> {
        SortedKeySet(SortedMap<K, V> map) {
            super(map);
        }

        @Override
        SortedMap<K, V> map() {
            return (SortedMap)super.map();
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.map().comparator();
        }

        @Override
        public SortedSet<K> subSet(@ParametricNullness K fromElement, @ParametricNullness K toElement) {
            return new SortedKeySet(this.map().subMap(fromElement, toElement));
        }

        @Override
        public SortedSet<K> headSet(@ParametricNullness K toElement) {
            return new SortedKeySet(this.map().headMap(toElement));
        }

        @Override
        public SortedSet<K> tailSet(@ParametricNullness K fromElement) {
            return new SortedKeySet(this.map().tailMap(fromElement));
        }

        @Override
        @ParametricNullness
        public K first() {
            return this.map().firstKey();
        }

        @Override
        @ParametricNullness
        public K last() {
            return this.map().lastKey();
        }
    }

    static class KeySet<K, V>
    extends Sets.ImprovedAbstractSet<K> {
        @Weak
        final Map<K, V> map;

        KeySet(Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }

        Map<K, V> map() {
            return this.map;
        }

        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(this.map().entrySet().iterator());
        }

        @Override
        public void forEach(Consumer<? super K> action) {
            Preconditions.checkNotNull(action);
            this.map.forEach((? super K k, ? super V v) -> action.accept((Object)k));
        }

        @Override
        public int size() {
            return this.map().size();
        }

        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }

        @Override
        public boolean contains(@CheckForNull Object o) {
            return this.map().containsKey(o);
        }

        @Override
        public boolean remove(@CheckForNull Object o) {
            if (this.contains(o)) {
                this.map().remove(o);
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            this.map().clear();
        }
    }

    static abstract class IteratorBasedAbstractMap<K, V>
    extends AbstractMap<K, V> {
        IteratorBasedAbstractMap() {
        }

        @Override
        public abstract int size();

        abstract Iterator<Map.Entry<K, V>> entryIterator();

        Spliterator<Map.Entry<K, V>> entrySpliterator() {
            return Spliterators.spliterator(this.entryIterator(), (long)this.size(), 65);
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return new EntrySet<K, V>(){

                @Override
                Map<K, V> map() {
                    return this;
                }

                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return this.entryIterator();
                }

                @Override
                public Spliterator<Map.Entry<K, V>> spliterator() {
                    return this.entrySpliterator();
                }

                @Override
                public void forEach(Consumer<? super Map.Entry<K, V>> action) {
                    this.forEachEntry(action);
                }
            };
        }

        void forEachEntry(Consumer<? super Map.Entry<K, V>> action) {
            this.entryIterator().forEachRemaining(action);
        }

        @Override
        public void clear() {
            Iterators.clear(this.entryIterator());
        }
    }

    @GwtCompatible
    static abstract class ViewCachingAbstractMap<K, V>
    extends AbstractMap<K, V> {
        @LazyInit
        @CheckForNull
        private transient Set<Map.Entry<K, V>> entrySet;
        @LazyInit
        @CheckForNull
        private transient Set<K> keySet;
        @LazyInit
        @CheckForNull
        private transient Collection<V> values;

        ViewCachingAbstractMap() {
        }

        abstract Set<Map.Entry<K, V>> createEntrySet();

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            Set<Map.Entry<K, V>> result = this.entrySet;
            return result == null ? (this.entrySet = this.createEntrySet()) : result;
        }

        @Override
        public Set<K> keySet() {
            Set<K> result = this.keySet;
            return result == null ? (this.keySet = this.createKeySet()) : result;
        }

        Set<K> createKeySet() {
            return new KeySet(this);
        }

        @Override
        public Collection<V> values() {
            Collection<V> result = this.values;
            return result == null ? (this.values = this.createValues()) : result;
        }

        Collection<V> createValues() {
            return new Values(this);
        }
    }

    @GwtIncompatible
    static class UnmodifiableNavigableMap<K, V>
    extends ForwardingSortedMap<K, V>
    implements NavigableMap<K, V>,
    Serializable {
        private final NavigableMap<K, ? extends V> delegate;
        @LazyInit
        @CheckForNull
        private transient UnmodifiableNavigableMap<K, V> descendingMap;

        UnmodifiableNavigableMap(NavigableMap<K, ? extends V> delegate) {
            this.delegate = delegate;
        }

        UnmodifiableNavigableMap(NavigableMap<K, ? extends V> delegate, UnmodifiableNavigableMap<K, V> descendingMap) {
            this.delegate = delegate;
            this.descendingMap = descendingMap;
        }

        @Override
        protected SortedMap<K, V> delegate() {
            return Collections.unmodifiableSortedMap(this.delegate);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> lowerEntry(@ParametricNullness K key) {
            return Maps.unmodifiableOrNull(this.delegate.lowerEntry(key));
        }

        @Override
        @CheckForNull
        public K lowerKey(@ParametricNullness K key) {
            return this.delegate.lowerKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> floorEntry(@ParametricNullness K key) {
            return Maps.unmodifiableOrNull(this.delegate.floorEntry(key));
        }

        @Override
        @CheckForNull
        public K floorKey(@ParametricNullness K key) {
            return this.delegate.floorKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> ceilingEntry(@ParametricNullness K key) {
            return Maps.unmodifiableOrNull(this.delegate.ceilingEntry(key));
        }

        @Override
        @CheckForNull
        public K ceilingKey(@ParametricNullness K key) {
            return this.delegate.ceilingKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> higherEntry(@ParametricNullness K key) {
            return Maps.unmodifiableOrNull(this.delegate.higherEntry(key));
        }

        @Override
        @CheckForNull
        public K higherKey(@ParametricNullness K key) {
            return this.delegate.higherKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> firstEntry() {
            return Maps.unmodifiableOrNull(this.delegate.firstEntry());
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> lastEntry() {
            return Maps.unmodifiableOrNull(this.delegate.lastEntry());
        }

        @Override
        @CheckForNull
        public final Map.Entry<K, V> pollFirstEntry() {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public final Map.Entry<K, V> pollLastEntry() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(@Nullable Object key, @Nullable Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends @Nullable V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V compute(K key, BiFunction<? super K, ? super @Nullable V, ? extends @Nullable V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends @Nullable V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            UnmodifiableNavigableMap<K, V> result = this.descendingMap;
            return result == null ? (this.descendingMap = new UnmodifiableNavigableMap<K, V>(this.delegate.descendingMap(), this)) : result;
        }

        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
        }

        @Override
        public NavigableSet<K> descendingKeySet() {
            return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
        }

        @Override
        public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return this.subMap(fromKey, true, toKey, false);
        }

        @Override
        public NavigableMap<K, V> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
            return Maps.unmodifiableNavigableMap(this.delegate.subMap(fromKey, fromInclusive, toKey, toInclusive));
        }

        @Override
        public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
            return this.headMap(toKey, false);
        }

        @Override
        public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
            return Maps.unmodifiableNavigableMap(this.delegate.headMap(toKey, inclusive));
        }

        @Override
        public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
            return this.tailMap(fromKey, true);
        }

        @Override
        public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
            return Maps.unmodifiableNavigableMap(this.delegate.tailMap(fromKey, inclusive));
        }
    }

    static final class FilteredEntryBiMap<K, V>
    extends FilteredEntryMap<K, V>
    implements BiMap<K, V> {
        @RetainedWith
        private final BiMap<V, K> inverse;

        private static <K, V> Predicate<Map.Entry<V, K>> inversePredicate(final Predicate<? super Map.Entry<K, V>> forwardPredicate) {
            return new Predicate<Map.Entry<V, K>>(){

                @Override
                public boolean apply(Map.Entry<V, K> input) {
                    return forwardPredicate.apply(Maps.immutableEntry(input.getValue(), input.getKey()));
                }
            };
        }

        FilteredEntryBiMap(BiMap<K, V> delegate, Predicate<? super Map.Entry<K, V>> predicate) {
            super(delegate, predicate);
            this.inverse = new FilteredEntryBiMap<K, V>(delegate.inverse(), FilteredEntryBiMap.inversePredicate(predicate), this);
        }

        private FilteredEntryBiMap(BiMap<K, V> delegate, Predicate<? super Map.Entry<K, V>> predicate, BiMap<V, K> inverse) {
            super(delegate, predicate);
            this.inverse = inverse;
        }

        BiMap<K, V> unfiltered() {
            return (BiMap)this.unfiltered;
        }

        @Override
        @CheckForNull
        public V forcePut(@ParametricNullness K key, @ParametricNullness V value) {
            Preconditions.checkArgument(this.apply(key, value));
            return this.unfiltered().forcePut(key, value);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            this.unfiltered().replaceAll((key, value) -> this.predicate.apply(Maps.immutableEntry(key, value)) ? function.apply((K)key, (V)value) : value);
        }

        @Override
        public BiMap<V, K> inverse() {
            return this.inverse;
        }

        @Override
        public Set<V> values() {
            return this.inverse.keySet();
        }
    }

    @GwtIncompatible
    private static class FilteredEntryNavigableMap<K, V>
    extends AbstractNavigableMap<K, V> {
        private final NavigableMap<K, V> unfiltered;
        private final Predicate<? super Map.Entry<K, V>> entryPredicate;
        private final Map<K, V> filteredDelegate;

        FilteredEntryNavigableMap(NavigableMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
            this.unfiltered = Preconditions.checkNotNull(unfiltered);
            this.entryPredicate = entryPredicate;
            this.filteredDelegate = new FilteredEntryMap<K, V>(unfiltered, entryPredicate);
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.unfiltered.comparator();
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return new NavigableKeySet<K, V>(this){

                @Override
                public boolean removeAll(Collection<?> collection) {
                    return FilteredEntryMap.removeAllKeys(unfiltered, entryPredicate, collection);
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return FilteredEntryMap.retainAllKeys(unfiltered, entryPredicate, collection);
                }
            };
        }

        @Override
        public Collection<V> values() {
            return new FilteredMapValues<K, V>(this, this.unfiltered, this.entryPredicate);
        }

        @Override
        Iterator<Map.Entry<K, V>> entryIterator() {
            return Iterators.filter(this.unfiltered.entrySet().iterator(), this.entryPredicate);
        }

        @Override
        Iterator<Map.Entry<K, V>> descendingEntryIterator() {
            return Iterators.filter(this.unfiltered.descendingMap().entrySet().iterator(), this.entryPredicate);
        }

        @Override
        public int size() {
            return this.filteredDelegate.size();
        }

        @Override
        public boolean isEmpty() {
            return !Iterables.any(this.unfiltered.entrySet(), this.entryPredicate);
        }

        @Override
        @CheckForNull
        public V get(@CheckForNull Object key) {
            return this.filteredDelegate.get(key);
        }

        @Override
        public boolean containsKey(@CheckForNull Object key) {
            return this.filteredDelegate.containsKey(key);
        }

        @Override
        @CheckForNull
        public V put(@ParametricNullness K key, @ParametricNullness V value) {
            return this.filteredDelegate.put(key, value);
        }

        @Override
        @CheckForNull
        public V remove(@CheckForNull Object key) {
            return this.filteredDelegate.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            this.filteredDelegate.putAll(m);
        }

        @Override
        public void clear() {
            this.filteredDelegate.clear();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return this.filteredDelegate.entrySet();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> pollFirstEntry() {
            return Iterables.removeFirstMatching(this.unfiltered.entrySet(), this.entryPredicate);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V> pollLastEntry() {
            return Iterables.removeFirstMatching(this.unfiltered.descendingMap().entrySet(), this.entryPredicate);
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            return Maps.filterEntries(this.unfiltered.descendingMap(), this.entryPredicate);
        }

        @Override
        public NavigableMap<K, V> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
            return Maps.filterEntries(this.unfiltered.subMap(fromKey, fromInclusive, toKey, toInclusive), this.entryPredicate);
        }

        @Override
        public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
            return Maps.filterEntries(this.unfiltered.headMap(toKey, inclusive), this.entryPredicate);
        }

        @Override
        public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
            return Maps.filterEntries(this.unfiltered.tailMap(fromKey, inclusive), this.entryPredicate);
        }
    }

    private static class FilteredEntrySortedMap<K, V>
    extends FilteredEntryMap<K, V>
    implements SortedMap<K, V> {
        FilteredEntrySortedMap(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
            super(unfiltered, entryPredicate);
        }

        SortedMap<K, V> sortedMap() {
            return (SortedMap)this.unfiltered;
        }

        @Override
        public SortedSet<K> keySet() {
            return (SortedSet)super.keySet();
        }

        @Override
        SortedSet<K> createKeySet() {
            return new SortedKeySet();
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }

        @Override
        @ParametricNullness
        public K firstKey() {
            return (K)this.keySet().iterator().next();
        }

        @Override
        @ParametricNullness
        public K lastKey() {
            SortedMap<K, V> headMap = this.sortedMap();
            K key;
            while (!this.apply(key = headMap.lastKey(), NullnessCasts.uncheckedCastNullableTToT(this.unfiltered.get(key)))) {
                headMap = this.sortedMap().headMap(key);
            }
            return key;
        }

        @Override
        public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
            return new FilteredEntrySortedMap<K, V>(this.sortedMap().headMap(toKey), this.predicate);
        }

        @Override
        public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return new FilteredEntrySortedMap<K, V>(this.sortedMap().subMap(fromKey, toKey), this.predicate);
        }

        @Override
        public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
            return new FilteredEntrySortedMap<K, V>(this.sortedMap().tailMap(fromKey), this.predicate);
        }

        class SortedKeySet
        extends FilteredEntryMap.KeySet
        implements SortedSet<K> {
            SortedKeySet() {
            }

            @Override
            @CheckForNull
            public Comparator<? super K> comparator() {
                return FilteredEntrySortedMap.this.sortedMap().comparator();
            }

            @Override
            public SortedSet<K> subSet(@ParametricNullness K fromElement, @ParametricNullness K toElement) {
                return (SortedSet)FilteredEntrySortedMap.this.subMap(fromElement, toElement).keySet();
            }

            @Override
            public SortedSet<K> headSet(@ParametricNullness K toElement) {
                return (SortedSet)FilteredEntrySortedMap.this.headMap(toElement).keySet();
            }

            @Override
            public SortedSet<K> tailSet(@ParametricNullness K fromElement) {
                return (SortedSet)FilteredEntrySortedMap.this.tailMap(fromElement).keySet();
            }

            @Override
            @ParametricNullness
            public K first() {
                return FilteredEntrySortedMap.this.firstKey();
            }

            @Override
            @ParametricNullness
            public K last() {
                return FilteredEntrySortedMap.this.lastKey();
            }
        }
    }

    static class FilteredEntryMap<K, V>
    extends AbstractFilteredMap<K, V> {
        final Set<Map.Entry<K, V>> filteredEntrySet;

        FilteredEntryMap(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
            super(unfiltered, entryPredicate);
            this.filteredEntrySet = Sets.filter(unfiltered.entrySet(), this.predicate);
        }

        @Override
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return new EntrySet();
        }

        @Override
        Set<K> createKeySet() {
            return new KeySet();
        }

        static <K, V> boolean removeAllKeys(Map<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate, Collection<?> keyCollection) {
            Iterator<Map.Entry<K, V>> entryItr = map.entrySet().iterator();
            boolean result = false;
            while (entryItr.hasNext()) {
                Map.Entry<K, V> entry = entryItr.next();
                if (!entryPredicate.apply(entry) || !keyCollection.contains(entry.getKey())) continue;
                entryItr.remove();
                result = true;
            }
            return result;
        }

        static <K, V> boolean retainAllKeys(Map<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate, Collection<?> keyCollection) {
            Iterator<Map.Entry<K, V>> entryItr = map.entrySet().iterator();
            boolean result = false;
            while (entryItr.hasNext()) {
                Map.Entry<K, V> entry = entryItr.next();
                if (!entryPredicate.apply(entry) || keyCollection.contains(entry.getKey())) continue;
                entryItr.remove();
                result = true;
            }
            return result;
        }

        class KeySet
        extends com.google.common.collect.Maps$KeySet<K, V> {
            KeySet() {
                super(FilteredEntryMap.this);
            }

            @Override
            public boolean remove(@CheckForNull Object o) {
                if (FilteredEntryMap.this.containsKey(o)) {
                    FilteredEntryMap.this.unfiltered.remove(o);
                    return true;
                }
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return FilteredEntryMap.removeAllKeys(FilteredEntryMap.this.unfiltered, FilteredEntryMap.this.predicate, collection);
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return FilteredEntryMap.retainAllKeys(FilteredEntryMap.this.unfiltered, FilteredEntryMap.this.predicate, collection);
            }

            @Override
            public @Nullable Object[] toArray() {
                return Lists.newArrayList(this.iterator()).toArray();
            }

            @Override
            public <T> T[] toArray(T[] array) {
                return Lists.newArrayList(this.iterator()).toArray(array);
            }
        }

        private class EntrySet
        extends ForwardingSet<Map.Entry<K, V>> {
            private EntrySet() {
            }

            @Override
            protected Set<Map.Entry<K, V>> delegate() {
                return FilteredEntryMap.this.filteredEntrySet;
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new TransformedIterator<Map.Entry<K, V>, Map.Entry<K, V>>(FilteredEntryMap.this.filteredEntrySet.iterator()){

                    @Override
                    Map.Entry<K, V> transform(final Map.Entry<K, V> entry) {
                        return new ForwardingMapEntry<K, V>(){

                            @Override
                            protected Map.Entry<K, V> delegate() {
                                return entry;
                            }

                            @Override
                            @ParametricNullness
                            public V setValue(@ParametricNullness V newValue) {
                                Preconditions.checkArgument(FilteredEntryMap.this.apply(this.getKey(), newValue));
                                return super.setValue(newValue);
                            }
                        };
                    }
                };
            }
        }
    }

    private static class FilteredKeyMap<K, V>
    extends AbstractFilteredMap<K, V> {
        final Predicate<? super K> keyPredicate;

        FilteredKeyMap(Map<K, V> unfiltered, Predicate<? super K> keyPredicate, Predicate<? super Map.Entry<K, V>> entryPredicate) {
            super(unfiltered, entryPredicate);
            this.keyPredicate = keyPredicate;
        }

        @Override
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return Sets.filter(this.unfiltered.entrySet(), this.predicate);
        }

        @Override
        Set<K> createKeySet() {
            return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
        }

        @Override
        public boolean containsKey(@CheckForNull Object key) {
            return this.unfiltered.containsKey(key) && this.keyPredicate.apply(key);
        }
    }

    private static final class FilteredMapValues<K, V>
    extends Values<K, V> {
        final Map<K, V> unfiltered;
        final Predicate<? super Map.Entry<K, V>> predicate;

        FilteredMapValues(Map<K, V> filteredMap, Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate) {
            super(filteredMap);
            this.unfiltered = unfiltered;
            this.predicate = predicate;
        }

        @Override
        public boolean remove(@CheckForNull Object o) {
            Iterator<Map.Entry<K, V>> entryItr = this.unfiltered.entrySet().iterator();
            while (entryItr.hasNext()) {
                Map.Entry<K, V> entry = entryItr.next();
                if (!this.predicate.apply(entry) || !com.google.common.base.Objects.equal(entry.getValue(), o)) continue;
                entryItr.remove();
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            Iterator<Map.Entry<K, V>> entryItr = this.unfiltered.entrySet().iterator();
            boolean result = false;
            while (entryItr.hasNext()) {
                Map.Entry<K, V> entry = entryItr.next();
                if (!this.predicate.apply(entry) || !collection.contains(entry.getValue())) continue;
                entryItr.remove();
                result = true;
            }
            return result;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            Iterator<Map.Entry<K, V>> entryItr = this.unfiltered.entrySet().iterator();
            boolean result = false;
            while (entryItr.hasNext()) {
                Map.Entry<K, V> entry = entryItr.next();
                if (!this.predicate.apply(entry) || collection.contains(entry.getValue())) continue;
                entryItr.remove();
                result = true;
            }
            return result;
        }

        @Override
        public @Nullable Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
        }

        @Override
        public <T> T[] toArray(T[] array) {
            return Lists.newArrayList(this.iterator()).toArray(array);
        }
    }

    private static abstract class AbstractFilteredMap<K, V>
    extends ViewCachingAbstractMap<K, V> {
        final Map<K, V> unfiltered;
        final Predicate<? super Map.Entry<K, V>> predicate;

        AbstractFilteredMap(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate) {
            this.unfiltered = unfiltered;
            this.predicate = predicate;
        }

        boolean apply(@CheckForNull Object key, @ParametricNullness V value) {
            Object k = key;
            return this.predicate.apply(Maps.immutableEntry(k, value));
        }

        @Override
        @CheckForNull
        public V put(@ParametricNullness K key, @ParametricNullness V value) {
            Preconditions.checkArgument(this.apply(key, value));
            return this.unfiltered.put(key, value);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                Preconditions.checkArgument(this.apply(entry.getKey(), entry.getValue()));
            }
            this.unfiltered.putAll(map);
        }

        @Override
        public boolean containsKey(@CheckForNull Object key) {
            return this.unfiltered.containsKey(key) && this.apply(key, this.unfiltered.get(key));
        }

        @Override
        @CheckForNull
        public V get(@CheckForNull Object key) {
            V value = this.unfiltered.get(key);
            return value != null && this.apply(key, value) ? (V)value : null;
        }

        @Override
        public boolean isEmpty() {
            return this.entrySet().isEmpty();
        }

        @Override
        @CheckForNull
        public V remove(@CheckForNull Object key) {
            return this.containsKey(key) ? (V)this.unfiltered.remove(key) : null;
        }

        @Override
        Collection<V> createValues() {
            return new FilteredMapValues<K, V>(this, this.unfiltered, this.predicate);
        }
    }

    @GwtIncompatible
    private static class TransformedEntriesNavigableMap<K, V1, V2>
    extends TransformedEntriesSortedMap<K, V1, V2>
    implements NavigableMap<K, V2> {
        TransformedEntriesNavigableMap(NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
            super(fromMap, transformer);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> ceilingEntry(@ParametricNullness K key) {
            return this.transformEntry(this.fromMap().ceilingEntry(key));
        }

        @Override
        @CheckForNull
        public K ceilingKey(@ParametricNullness K key) {
            return this.fromMap().ceilingKey(key);
        }

        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.fromMap().descendingKeySet();
        }

        @Override
        public NavigableMap<K, V2> descendingMap() {
            return Maps.transformEntries(this.fromMap().descendingMap(), this.transformer);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> firstEntry() {
            return this.transformEntry(this.fromMap().firstEntry());
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> floorEntry(@ParametricNullness K key) {
            return this.transformEntry(this.fromMap().floorEntry(key));
        }

        @Override
        @CheckForNull
        public K floorKey(@ParametricNullness K key) {
            return this.fromMap().floorKey(key);
        }

        @Override
        public NavigableMap<K, V2> headMap(@ParametricNullness K toKey) {
            return this.headMap(toKey, false);
        }

        @Override
        public NavigableMap<K, V2> headMap(@ParametricNullness K toKey, boolean inclusive) {
            return Maps.transformEntries(this.fromMap().headMap(toKey, inclusive), this.transformer);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> higherEntry(@ParametricNullness K key) {
            return this.transformEntry(this.fromMap().higherEntry(key));
        }

        @Override
        @CheckForNull
        public K higherKey(@ParametricNullness K key) {
            return this.fromMap().higherKey(key);
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> lastEntry() {
            return this.transformEntry(this.fromMap().lastEntry());
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> lowerEntry(@ParametricNullness K key) {
            return this.transformEntry(this.fromMap().lowerEntry(key));
        }

        @Override
        @CheckForNull
        public K lowerKey(@ParametricNullness K key) {
            return this.fromMap().lowerKey(key);
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return this.fromMap().navigableKeySet();
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> pollFirstEntry() {
            return this.transformEntry(this.fromMap().pollFirstEntry());
        }

        @Override
        @CheckForNull
        public Map.Entry<K, V2> pollLastEntry() {
            return this.transformEntry(this.fromMap().pollLastEntry());
        }

        @Override
        public NavigableMap<K, V2> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
            return Maps.transformEntries(this.fromMap().subMap(fromKey, fromInclusive, toKey, toInclusive), this.transformer);
        }

        @Override
        public NavigableMap<K, V2> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return this.subMap(fromKey, true, toKey, false);
        }

        @Override
        public NavigableMap<K, V2> tailMap(@ParametricNullness K fromKey) {
            return this.tailMap(fromKey, true);
        }

        @Override
        public NavigableMap<K, V2> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
            return Maps.transformEntries(this.fromMap().tailMap(fromKey, inclusive), this.transformer);
        }

        @CheckForNull
        private Map.Entry<K, V2> transformEntry(@CheckForNull Map.Entry<K, V1> entry) {
            return entry == null ? null : Maps.transformEntry(this.transformer, entry);
        }

        @Override
        protected NavigableMap<K, V1> fromMap() {
            return (NavigableMap)super.fromMap();
        }
    }

    static class TransformedEntriesSortedMap<K, V1, V2>
    extends TransformedEntriesMap<K, V1, V2>
    implements SortedMap<K, V2> {
        protected SortedMap<K, V1> fromMap() {
            return (SortedMap)this.fromMap;
        }

        TransformedEntriesSortedMap(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
            super(fromMap, transformer);
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.fromMap().comparator();
        }

        @Override
        @ParametricNullness
        public K firstKey() {
            return this.fromMap().firstKey();
        }

        @Override
        public SortedMap<K, V2> headMap(@ParametricNullness K toKey) {
            return Maps.transformEntries(this.fromMap().headMap(toKey), this.transformer);
        }

        @Override
        @ParametricNullness
        public K lastKey() {
            return this.fromMap().lastKey();
        }

        @Override
        public SortedMap<K, V2> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return Maps.transformEntries(this.fromMap().subMap(fromKey, toKey), this.transformer);
        }

        @Override
        public SortedMap<K, V2> tailMap(@ParametricNullness K fromKey) {
            return Maps.transformEntries(this.fromMap().tailMap(fromKey), this.transformer);
        }
    }

    static class TransformedEntriesMap<K, V1, V2>
    extends IteratorBasedAbstractMap<K, V2> {
        final Map<K, V1> fromMap;
        final EntryTransformer<? super K, ? super V1, V2> transformer;

        TransformedEntriesMap(Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
            this.fromMap = Preconditions.checkNotNull(fromMap);
            this.transformer = Preconditions.checkNotNull(transformer);
        }

        @Override
        public int size() {
            return this.fromMap.size();
        }

        @Override
        public boolean containsKey(@CheckForNull Object key) {
            return this.fromMap.containsKey(key);
        }

        @Override
        @CheckForNull
        public V2 get(@CheckForNull Object key) {
            return this.getOrDefault(key, (V2)null);
        }

        @Override
        @CheckForNull
        public V2 getOrDefault(@CheckForNull Object key, @CheckForNull V2 defaultValue) {
            V1 value = this.fromMap.get(key);
            if (value != null || this.fromMap.containsKey(key)) {
                return this.transformer.transformEntry(key, NullnessCasts.uncheckedCastNullableTToT(value));
            }
            return defaultValue;
        }

        @Override
        @CheckForNull
        public V2 remove(@CheckForNull Object key) {
            return this.fromMap.containsKey(key) ? (V2)this.transformer.transformEntry((K)key, (V1)NullnessCasts.uncheckedCastNullableTToT(this.fromMap.remove(key))) : null;
        }

        @Override
        public void clear() {
            this.fromMap.clear();
        }

        @Override
        public Set<K> keySet() {
            return this.fromMap.keySet();
        }

        @Override
        Iterator<Map.Entry<K, V2>> entryIterator() {
            return Iterators.transform(this.fromMap.entrySet().iterator(), Maps.asEntryToEntryFunction(this.transformer));
        }

        @Override
        Spliterator<Map.Entry<K, V2>> entrySpliterator() {
            return CollectSpliterators.map(this.fromMap.entrySet().spliterator(), Maps.asEntryToEntryFunction(this.transformer));
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V2> action) {
            Preconditions.checkNotNull(action);
            this.fromMap.forEach((? super K k, ? super V v1) -> action.accept((K)k, (V2)this.transformer.transformEntry(k, v1)));
        }

        @Override
        public Collection<V2> values() {
            return new Values(this);
        }
    }

    @FunctionalInterface
    public static interface EntryTransformer<K, V1, V2> {
        @ParametricNullness
        public V2 transformEntry(@ParametricNullness K var1, @ParametricNullness V1 var2);
    }

    private static class UnmodifiableBiMap<K, V>
    extends ForwardingMap<K, V>
    implements BiMap<K, V>,
    Serializable {
        final Map<K, V> unmodifiableMap;
        final BiMap<? extends K, ? extends V> delegate;
        @LazyInit
        @CheckForNull
        @RetainedWith
        BiMap<V, K> inverse;
        @LazyInit
        @CheckForNull
        transient Set<V> values;
        private static final long serialVersionUID = 0L;

        UnmodifiableBiMap(BiMap<? extends K, ? extends V> delegate, @CheckForNull BiMap<V, K> inverse) {
            this.unmodifiableMap = Collections.unmodifiableMap(delegate);
            this.delegate = delegate;
            this.inverse = inverse;
        }

        @Override
        protected Map<K, V> delegate() {
            return this.unmodifiableMap;
        }

        @Override
        @CheckForNull
        public V forcePut(@ParametricNullness K key, @ParametricNullness V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(@Nullable Object key, @Nullable Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends @Nullable V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V compute(K key, BiFunction<? super K, ? super @Nullable V, ? extends @Nullable V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        @CheckForNull
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends @Nullable V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BiMap<V, K> inverse() {
            BiMap<K, V> result = this.inverse;
            return result == null ? (this.inverse = new UnmodifiableBiMap<V, K>(this.delegate.inverse(), this)) : result;
        }

        @Override
        public Set<V> values() {
            Set<V> result = this.values;
            return result == null ? (this.values = Collections.unmodifiableSet(this.delegate.values())) : result;
        }
    }

    private static final class BiMapConverter<A, B>
    extends Converter<A, B>
    implements Serializable {
        private final BiMap<A, B> bimap;
        private static final long serialVersionUID = 0L;

        BiMapConverter(BiMap<A, B> bimap) {
            this.bimap = Preconditions.checkNotNull(bimap);
        }

        @Override
        protected B doForward(A a) {
            return BiMapConverter.convert(this.bimap, a);
        }

        @Override
        protected A doBackward(B b) {
            return BiMapConverter.convert(this.bimap.inverse(), b);
        }

        private static <X, Y> Y convert(BiMap<X, Y> bimap, X input) {
            Object output = bimap.get(input);
            Preconditions.checkArgument(output != null, "No non-null mapping present for input: %s", input);
            return (Y)output;
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object instanceof BiMapConverter) {
                BiMapConverter that = (BiMapConverter)object;
                return this.bimap.equals(that.bimap);
            }
            return false;
        }

        public int hashCode() {
            return this.bimap.hashCode();
        }

        public String toString() {
            return "Maps.asConverter(" + this.bimap + ")";
        }
    }

    static class UnmodifiableEntrySet<K, V>
    extends UnmodifiableEntries<K, V>
    implements Set<Map.Entry<K, V>> {
        UnmodifiableEntrySet(Set<Map.Entry<K, V>> entries) {
            super(entries);
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            return Sets.equalsImpl(this, object);
        }

        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    static class UnmodifiableEntries<K, V>
    extends ForwardingCollection<Map.Entry<K, V>> {
        private final Collection<Map.Entry<K, V>> entries;

        UnmodifiableEntries(Collection<Map.Entry<K, V>> entries) {
            this.entries = entries;
        }

        @Override
        protected Collection<Map.Entry<K, V>> delegate() {
            return this.entries;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return Maps.unmodifiableEntryIterator(this.entries.iterator());
        }

        @Override
        public @Nullable Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] array) {
            return this.standardToArray(array);
        }
    }

    @GwtIncompatible
    private static final class NavigableAsMapView<K, V>
    extends AbstractNavigableMap<K, V> {
        private final NavigableSet<K> set;
        private final Function<? super K, V> function;

        NavigableAsMapView(NavigableSet<K> ks, Function<? super K, V> vFunction) {
            this.set = Preconditions.checkNotNull(ks);
            this.function = Preconditions.checkNotNull(vFunction);
        }

        @Override
        public NavigableMap<K, V> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
            return Maps.asMap(this.set.subSet(fromKey, fromInclusive, toKey, toInclusive), this.function);
        }

        @Override
        public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
            return Maps.asMap(this.set.headSet(toKey, inclusive), this.function);
        }

        @Override
        public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
            return Maps.asMap(this.set.tailSet(fromKey, inclusive), this.function);
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.set.comparator();
        }

        @Override
        @CheckForNull
        public V get(@CheckForNull Object key) {
            return this.getOrDefault(key, null);
        }

        @Override
        @CheckForNull
        public V getOrDefault(@CheckForNull Object key, @CheckForNull V defaultValue) {
            if (Collections2.safeContains(this.set, key)) {
                Object k = key;
                return this.function.apply((K)k);
            }
            return defaultValue;
        }

        @Override
        public void clear() {
            this.set.clear();
        }

        @Override
        Iterator<Map.Entry<K, V>> entryIterator() {
            return Maps.asMapEntryIterator(this.set, this.function);
        }

        @Override
        Spliterator<Map.Entry<K, V>> entrySpliterator() {
            return CollectSpliterators.map(this.set.spliterator(), e -> Maps.immutableEntry(e, this.function.apply((K)e)));
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            this.set.forEach((? super T k) -> action.accept((K)k, (V)this.function.apply((K)k)));
        }

        @Override
        Iterator<Map.Entry<K, V>> descendingEntryIterator() {
            return this.descendingMap().entrySet().iterator();
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return Maps.removeOnlyNavigableSet(this.set);
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            return Maps.asMap(this.set.descendingSet(), this.function);
        }
    }

    private static class SortedAsMapView<K, V>
    extends AsMapView<K, V>
    implements SortedMap<K, V> {
        SortedAsMapView(SortedSet<K> set, Function<? super K, V> function) {
            super(set, function);
        }

        @Override
        SortedSet<K> backingSet() {
            return (SortedSet)super.backingSet();
        }

        @Override
        @CheckForNull
        public Comparator<? super K> comparator() {
            return this.backingSet().comparator();
        }

        @Override
        public Set<K> keySet() {
            return Maps.removeOnlySortedSet((SortedSet)this.backingSet());
        }

        @Override
        public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
            return Maps.asMap(this.backingSet().subSet(fromKey, toKey), this.function);
        }

        @Override
        public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
            return Maps.asMap(this.backingSet().headSet(toKey), this.function);
        }

        @Override
        public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
            return Maps.asMap(this.backingSet().tailSet(fromKey), this.function);
        }

        @Override
        @ParametricNullness
        public K firstKey() {
            return (K)this.backingSet().first();
        }

        @Override
        @ParametricNullness
        public K lastKey() {
            return (K)this.backingSet().last();
        }
    }

    private static class AsMapView<K, V>
    extends ViewCachingAbstractMap<K, V> {
        private final Set<K> set;
        final Function<? super K, V> function;

        Set<K> backingSet() {
            return this.set;
        }

        AsMapView(Set<K> set, Function<? super K, V> function) {
            this.set = Preconditions.checkNotNull(set);
            this.function = Preconditions.checkNotNull(function);
        }

        @Override
        public Set<K> createKeySet() {
            return Maps.removeOnlySet(this.backingSet());
        }

        @Override
        Collection<V> createValues() {
            return Collections2.transform(this.set, this.function);
        }

        @Override
        public int size() {
            return this.backingSet().size();
        }

        @Override
        public boolean containsKey(@CheckForNull Object key) {
            return this.backingSet().contains(key);
        }

        @Override
        @CheckForNull
        public V get(@CheckForNull Object key) {
            return this.getOrDefault(key, null);
        }

        @Override
        @CheckForNull
        public V getOrDefault(@CheckForNull Object key, @CheckForNull V defaultValue) {
            if (Collections2.safeContains(this.backingSet(), key)) {
                Object k = key;
                return this.function.apply((K)k);
            }
            return defaultValue;
        }

        @Override
        @CheckForNull
        public V remove(@CheckForNull Object key) {
            if (this.backingSet().remove(key)) {
                Object k = key;
                return this.function.apply((K)k);
            }
            return null;
        }

        @Override
        public void clear() {
            this.backingSet().clear();
        }

        @Override
        protected Set<Map.Entry<K, V>> createEntrySet() {
            class EntrySetImpl
            extends EntrySet<K, V> {
                EntrySetImpl() {
                }

                @Override
                Map<K, V> map() {
                    return AsMapView.this;
                }

                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
                }
            }
            return new EntrySetImpl();
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            Preconditions.checkNotNull(action);
            this.backingSet().forEach((? super T k) -> action.accept((K)k, (V)this.function.apply((K)k)));
        }
    }

    static class SortedMapDifferenceImpl<K, V>
    extends MapDifferenceImpl<K, V>
    implements SortedMapDifference<K, V> {
        SortedMapDifferenceImpl(SortedMap<K, V> onlyOnLeft, SortedMap<K, V> onlyOnRight, SortedMap<K, V> onBoth, SortedMap<K, MapDifference.ValueDifference<V>> differences) {
            super(onlyOnLeft, onlyOnRight, onBoth, differences);
        }

        @Override
        public SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering() {
            return (SortedMap)super.entriesDiffering();
        }

        @Override
        public SortedMap<K, V> entriesInCommon() {
            return (SortedMap)super.entriesInCommon();
        }

        @Override
        public SortedMap<K, V> entriesOnlyOnLeft() {
            return (SortedMap)super.entriesOnlyOnLeft();
        }

        @Override
        public SortedMap<K, V> entriesOnlyOnRight() {
            return (SortedMap)super.entriesOnlyOnRight();
        }
    }

    static class ValueDifferenceImpl<V>
    implements MapDifference.ValueDifference<V> {
        @ParametricNullness
        private final V left;
        @ParametricNullness
        private final V right;

        static <V> MapDifference.ValueDifference<V> create(@ParametricNullness V left, @ParametricNullness V right) {
            return new ValueDifferenceImpl<V>(left, right);
        }

        private ValueDifferenceImpl(@ParametricNullness V left, @ParametricNullness V right) {
            this.left = left;
            this.right = right;
        }

        @Override
        @ParametricNullness
        public V leftValue() {
            return this.left;
        }

        @Override
        @ParametricNullness
        public V rightValue() {
            return this.right;
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object instanceof MapDifference.ValueDifference) {
                MapDifference.ValueDifference that = (MapDifference.ValueDifference)object;
                return com.google.common.base.Objects.equal(this.left, that.leftValue()) && com.google.common.base.Objects.equal(this.right, that.rightValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(this.left, this.right);
        }

        public String toString() {
            return "(" + this.left + ", " + this.right + ")";
        }
    }

    static class MapDifferenceImpl<K, V>
    implements MapDifference<K, V> {
        final Map<K, V> onlyOnLeft;
        final Map<K, V> onlyOnRight;
        final Map<K, V> onBoth;
        final Map<K, MapDifference.ValueDifference<V>> differences;

        MapDifferenceImpl(Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences) {
            this.onlyOnLeft = Maps.unmodifiableMap(onlyOnLeft);
            this.onlyOnRight = Maps.unmodifiableMap(onlyOnRight);
            this.onBoth = Maps.unmodifiableMap(onBoth);
            this.differences = Maps.unmodifiableMap(differences);
        }

        @Override
        public boolean areEqual() {
            return this.onlyOnLeft.isEmpty() && this.onlyOnRight.isEmpty() && this.differences.isEmpty();
        }

        @Override
        public Map<K, V> entriesOnlyOnLeft() {
            return this.onlyOnLeft;
        }

        @Override
        public Map<K, V> entriesOnlyOnRight() {
            return this.onlyOnRight;
        }

        @Override
        public Map<K, V> entriesInCommon() {
            return this.onBoth;
        }

        @Override
        public Map<K, MapDifference.ValueDifference<V>> entriesDiffering() {
            return this.differences;
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof MapDifference) {
                MapDifference other = (MapDifference)object;
                return this.entriesOnlyOnLeft().equals(other.entriesOnlyOnLeft()) && this.entriesOnlyOnRight().equals(other.entriesOnlyOnRight()) && this.entriesInCommon().equals(other.entriesInCommon()) && this.entriesDiffering().equals(other.entriesDiffering());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(this.entriesOnlyOnLeft(), this.entriesOnlyOnRight(), this.entriesInCommon(), this.entriesDiffering());
        }

        public String toString() {
            if (this.areEqual()) {
                return "equal";
            }
            StringBuilder result = new StringBuilder("not equal");
            if (!this.onlyOnLeft.isEmpty()) {
                result.append(": only on left=").append(this.onlyOnLeft);
            }
            if (!this.onlyOnRight.isEmpty()) {
                result.append(": only on right=").append(this.onlyOnRight);
            }
            if (!this.differences.isEmpty()) {
                result.append(": value differences=").append(this.differences);
            }
            return result.toString();
        }
    }

    private static enum EntryFunction implements Function<Map.Entry<?, ?>, Object>
    {
        KEY{

            @Override
            @CheckForNull
            public Object apply(Map.Entry<?, ?> entry) {
                return entry.getKey();
            }
        }
        ,
        VALUE{

            @Override
            @CheckForNull
            public Object apply(Map.Entry<?, ?> entry) {
                return entry.getValue();
            }
        };

    }
}

