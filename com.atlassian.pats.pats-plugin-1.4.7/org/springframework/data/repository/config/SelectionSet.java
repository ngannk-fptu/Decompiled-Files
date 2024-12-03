/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.config;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class SelectionSet<T> {
    private final Collection<T> collection;
    private final Function<Collection<T>, Optional<T>> fallback;

    private SelectionSet(Collection<T> collection, Function<Collection<T>, Optional<T>> fallback) {
        this.collection = collection;
        this.fallback = fallback;
    }

    static <T> SelectionSet<T> of(Collection<T> collection) {
        return new SelectionSet<T>(collection, SelectionSet.defaultFallback());
    }

    public static <T> SelectionSet<T> of(Collection<T> collection, Function<Collection<T>, Optional<T>> fallback) {
        return new SelectionSet<T>(collection, fallback);
    }

    Optional<T> uniqueResult() {
        Optional<T> uniqueResult = this.findUniqueResult();
        return uniqueResult.isPresent() ? uniqueResult : this.fallback.apply(this.collection);
    }

    SelectionSet<T> filterIfNecessary(Predicate<T> predicate) {
        return this.findUniqueResult().map(it -> this).orElseGet(() -> new SelectionSet<T>(this.collection.stream().filter(predicate).collect(Collectors.toList()), this.fallback));
    }

    private static <S> Function<Collection<S>, Optional<S>> defaultFallback() {
        return c -> {
            if (c.isEmpty()) {
                return Optional.empty();
            }
            throw new IllegalStateException("More then one element in collection.");
        };
    }

    private Optional<T> findUniqueResult() {
        return Optional.ofNullable(this.collection.size() == 1 ? (Object)this.collection.iterator().next() : null);
    }
}

