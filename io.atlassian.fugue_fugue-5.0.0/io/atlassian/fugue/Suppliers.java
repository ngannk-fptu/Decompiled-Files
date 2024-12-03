/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Option;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class Suppliers {
    public static <A> Supplier<A> ofInstance(A a) {
        return () -> a;
    }

    public static <A, B> Supplier<B> compose(Function<? super A, B> transform, Supplier<A> first) {
        return () -> transform.apply((Object)first.get());
    }

    public static <A, B> Supplier<B> ap(Supplier<A> sa, Supplier<Function<A, B>> sf) {
        return () -> ((Function)sf.get()).apply(sa.get());
    }

    public static Supplier<Boolean> alwaysTrue() {
        return () -> true;
    }

    public static Supplier<Boolean> alwaysFalse() {
        return () -> false;
    }

    public static <A> Supplier<A> alwaysNull() {
        return () -> null;
    }

    public static <A> Supplier<A> fromOption(Option<A> option) {
        return option::get;
    }

    public static <A, B> Supplier<B> fromFunction(Function<? super A, ? extends B> f, A a) {
        return () -> f.apply((Object)a);
    }

    public static <A> Supplier<A> memoize(Supplier<A> supplier) {
        return supplier instanceof MemoizingSupplier ? supplier : new MemoizingSupplier(Objects.requireNonNull(supplier));
    }

    public static <A> Supplier<A> weakMemoize(Supplier<A> supplier) {
        return supplier instanceof WeakMemoizingSupplier || supplier instanceof MemoizingSupplier ? supplier : new WeakMemoizingSupplier(Objects.requireNonNull(supplier));
    }

    private static final class WeakMemoizingSupplier<A>
    implements Supplier<A> {
        private final Supplier<A> delegate;
        private volatile WeakReference<A> value;

        WeakMemoizingSupplier(Supplier<A> delegate) {
            this.delegate = delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public A get() {
            Object a;
            Object t = a = this.value == null ? null : (Object)this.value.get();
            if (a == null) {
                WeakMemoizingSupplier weakMemoizingSupplier = this;
                synchronized (weakMemoizingSupplier) {
                    Object t2 = a = this.value == null ? null : (Object)this.value.get();
                    if (a == null) {
                        a = this.delegate.get();
                        this.value = new WeakReference<Object>(a);
                    }
                }
            }
            return (A)a;
        }
    }

    private static final class MemoizingSupplier<A>
    implements Supplier<A> {
        private volatile Supplier<A> delegate;
        private A a;

        MemoizingSupplier(Supplier<A> delegate) {
            this.delegate = delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public A get() {
            if (this.delegate != null) {
                MemoizingSupplier memoizingSupplier = this;
                synchronized (memoizingSupplier) {
                    if (this.delegate != null) {
                        A res = this.delegate.get();
                        this.a = res;
                        this.delegate = null;
                        return res;
                    }
                }
            }
            return this.a;
        }
    }
}

