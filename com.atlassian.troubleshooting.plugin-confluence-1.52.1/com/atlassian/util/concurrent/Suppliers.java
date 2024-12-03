/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Function;
import com.atlassian.util.concurrent.Supplier;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Suppliers {
    public static <T> Supplier<T> memoize(final T source) {
        return new Supplier<T>(){

            @Override
            public T get() {
                return source;
            }
        };
    }

    public static <D, T> Supplier<T> fromFunction(final D input, final Function<D, T> function) {
        return new Supplier<T>(){

            @Override
            public T get() {
                return function.get(input);
            }
        };
    }

    public static <T> com.google.common.base.Supplier<T> toGoogleSupplier(Supplier<T> supplier) {
        return new ToGoogleAdapter<T>(supplier);
    }

    public static <T> Supplier<T> fromGoogleSupplier(com.google.common.base.Supplier<T> supplier) {
        return new FromGoogleAdapter<T>(supplier);
    }

    private Suppliers() {
        throw new AssertionError((Object)"cannot instantiate!");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class FromGoogleAdapter<T>
    implements Supplier<T> {
        private final com.google.common.base.Supplier<T> delegate;

        FromGoogleAdapter(com.google.common.base.Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            return (T)this.delegate.get();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ToGoogleAdapter<T>
    implements com.google.common.base.Supplier<T> {
        private final Supplier<T> delegate;

        ToGoogleAdapter(Supplier<T> delegate) {
            this.delegate = delegate;
        }

        public T get() {
            return this.delegate.get();
        }
    }
}

