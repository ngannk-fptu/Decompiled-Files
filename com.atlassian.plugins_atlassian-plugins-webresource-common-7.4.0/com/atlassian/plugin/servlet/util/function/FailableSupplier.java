/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.servlet.util.function;

import java.util.function.Supplier;

@FunctionalInterface
public interface FailableSupplier<T, E extends Exception> {
    public T get() throws E;

    public static <T, E extends Exception> Supplier<T> wrapper(FailableSupplier<T, E> supplier) {
        return () -> {
            try {
                return supplier.get();
            }
            catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        };
    }
}

