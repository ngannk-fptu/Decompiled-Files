/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Suppliers {
    public static <T> Supplier<T> memoize(T source) {
        return () -> source;
    }

    public static <D, T> Supplier<T> fromFunction(D input, Function<D, T> function) {
        return () -> function.apply(input);
    }

    public static <T> Callable<T> toCallable(Supplier<T> supplier) {
        return new CallableAdapter<T>(supplier);
    }

    private Suppliers() {
        throw new AssertionError((Object)"cannot instantiate!");
    }

    static class CallableAdapter<T>
    implements Callable<T> {
        private final Supplier<T> supplier;

        CallableAdapter(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T call() {
            return this.supplier.get();
        }
    }
}

