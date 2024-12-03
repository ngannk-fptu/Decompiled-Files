/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.confluence.impl.util;

import io.atlassian.fugue.Pair;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OptionalUtils {
    public static <A, B> Optional<Pair<A, B>> zip(Optional<A> o1, Optional<B> o2) {
        if (o1.isPresent() && o2.isPresent()) {
            return Optional.of(Pair.pair(o1.get(), o2.get()));
        }
        return Optional.empty();
    }

    @SafeVarargs
    public static <T, U> Optional<U> first(Optional<T> maybeT, Function<? super T, Optional<U>> ... mappers) {
        return Arrays.stream(mappers).map(maybeT::flatMap).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    @SafeVarargs
    public static <T> Optional<T> firstNonEmpty(Supplier<Optional<T>> ... lazyOptionals) {
        return Arrays.stream(lazyOptionals).map(Supplier::get).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    private OptionalUtils() {
    }
}

