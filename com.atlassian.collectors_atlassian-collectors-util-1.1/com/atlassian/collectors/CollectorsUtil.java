/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ImmutableListMultimap$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.SetMultimap
 *  javax.annotation.Nonnull
 */
package com.atlassian.collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class CollectorsUtil {
    public static <T> Collector<T, ?, ArrayList<T>> toNewArrayListWithCapacity(int capacity) {
        return Collectors.toCollection(() -> Lists.newArrayListWithCapacity((int)capacity));
    }

    public static <T> Collector<T, ?, ArrayList<T>> toNewArrayListWithSizeOf(@Nonnull Collection<?> other) {
        return CollectorsUtil.toNewArrayListWithCapacity(other.size());
    }

    public static <T> Collector<T, ?, ArrayList<T>> toNewArrayListWithSizeOf(@Nonnull Map<?, ?> other) {
        return CollectorsUtil.toNewArrayListWithCapacity(other.size());
    }

    public static <T, C extends Collection<T>> Collector<T, ?, C> appendingTo(@Nonnull C mutableCollection) {
        if (mutableCollection == null) {
            throw new NullPointerException();
        }
        return Collectors.toCollection(() -> mutableCollection);
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
        return CollectorsUtil.immutableListCollector(ArrayList::new);
    }

    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collector.of(ImmutableMap::builder, (builder, element) -> builder.put(keyMapper.apply(element), valueMapper.apply(element)), (m1, m2) -> m1.putAll((Map)m2.build()), ImmutableMap.Builder::build, Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableListWithCapacity(int capacity) {
        return CollectorsUtil.immutableListCollector(() -> Lists.newArrayListWithCapacity((int)capacity));
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableListWithSizeOf(@Nonnull Collection<?> other) {
        return CollectorsUtil.toImmutableListWithCapacity(other.size());
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableListWithSizeOf(@Nonnull Map<?, ?> other) {
        return CollectorsUtil.toImmutableListWithCapacity(other.size());
    }

    private static <T> Collector<T, ?, ImmutableList<T>> immutableListCollector(final Supplier<ArrayList<T>> accumulatorSupplier) {
        return new Collector<T, ArrayList<T>, ImmutableList<T>>(){

            @Override
            public Supplier<ArrayList<T>> supplier() {
                return accumulatorSupplier;
            }

            @Override
            public BiConsumer<ArrayList<T>, T> accumulator() {
                return ArrayList::add;
            }

            @Override
            public BinaryOperator<ArrayList<T>> combiner() {
                return (array1, array2) -> {
                    array1.ensureCapacity(array1.size() + array2.size());
                    for (Object element : array2) {
                        array1.add(element);
                    }
                    return array1;
                };
            }

            @Override
            public Function<ArrayList<T>, ImmutableList<T>> finisher() {
                return ImmutableList::copyOf;
            }

            @Override
            public Set<Collector.Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
        return CollectorsUtil.immutableSetCollector(ArrayList::new);
    }

    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSetWithCapacity(int capacity) {
        return CollectorsUtil.immutableSetCollector(() -> Lists.newArrayListWithCapacity((int)capacity));
    }

    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSetWithSizeOf(@Nonnull Collection<?> other) {
        return CollectorsUtil.toImmutableSetWithCapacity(other.size());
    }

    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSetWithSizeOf(@Nonnull Map<?, ?> other) {
        return CollectorsUtil.toImmutableSetWithCapacity(other.size());
    }

    private static <T> Collector<T, ?, ImmutableSet<T>> immutableSetCollector(final Supplier<ArrayList<T>> accumulatorSupplier) {
        return new Collector<T, ArrayList<T>, ImmutableSet<T>>(){

            @Override
            public Supplier<ArrayList<T>> supplier() {
                return accumulatorSupplier;
            }

            @Override
            public BiConsumer<ArrayList<T>, T> accumulator() {
                return ArrayList::add;
            }

            @Override
            public BinaryOperator<ArrayList<T>> combiner() {
                return (array1, array2) -> {
                    array1.ensureCapacity(array1.size() + array2.size());
                    for (Object element : array2) {
                        array1.add(element);
                    }
                    return array1;
                };
            }

            @Override
            public Function<ArrayList<T>, ImmutableSet<T>> finisher() {
                return ImmutableSet::copyOf;
            }

            @Override
            public Set<Collector.Characteristics> characteristics() {
                return EnumSet.of(Collector.Characteristics.UNORDERED);
            }
        };
    }

    public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultiMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collector.of(ImmutableSetMultimap::builder, (builder, element) -> builder.put(keyMapper.apply(element), valueMapper.apply(element)), (m1, m2) -> m1.putAll((Multimap)m2.build()), ImmutableSetMultimap.Builder::build, new Collector.Characteristics[0]);
    }

    public static <T, K, V> Collector<T, ?, SetMultimap<K, V>> toUnmodifiableSetMultiMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collector.of(LinkedHashMultimap::create, (map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element)), (map1, map2) -> {
            map1.putAll((Multimap)map2);
            return map1;
        }, Multimaps::unmodifiableSetMultimap, Collector.Characteristics.UNORDERED);
    }

    public static <T, K, V> Collector<T, ?, SetMultimap<K, V>> toMutableSetMultiMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collector.of(LinkedHashMultimap::create, (map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element)), (map1, map2) -> {
            map1.putAll((Multimap)map2);
            return map1;
        }, Collector.Characteristics.UNORDERED);
    }

    public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultiMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collector.of(ImmutableListMultimap::builder, (builder, element) -> builder.put(keyMapper.apply(element), valueMapper.apply(element)), (m1, m2) -> m1.putAll((Multimap)m2.build()), ImmutableListMultimap.Builder::build, new Collector.Characteristics[0]);
    }
}

