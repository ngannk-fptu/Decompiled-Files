/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class TimedSupplier<V>
implements Supplier<V>,
AutoCloseable {
    private final Supplier<? extends V> delegate;
    private final Consumer<Optional<Long>> handler;
    private Optional<Long> elapsedDuration = Optional.empty();

    public TimedSupplier(Supplier<V> delegate, Consumer<Optional<Long>> handler) {
        this.delegate = Objects.requireNonNull(delegate);
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public V get() {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> {
            this.elapsedDuration = Optional.of(t);
        });){
            V v = this.delegate.get();
            return v;
        }
    }

    @Override
    public void close() {
        this.handler.accept(this.elapsedDuration);
    }

    public boolean wasInvoked() {
        return this.elapsedDuration.isPresent();
    }
}

