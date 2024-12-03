/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.mysema.commons.lang.Pair;
import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GAvg;
import com.querydsl.core.group.GList;
import com.querydsl.core.group.GMap;
import com.querydsl.core.group.GMax;
import com.querydsl.core.group.GMin;
import com.querydsl.core.group.GOne;
import com.querydsl.core.group.GSet;
import com.querydsl.core.group.GSum;
import com.querydsl.core.group.GroupByBuilder;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.group.MixinGroupExpression;
import com.querydsl.core.group.QPair;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public final class GroupBy {
    public static <K> GroupByBuilder<K> groupBy(Expression<K> key) {
        return new GroupByBuilder<K>(key);
    }

    public static GroupByBuilder<List<?>> groupBy(Expression<?> ... keys) {
        return new GroupByBuilder(Projections.list(keys));
    }

    public static <E extends Comparable<? super E>> AbstractGroupExpression<E, E> min(Expression<E> expression) {
        return new GMin<E>(expression);
    }

    public static <E extends Number> AbstractGroupExpression<E, E> sum(Expression<E> expression) {
        return new GSum<E>(expression);
    }

    public static <E extends Number> AbstractGroupExpression<E, E> avg(Expression<E> expression) {
        return new GAvg<E>(expression);
    }

    public static <E extends Comparable<? super E>> AbstractGroupExpression<E, E> max(Expression<E> expression) {
        return new GMax<E>(expression);
    }

    public static <E> AbstractGroupExpression<E, List<E>> list(Expression<E> expression) {
        return new GList<E>(expression);
    }

    public static <E, F> AbstractGroupExpression<E, List<F>> list(GroupExpression<E, F> groupExpression) {
        return new MixinGroupExpression(groupExpression, new GList(groupExpression));
    }

    public static <E> AbstractGroupExpression<E, Set<E>> set(Expression<E> expression) {
        return GSet.createLinked(expression);
    }

    public static <E, F> GroupExpression<E, Set<F>> set(GroupExpression<E, F> groupExpression) {
        return new MixinGroupExpression(groupExpression, GSet.createLinked(groupExpression));
    }

    public static <E extends Comparable<? super E>> AbstractGroupExpression<E, SortedSet<E>> sortedSet(Expression<E> expression) {
        return GSet.createSorted(expression);
    }

    public static <E, F extends Comparable<? super F>> GroupExpression<E, SortedSet<F>> sortedSet(GroupExpression<E, F> groupExpression) {
        return new MixinGroupExpression(groupExpression, GSet.createSorted(groupExpression));
    }

    public static <E> AbstractGroupExpression<E, SortedSet<E>> sortedSet(Expression<E> expression, Comparator<? super E> comparator) {
        return GSet.createSorted(expression, comparator);
    }

    public static <E, F> GroupExpression<E, SortedSet<F>> sortedSet(GroupExpression<E, F> groupExpression, Comparator<? super F> comparator) {
        return new MixinGroupExpression<E, F, SortedSet<? super F>>(groupExpression, GSet.createSorted(groupExpression, comparator));
    }

    public static <K, V> AbstractGroupExpression<Pair<K, V>, Map<K, V>> map(Expression<K> key, Expression<V> value) {
        return GMap.createLinked(QPair.create(key, value));
    }

    public static <K, V, T> AbstractGroupExpression<Pair<K, V>, Map<T, V>> map(GroupExpression<K, T> key, Expression<V> value) {
        return GroupBy.map(key, new GOne<V>(value));
    }

    public static <K, V, U> AbstractGroupExpression<Pair<K, V>, Map<K, U>> map(Expression<K> key, GroupExpression<V, U> value) {
        return GroupBy.map(new GOne<K>(key), value);
    }

    public static <K, V, T, U> AbstractGroupExpression<Pair<K, V>, Map<T, U>> map(GroupExpression<K, T> key, GroupExpression<V, U> value) {
        return new GMap.Mixin(key, value, GMap.createLinked(QPair.create(key, value)));
    }

    public static <K extends Comparable<? super K>, V> AbstractGroupExpression<Pair<K, V>, SortedMap<K, V>> sortedMap(Expression<K> key, Expression<V> value) {
        return GMap.createSorted(QPair.create(key, value));
    }

    public static <K, V, T extends Comparable<? super T>> AbstractGroupExpression<Pair<K, V>, SortedMap<T, V>> sortedMap(GroupExpression<K, T> key, Expression<V> value) {
        return GroupBy.sortedMap(key, new GOne<V>(value));
    }

    public static <K extends Comparable<? super K>, V, U> AbstractGroupExpression<Pair<K, V>, SortedMap<K, U>> sortedMap(Expression<K> key, GroupExpression<V, U> value) {
        return GroupBy.sortedMap(new GOne<K>(key), value);
    }

    public static <K, V, T extends Comparable<? super T>, U> AbstractGroupExpression<Pair<K, V>, SortedMap<T, U>> sortedMap(GroupExpression<K, T> key, GroupExpression<V, U> value) {
        return new GMap.Mixin(key, value, GMap.createSorted(QPair.create(key, value)));
    }

    public static <K, V> AbstractGroupExpression<Pair<K, V>, SortedMap<K, V>> sortedMap(Expression<K> key, Expression<V> value, Comparator<? super K> comparator) {
        return GMap.createSorted(QPair.create(key, value), comparator);
    }

    public static <K, V, T> AbstractGroupExpression<Pair<K, V>, SortedMap<T, V>> sortedMap(GroupExpression<K, T> key, Expression<V> value, Comparator<? super T> comparator) {
        return GroupBy.sortedMap(key, new GOne<V>(value), comparator);
    }

    public static <K, V, U> AbstractGroupExpression<Pair<K, V>, SortedMap<K, U>> sortedMap(Expression<K> key, GroupExpression<V, U> value, Comparator<? super K> comparator) {
        return GroupBy.sortedMap(new GOne<K>(key), value, comparator);
    }

    public static <K, V, T, U> AbstractGroupExpression<Pair<K, V>, SortedMap<T, U>> sortedMap(GroupExpression<K, T> key, GroupExpression<V, U> value, Comparator<? super T> comparator) {
        return new GMap.Mixin(key, value, GMap.createSorted(QPair.create(key, value), comparator));
    }

    private GroupBy() {
    }
}

