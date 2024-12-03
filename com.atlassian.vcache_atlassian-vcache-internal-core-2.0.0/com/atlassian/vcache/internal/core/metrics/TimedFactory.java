/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

class TimedFactory<K, V>
implements Function<Set<K>, Map<K, V>>,
AutoCloseable {
    private final Function<Set<K>, Map<K, V>> delegate;
    private final BiConsumer<Optional<Long>, Long> handler;
    private Optional<Long> elapsedDuration = Optional.empty();
    private long numberOfKeys;

    TimedFactory(Function<Set<K>, Map<K, V>> delegate, BiConsumer<Optional<Long>, Long> handler) {
        this.delegate = Objects.requireNonNull(delegate);
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public Map<K, V> apply(Set<K> keys) {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> {
            this.elapsedDuration = Optional.of(t);
            this.numberOfKeys = keys.size();
        });){
            Map<K, V> map = this.delegate.apply(keys);
            return map;
        }
    }

    @Override
    public void close() {
        this.handler.accept(this.elapsedDuration, this.numberOfKeys);
    }

    long getNumberOfKeys() {
        return this.numberOfKeys;
    }
}

