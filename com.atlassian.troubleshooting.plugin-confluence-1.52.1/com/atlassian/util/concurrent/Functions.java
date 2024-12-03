/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import com.atlassian.util.concurrent.Function;
import com.atlassian.util.concurrent.NotNull;
import com.atlassian.util.concurrent.Supplier;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Functions {
    public static <D, R> Function<D, R> fromSupplier(@NotNull Supplier<R> supplier) {
        return new FromSupplier(supplier);
    }

    static <T> com.google.common.base.Function<com.google.common.base.Supplier<? extends T>, T> fromSupplier() {
        return new ValueExtractor();
    }

    public static <T> Function<T, T> identity() {
        return new Identity();
    }

    static <T> com.google.common.base.Function<com.google.common.base.Supplier<T>, com.google.common.base.Supplier<T>> ignoreExceptions() {
        return new ExceptionIgnorer();
    }

    public static <T, R> com.google.common.base.Function<T, R> toGoogleFunction(Function<T, R> function) {
        return new ToGoogleAdapter<T, R>(function);
    }

    private Functions() {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ToGoogleAdapter<T, R>
    implements com.google.common.base.Function<T, R> {
        private final Function<T, R> delegate;

        ToGoogleAdapter(Function<T, R> delegate) {
            this.delegate = delegate;
        }

        public R apply(T from) {
            return this.delegate.get(from);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class IgnoreAndReturnNull<T>
    implements com.google.common.base.Supplier<T> {
        private final com.google.common.base.Supplier<T> delegate;

        IgnoreAndReturnNull(com.google.common.base.Supplier<T> delegate) {
            this.delegate = Assertions.notNull("delegate", delegate);
        }

        public T get() {
            try {
                return (T)this.delegate.get();
            }
            catch (RuntimeException ignore) {
                return null;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ExceptionIgnorer<T>
    implements com.google.common.base.Function<com.google.common.base.Supplier<T>, com.google.common.base.Supplier<T>> {
        ExceptionIgnorer() {
        }

        public com.google.common.base.Supplier<T> apply(com.google.common.base.Supplier<T> from) {
            return new IgnoreAndReturnNull<T>(from);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Identity<T>
    implements Function<T, T> {
        private Identity() {
        }

        @Override
        public T get(T input) {
            return input;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ValueExtractor<T>
    implements com.google.common.base.Function<com.google.common.base.Supplier<? extends T>, T> {
        private ValueExtractor() {
        }

        public T apply(com.google.common.base.Supplier<? extends T> supplier) {
            return (T)supplier.get();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class FromSupplier<D, R>
    implements Function<D, R> {
        private final Supplier<R> supplier;

        FromSupplier(Supplier<R> supplier) {
            this.supplier = Assertions.notNull("supplier", supplier);
        }

        @Override
        public R get(D input) {
            return this.supplier.get();
        }
    }
}

