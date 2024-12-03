/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.data.util.CloseableIterator;
import org.springframework.data.util.MultiValueMapCollector;
import org.springframework.data.util.Sink;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

public interface StreamUtils {
    public static <T> Stream<T> createStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 256);
        return StreamSupport.stream(spliterator, false);
    }

    public static <T> Stream<T> createStreamFromIterator(CloseableIterator<T> iterator) {
        Assert.notNull(iterator, (String)"Iterator must not be null!");
        return (Stream)StreamUtils.createStreamFromIterator(iterator).onClose(() -> iterator.close());
    }

    public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
        return Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList);
    }

    public static <T> Collector<T, ?, Set<T>> toUnmodifiableSet() {
        return Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet);
    }

    public static <T, K, V> Collector<T, MultiValueMap<K, V>, MultiValueMap<K, V>> toMultiMap(Function<T, K> keyFunction, Function<T, V> valueFunction) {
        return MultiValueMapCollector.of(keyFunction, valueFunction);
    }

    public static <T> Stream<T> fromNullable(@Nullable T source) {
        return source == null ? Stream.empty() : Stream.of(source);
    }

    public static <L, R, T> Stream<T> zip(Stream<L> left, Stream<R> right, final BiFunction<L, R, T> combiner) {
        Assert.notNull(left, (String)"Left stream must not be null!");
        Assert.notNull(right, (String)"Right must not be null!");
        Assert.notNull(combiner, (String)"Combiner must not be null!");
        final Spliterator lefts = left.spliterator();
        final Spliterator rights = right.spliterator();
        long size = Long.min(lefts.estimateSize(), rights.estimateSize());
        int characteristics = lefts.characteristics() & rights.characteristics();
        boolean parallel = left.isParallel() || right.isParallel();
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(size, characteristics){

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                Sink leftSink = new Sink();
                Sink rightSink = new Sink();
                boolean leftAdvance = lefts.tryAdvance(leftSink);
                if (!leftAdvance) {
                    return false;
                }
                boolean rightAdvance = rights.tryAdvance(rightSink);
                if (!rightAdvance) {
                    return false;
                }
                action.accept(combiner.apply(leftSink.getValue(), rightSink.getValue()));
                return true;
            }
        }, parallel);
    }
}

