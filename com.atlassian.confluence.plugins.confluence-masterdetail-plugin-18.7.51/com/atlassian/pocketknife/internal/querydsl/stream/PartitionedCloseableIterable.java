/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.atlassian.pocketknife.internal.querydsl.stream;

import com.atlassian.annotations.Internal;
import com.atlassian.pocketknife.api.querydsl.stream.CloseableIterable;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.types.Expression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Internal
public class PartitionedCloseableIterable<T>
implements CloseableIterable<T> {
    private final List<CloseableIterable<T>> iterables;

    public PartitionedCloseableIterable(List<CloseableIterable<T>> iterables) {
        this.iterables = iterables == null ? Collections.emptyList() : ImmutableList.copyOf(iterables);
    }

    @Override
    public CloseableIterator<T> iterator() {
        return new PartitionedCloseableIterator(this.iterables.stream().map(CloseableIterable::iterator).collect(Collectors.toList()));
    }

    @Override
    public Optional<T> fetchFirst() {
        try {
            Optional optional = this.iterables.stream().findFirst().flatMap(CloseableIterable::fetchFirst);
            return optional;
        }
        finally {
            this.close();
        }
    }

    @Override
    public CloseableIterable<T> take(int n) {
        ArrayList newIterables = Lists.newArrayList();
        AtomicInteger count = new AtomicInteger(0);
        for (CloseableIterable<Object> closeableIterable : this.iterables) {
            newIterables.add(closeableIterable.takeWhile(t -> count.getAndIncrement() < n));
        }
        return new PartitionedCloseableIterable<T>(newIterables);
    }

    @Override
    public CloseableIterable<T> takeWhile(Predicate<T> takeWhilePredicate) {
        ArrayList newIterables = Lists.newArrayList();
        AtomicBoolean ok = new AtomicBoolean(true);
        for (CloseableIterable<Object> closeableIterable : this.iterables) {
            newIterables.add(closeableIterable.takeWhile(t -> {
                if (ok.get()) {
                    ok.set(takeWhilePredicate.test(t));
                }
                return ok.get();
            }));
        }
        return new PartitionedCloseableIterable<T>(newIterables);
    }

    @Override
    public CloseableIterable<T> filter(Predicate<T> filterPredicate) {
        ArrayList newIterables = Lists.newArrayList();
        for (CloseableIterable<T> iterable : this.iterables) {
            newIterables.add(iterable.filter(filterPredicate));
        }
        return new PartitionedCloseableIterable<T>(newIterables);
    }

    @Override
    public <D> CloseableIterable<D> map(Function<T, D> mapper) {
        ArrayList newIterables = Lists.newArrayList();
        for (CloseableIterable<T> iterable : this.iterables) {
            newIterables.add(iterable.map(mapper));
        }
        return new PartitionedCloseableIterable<T>(newIterables);
    }

    @Override
    public <D> CloseableIterable<D> map(Expression<D> expr) {
        ArrayList newIterables = Lists.newArrayList();
        for (CloseableIterable<T> iterable : this.iterables) {
            newIterables.add(iterable.map(expr));
        }
        return new PartitionedCloseableIterable<T>(newIterables);
    }

    @Override
    public <D> D foldLeft(D initial, BiFunction<D, T, D> combiningFunction) {
        D value = initial;
        for (CloseableIterable<T> iterable : this.iterables) {
            value = iterable.foldLeft(value, combiningFunction);
        }
        return value;
    }

    @Override
    public void foreach(Consumer<T> effect) {
        this.iterables.forEach(i -> i.foreach(effect));
    }

    @Override
    public void close() {
        this.iterables.forEach(CloseableIterable::close);
    }

    @VisibleForTesting
    static class PartitionedCloseableIterator<T>
    implements CloseableIterator<T> {
        private final List<CloseableIterator<T>> iterators;

        PartitionedCloseableIterator(List<CloseableIterator<T>> iterators) {
            this.iterators = iterators == null ? Collections.emptyList() : ImmutableList.copyOf(iterators);
        }

        @Override
        public void close() {
            this.iterators.forEach(CloseableIterator::close);
        }

        @Override
        public boolean hasNext() {
            return this.iterators.stream().anyMatch(Iterator::hasNext);
        }

        @Override
        public T next() {
            Optional<CloseableIterator> first = this.iterators.stream().filter(Iterator::hasNext).findFirst();
            if (first.isPresent()) {
                return (T)first.get().next();
            }
            throw new NoSuchElementException();
        }
    }
}

