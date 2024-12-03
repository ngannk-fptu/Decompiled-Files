/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.NotNull;
import io.atlassian.util.concurrent.WeakMemoizer;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Functions {
    public static <D, R> Function<D, R> fromSupplier(@NotNull Supplier<R> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return d -> supplier.get();
    }

    static <T> Function<Supplier<? extends T>, T> fromSupplier() {
        return Supplier::get;
    }

    public static <T, R> Function<T, R> weakMemoize(Function<T, R> f) {
        return WeakMemoizer.weakMemoizer(f);
    }

    static <T> Function<Supplier<T>, Supplier<T>> ignoreExceptions() {
        return new ExceptionIgnorer();
    }

    private Functions() {
    }

    static class IgnoreAndReturnNull<T>
    implements Supplier<T> {
        private final Supplier<T> delegate;

        IgnoreAndReturnNull(Supplier<T> delegate) {
            this.delegate = Objects.requireNonNull(delegate, "delegate");
        }

        @Override
        public T get() {
            try {
                return this.delegate.get();
            }
            catch (RuntimeException ignore) {
                return null;
            }
        }
    }

    static class ExceptionIgnorer<T>
    implements Function<Supplier<T>, Supplier<T>> {
        ExceptionIgnorer() {
        }

        @Override
        public Supplier<T> apply(Supplier<T> from) {
            return new IgnoreAndReturnNull<T>(from);
        }
    }
}

