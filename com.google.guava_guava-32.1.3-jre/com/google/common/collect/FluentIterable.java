/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.InlineMe
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.Streams;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
public abstract class FluentIterable<E>
implements Iterable<E> {
    private final Optional<Iterable<E>> iterableDelegate;

    protected FluentIterable() {
        this.iterableDelegate = Optional.absent();
    }

    FluentIterable(Iterable<E> iterable) {
        this.iterableDelegate = Optional.of(iterable);
    }

    private Iterable<E> getDelegate() {
        return this.iterableDelegate.or(this);
    }

    public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
        return iterable instanceof FluentIterable ? (FluentIterable<E>)iterable : new FluentIterable<E>(iterable){

            @Override
            public Iterator<E> iterator() {
                return iterable.iterator();
            }
        };
    }

    public static <E> FluentIterable<E> from(E[] elements) {
        return FluentIterable.from(Arrays.asList(elements));
    }

    @Deprecated
    @InlineMe(replacement="checkNotNull(iterable)", staticImports={"com.google.common.base.Preconditions.checkNotNull"})
    public static <E> FluentIterable<E> from(FluentIterable<E> iterable) {
        return Preconditions.checkNotNull(iterable);
    }

    public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
        return FluentIterable.concatNoDefensiveCopy(a, b);
    }

    public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c) {
        return FluentIterable.concatNoDefensiveCopy(a, b, c);
    }

    public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c, Iterable<? extends T> d) {
        return FluentIterable.concatNoDefensiveCopy(a, b, c, d);
    }

    public static <T> FluentIterable<T> concat(Iterable<? extends T> ... inputs) {
        return FluentIterable.concatNoDefensiveCopy(Arrays.copyOf(inputs, inputs.length));
    }

    public static <T> FluentIterable<T> concat(final Iterable<? extends Iterable<? extends T>> inputs) {
        Preconditions.checkNotNull(inputs);
        return new FluentIterable<T>(){

            @Override
            public Iterator<T> iterator() {
                return Iterators.concat(Iterators.transform(inputs.iterator(), Iterable::iterator));
            }
        };
    }

    private static <T> FluentIterable<T> concatNoDefensiveCopy(final Iterable<? extends T> ... inputs) {
        for (Iterable<? extends T> input : inputs) {
            Preconditions.checkNotNull(input);
        }
        return new FluentIterable<T>(){

            @Override
            public Iterator<T> iterator() {
                return Iterators.concat(new AbstractIndexedListIterator<Iterator<? extends T>>(inputs.length){

                    @Override
                    public Iterator<? extends T> get(int i) {
                        return inputs[i].iterator();
                    }
                });
            }
        };
    }

    public static <E> FluentIterable<E> of() {
        return FluentIterable.from(Collections.emptyList());
    }

    public static <E> FluentIterable<E> of(@ParametricNullness E element, E ... elements) {
        return FluentIterable.from(Lists.asList(element, elements));
    }

    public String toString() {
        return Iterables.toString(this.getDelegate());
    }

    public final int size() {
        return Iterables.size(this.getDelegate());
    }

    public final boolean contains(@CheckForNull Object target) {
        return Iterables.contains(this.getDelegate(), target);
    }

    public final FluentIterable<E> cycle() {
        return FluentIterable.from(Iterables.cycle(this.getDelegate()));
    }

    public final FluentIterable<E> append(Iterable<? extends E> other) {
        return FluentIterable.concat(this.getDelegate(), other);
    }

    public final FluentIterable<E> append(E ... elements) {
        return FluentIterable.concat(this.getDelegate(), Arrays.asList(elements));
    }

    public final FluentIterable<E> filter(Predicate<? super E> predicate) {
        return FluentIterable.from(Iterables.filter(this.getDelegate(), predicate));
    }

    @GwtIncompatible
    public final <T> FluentIterable<T> filter(Class<T> type) {
        return FluentIterable.from(Iterables.filter(this.getDelegate(), type));
    }

    public final boolean anyMatch(Predicate<? super E> predicate) {
        return Iterables.any(this.getDelegate(), predicate);
    }

    public final boolean allMatch(Predicate<? super E> predicate) {
        return Iterables.all(this.getDelegate(), predicate);
    }

    public final Optional<@NonNull E> firstMatch(Predicate<? super E> predicate) {
        return Iterables.tryFind(this.getDelegate(), predicate);
    }

    public final <T> FluentIterable<T> transform(Function<? super E, T> function) {
        return FluentIterable.from(Iterables.transform(this.getDelegate(), function));
    }

    public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> function) {
        return FluentIterable.concat(this.transform(function));
    }

    public final Optional<@NonNull E> first() {
        Iterator<E> iterator = this.getDelegate().iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.absent();
    }

    public final Optional<@NonNull E> last() {
        E current;
        Iterable<E> iterable = this.getDelegate();
        if (iterable instanceof List) {
            List list = (List)iterable;
            if (list.isEmpty()) {
                return Optional.absent();
            }
            return Optional.of(list.get(list.size() - 1));
        }
        Iterator<E> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return Optional.absent();
        }
        if (iterable instanceof SortedSet) {
            SortedSet sortedSet = (SortedSet)iterable;
            return Optional.of(sortedSet.last());
        }
        do {
            current = iterator.next();
        } while (iterator.hasNext());
        return Optional.of(current);
    }

    public final FluentIterable<E> skip(int numberToSkip) {
        return FluentIterable.from(Iterables.skip(this.getDelegate(), numberToSkip));
    }

    public final FluentIterable<E> limit(int maxSize) {
        return FluentIterable.from(Iterables.limit(this.getDelegate(), maxSize));
    }

    public final boolean isEmpty() {
        return !this.getDelegate().iterator().hasNext();
    }

    public final ImmutableList<@NonNull E> toList() {
        return ImmutableList.copyOf(this.getDelegate());
    }

    public final ImmutableList<@NonNull E> toSortedList(Comparator<? super E> comparator) {
        return Ordering.from(comparator).immutableSortedCopy(this.getDelegate());
    }

    public final ImmutableSet<@NonNull E> toSet() {
        return ImmutableSet.copyOf(this.getDelegate());
    }

    public final ImmutableSortedSet<@NonNull E> toSortedSet(Comparator<? super E> comparator) {
        return ImmutableSortedSet.copyOf(comparator, this.getDelegate());
    }

    public final ImmutableMultiset<@NonNull E> toMultiset() {
        return ImmutableMultiset.copyOf(this.getDelegate());
    }

    public final <V> ImmutableMap<@NonNull E, V> toMap(Function<? super E, V> valueFunction) {
        return Maps.toMap(this.getDelegate(), valueFunction);
    }

    public final <K> ImmutableListMultimap<K, @NonNull E> index(Function<? super E, K> keyFunction) {
        return Multimaps.index(this.getDelegate(), keyFunction);
    }

    public final <K> ImmutableMap<K, @NonNull E> uniqueIndex(Function<? super E, K> keyFunction) {
        return Maps.uniqueIndex(this.getDelegate(), keyFunction);
    }

    @GwtIncompatible
    public final E[] toArray(Class<@NonNull E> type) {
        return Iterables.toArray(this.getDelegate(), type);
    }

    @CanIgnoreReturnValue
    public final <C extends Collection<? super E>> C copyInto(C collection) {
        Preconditions.checkNotNull(collection);
        Iterable<E> iterable = this.getDelegate();
        if (iterable instanceof Collection) {
            collection.addAll((Collection)iterable);
        } else {
            for (E item : iterable) {
                collection.add(item);
            }
        }
        return collection;
    }

    public final String join(Joiner joiner) {
        return joiner.join(this);
    }

    @ParametricNullness
    public final E get(int position) {
        return Iterables.get(this.getDelegate(), position);
    }

    public final Stream<E> stream() {
        return Streams.stream(this.getDelegate());
    }

    private static class FromIterableFunction<E>
    implements Function<Iterable<E>, FluentIterable<E>> {
        private FromIterableFunction() {
        }

        @Override
        public FluentIterable<E> apply(Iterable<E> fromObject) {
            return FluentIterable.from(fromObject);
        }
    }
}

