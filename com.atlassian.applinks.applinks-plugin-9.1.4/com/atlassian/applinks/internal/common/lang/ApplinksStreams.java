/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.lang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ApplinksStreams {
    private ApplinksStreams() {
    }

    @Nullable
    public static <T> T firstOrNull(@Nonnull Iterable<T> iterable) {
        return (T)Iterables.getFirst(iterable, null);
    }

    @Nonnull
    public static <T> Stream<T> toStream(@Nonnull Iterable<T> source) {
        return StreamSupport.stream(source.spliterator(), false);
    }

    @Nonnull
    public static <T> Iterable<T> toIterable(@Nonnull Stream<T> source) {
        return ImmutableList.copyOf(source::iterator);
    }

    @Nonnull
    public static <T, A extends List<T>> Collector<T, A, List<T>> toImmutableList(@Nonnull Supplier<A> listFactory) {
        return Collector.of(listFactory, List::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableList, new Collector.Characteristics[0]);
    }

    @Nonnull
    public static <T> Collector<T, List<T>, List<T>> toImmutableList() {
        return ApplinksStreams.toImmutableList(ArrayList::new);
    }

    @Nonnull
    public static <T, A extends Set<T>> Collector<T, A, Set<T>> toImmutableSet(@Nonnull Supplier<A> setFactory) {
        return Collector.of(setFactory, Set::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableSet, new Collector.Characteristics[0]);
    }

    @Nonnull
    public static <T> Collector<T, Set<T>, Set<T>> toImmutableSet() {
        return ApplinksStreams.toImmutableSet(LinkedHashSet::new);
    }

    @Nonnull
    public static <T, K, V, A extends Map<K, V>> Collector<T, A, Map<K, V>> toImmutableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper, @Nonnull Supplier<A> mapFactory) {
        BiConsumer<Map, Object> accumulator = (map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element));
        BinaryOperator combiner = (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
        return Collector.of(mapFactory, accumulator, combiner, Collections::unmodifiableMap, new Collector.Characteristics[0]);
    }

    @Nonnull
    public static <T, K, V> Collector<T, Map<K, V>, Map<K, V>> toImmutableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return ApplinksStreams.toImmutableMap(keyMapper, valueMapper, LinkedHashMap::new);
    }

    @Nonnull
    public static <K, V> Collector<Map.Entry<K, V>, Map<K, V>, Map<K, V>> entryToMap() {
        return ApplinksStreams.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}

