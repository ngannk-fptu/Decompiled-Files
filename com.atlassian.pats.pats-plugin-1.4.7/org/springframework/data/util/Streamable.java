/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.data.util.LazyStreamable;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.Assert;

@FunctionalInterface
public interface Streamable<T>
extends Iterable<T>,
Supplier<Stream<T>> {
    public static <T> Streamable<T> empty() {
        return Collections::emptyIterator;
    }

    @SafeVarargs
    public static <T> Streamable<T> of(T ... t) {
        return () -> Arrays.asList(t).iterator();
    }

    public static <T> Streamable<T> of(Iterable<T> iterable) {
        Assert.notNull(iterable, (String)"Iterable must not be null!");
        return iterable::iterator;
    }

    public static <T> Streamable<T> of(Supplier<? extends Stream<T>> supplier) {
        return LazyStreamable.of(supplier);
    }

    default public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    default public <R> Streamable<R> map(Function<? super T, ? extends R> mapper) {
        Assert.notNull(mapper, (String)"Mapping function must not be null!");
        return Streamable.of(() -> this.stream().map(mapper));
    }

    default public <R> Streamable<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        Assert.notNull(mapper, (String)"Mapping function must not be null!");
        return Streamable.of(() -> this.stream().flatMap(mapper));
    }

    default public Streamable<T> filter(Predicate<? super T> predicate) {
        Assert.notNull(predicate, (String)"Filter predicate must not be null!");
        return Streamable.of(() -> this.stream().filter(predicate));
    }

    default public boolean isEmpty() {
        return !this.iterator().hasNext();
    }

    default public Streamable<T> and(Supplier<? extends Stream<? extends T>> stream) {
        Assert.notNull(stream, (String)"Stream must not be null!");
        return Streamable.of(() -> Stream.concat(this.stream(), (Stream)stream.get()));
    }

    default public Streamable<T> and(T ... others) {
        Assert.notNull(others, (String)"Other values must not be null!");
        return Streamable.of(() -> Stream.concat(this.stream(), Arrays.stream(others)));
    }

    default public Streamable<T> and(Iterable<? extends T> iterable) {
        Assert.notNull(iterable, (String)"Iterable must not be null!");
        return Streamable.of(() -> Stream.concat(this.stream(), StreamSupport.stream(iterable.spliterator(), false)));
    }

    default public Streamable<T> and(Streamable<? extends T> streamable) {
        return this.and((Supplier<? extends Stream<? extends T>>)streamable);
    }

    default public List<T> toList() {
        return this.stream().collect(StreamUtils.toUnmodifiableList());
    }

    default public Set<T> toSet() {
        return this.stream().collect(StreamUtils.toUnmodifiableSet());
    }

    @Override
    default public Stream<T> get() {
        return this.stream();
    }

    public static <S> Collector<S, ?, Streamable<S>> toStreamable() {
        return Streamable.toStreamable(Collectors.toList());
    }

    public static <S, T extends Iterable<S>> Collector<S, ?, Streamable<S>> toStreamable(Collector<S, ?, T> intermediate) {
        return Collector.of(intermediate.supplier(), intermediate.accumulator(), intermediate.combiner(), Streamable::of, new Collector.Characteristics[0]);
    }
}

