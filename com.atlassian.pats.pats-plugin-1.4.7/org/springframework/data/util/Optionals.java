/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.data.util.Pair;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

public interface Optionals {
    public static boolean isAnyPresent(Optional<?> ... optionals) {
        Assert.notNull(optionals, (String)"Optionals must not be null!");
        return Arrays.stream(optionals).anyMatch(Optional::isPresent);
    }

    @SafeVarargs
    public static <T> Stream<T> toStream(Optional<? extends T> ... optionals) {
        Assert.notNull(optionals, (String)"Optional must not be null!");
        return Arrays.asList(optionals).stream().flatMap(it -> it.map(Stream::of).orElseGet(Stream::empty));
    }

    public static <S, T> Optional<T> firstNonEmpty(Iterable<S> source, Function<S, Optional<T>> function) {
        Assert.notNull(source, (String)"Source must not be null!");
        Assert.notNull(function, (String)"Function must not be null!");
        return Streamable.of(source).stream().map(function::apply).filter(Optional::isPresent).findFirst().orElseGet(Optional::empty);
    }

    public static <S, T> T firstNonEmpty(Iterable<S> source, Function<S, T> function, T defaultValue) {
        Assert.notNull(source, (String)"Source must not be null!");
        Assert.notNull(function, (String)"Function must not be null!");
        return (T)Streamable.of(source).stream().map(function::apply).filter(it -> !it.equals(defaultValue)).findFirst().orElse(defaultValue);
    }

    @SafeVarargs
    public static <T> Optional<T> firstNonEmpty(Supplier<Optional<T>> ... suppliers) {
        Assert.notNull(suppliers, (String)"Suppliers must not be null!");
        return Optionals.firstNonEmpty(Streamable.of(suppliers));
    }

    public static <T> Optional<T> firstNonEmpty(Iterable<Supplier<Optional<T>>> suppliers) {
        Assert.notNull(suppliers, (String)"Suppliers must not be null!");
        return Streamable.of(suppliers).stream().map(Supplier::get).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    public static <T> Optional<T> next(Iterator<T> iterator) {
        Assert.notNull(iterator, (String)"Iterator must not be null!");
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    public static <T, S> Optional<Pair<T, S>> withBoth(Optional<T> left, Optional<S> right) {
        return left.flatMap(l -> right.map(r -> Pair.of(l, r)));
    }

    public static <T, S> void ifAllPresent(Optional<T> left, Optional<S> right, BiConsumer<T, S> consumer) {
        Assert.notNull(left, (String)"Optional must not be null!");
        Assert.notNull(right, (String)"Optional must not be null!");
        Assert.notNull(consumer, (String)"Consumer must not be null!");
        Optionals.mapIfAllPresent(left, right, (l, r) -> {
            consumer.accept(l, r);
            return null;
        });
    }

    public static <T, S, R> Optional<R> mapIfAllPresent(Optional<T> left, Optional<S> right, BiFunction<T, S, R> function) {
        Assert.notNull(left, (String)"Optional must not be null!");
        Assert.notNull(right, (String)"Optional must not be null!");
        Assert.notNull(function, (String)"BiFunctionmust not be null!");
        return left.flatMap(l -> right.map(r -> function.apply(l, r)));
    }

    public static <T> void ifPresentOrElse(Optional<T> optional, Consumer<? super T> consumer, Runnable runnable) {
        Assert.notNull(optional, (String)"Optional must not be null!");
        Assert.notNull(consumer, (String)"Consumer must not be null!");
        Assert.notNull((Object)runnable, (String)"Runnable must not be null!");
        if (optional.isPresent()) {
            optional.ifPresent(consumer);
        } else {
            runnable.run();
        }
    }
}

