/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.audit.coverage;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SingleValueCache<T>
implements Supplier<T> {
    private final long expirationNanos;
    private final AtomicReference<ValueHolder<T>> currentReference = new AtomicReference();
    private final Supplier<Long> nanoTimeProvider;
    private final Function<T, T> accumulator;

    public SingleValueCache(Supplier<T> delegate, long duration, TimeUnit unit) {
        this(oldValue -> delegate.get(), duration, unit, System::nanoTime);
    }

    public SingleValueCache(Function<T, T> accumulator, long duration, TimeUnit unit) {
        this(accumulator, duration, unit, System::nanoTime);
    }

    SingleValueCache(Function<T, T> accumulator, long duration, TimeUnit unit, Supplier<Long> nanoTimeProvider) {
        this.accumulator = accumulator;
        this.expirationNanos = unit.toNanos(duration);
        this.nanoTimeProvider = nanoTimeProvider;
    }

    @Override
    public T get() {
        long currentTimeNanos = this.nanoTimeProvider.get();
        ValueHolder<T> currentValue = this.currentReference.get();
        if (currentValue == null || currentTimeNanos >= ((ValueHolder)currentValue).creationTimeNanos + this.expirationNanos) {
            T newValue = Objects.requireNonNull(this.accumulator.apply(currentValue == null ? null : ((ValueHolder)currentValue).value));
            this.currentReference.compareAndSet(currentValue, new ValueHolder<T>(newValue, currentTimeNanos));
            return newValue;
        }
        return (T)((ValueHolder)currentValue).value;
    }

    public void invalidate() {
        this.currentReference.set(null);
    }

    private static class ValueHolder<T> {
        private final T value;
        private final long creationTimeNanos;

        public ValueHolder(T value, long creationTimeNanos) {
            this.value = value;
            this.creationTimeNanos = creationTimeNanos;
        }
    }
}

