/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Function;
import com.google.common.base.NullnessCasts;
import com.google.common.base.Objects;
import com.google.common.base.ParametricNullness;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Suppliers {
    private Suppliers() {
    }

    public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
        return new SupplierComposition<F, T>(function, supplier);
    }

    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        if (delegate instanceof NonSerializableMemoizingSupplier || delegate instanceof MemoizingSupplier) {
            return delegate;
        }
        return delegate instanceof Serializable ? new MemoizingSupplier<T>(delegate) : new NonSerializableMemoizingSupplier<T>(delegate);
    }

    public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, long duration, TimeUnit unit) {
        return new ExpiringMemoizingSupplier<T>(delegate, duration, unit);
    }

    public static <T> Supplier<T> ofInstance(@ParametricNullness T instance) {
        return new SupplierOfInstance<T>(instance);
    }

    public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate) {
        return new ThreadSafeSupplier<T>(delegate);
    }

    public static <T> Function<Supplier<T>, T> supplierFunction() {
        SupplierFunctionImpl sf = SupplierFunctionImpl.INSTANCE;
        return sf;
    }

    private static enum SupplierFunctionImpl implements SupplierFunction<Object>
    {
        INSTANCE;


        @Override
        @CheckForNull
        public Object apply(Supplier<@Nullable Object> input) {
            return input.get();
        }

        public String toString() {
            return "Suppliers.supplierFunction()";
        }
    }

    private static interface SupplierFunction<T>
    extends Function<Supplier<T>, T> {
    }

    private static class ThreadSafeSupplier<T>
    implements Supplier<T>,
    Serializable {
        final Supplier<T> delegate;
        private static final long serialVersionUID = 0L;

        ThreadSafeSupplier(Supplier<T> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @ParametricNullness
        public T get() {
            Supplier<T> supplier = this.delegate;
            synchronized (supplier) {
                return this.delegate.get();
            }
        }

        public String toString() {
            return "Suppliers.synchronizedSupplier(" + this.delegate + ")";
        }
    }

    private static class SupplierOfInstance<T>
    implements Supplier<T>,
    Serializable {
        @ParametricNullness
        final T instance;
        private static final long serialVersionUID = 0L;

        SupplierOfInstance(@ParametricNullness T instance) {
            this.instance = instance;
        }

        @Override
        @ParametricNullness
        public T get() {
            return this.instance;
        }

        public boolean equals(@CheckForNull Object obj) {
            if (obj instanceof SupplierOfInstance) {
                SupplierOfInstance that = (SupplierOfInstance)obj;
                return Objects.equal(this.instance, that.instance);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hashCode(this.instance);
        }

        public String toString() {
            return "Suppliers.ofInstance(" + this.instance + ")";
        }
    }

    @VisibleForTesting
    static class ExpiringMemoizingSupplier<T>
    implements Supplier<T>,
    Serializable {
        final Supplier<T> delegate;
        final long durationNanos;
        @CheckForNull
        volatile transient T value;
        volatile transient long expirationNanos;
        private static final long serialVersionUID = 0L;

        ExpiringMemoizingSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
            this.delegate = Preconditions.checkNotNull(delegate);
            this.durationNanos = unit.toNanos(duration);
            Preconditions.checkArgument(duration > 0L, "duration (%s %s) must be > 0", duration, (Object)unit);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @ParametricNullness
        public T get() {
            long nanos = this.expirationNanos;
            long now = System.nanoTime();
            if (nanos == 0L || now - nanos >= 0L) {
                ExpiringMemoizingSupplier expiringMemoizingSupplier = this;
                synchronized (expiringMemoizingSupplier) {
                    if (nanos == this.expirationNanos) {
                        T t = this.delegate.get();
                        this.value = t;
                        nanos = now + this.durationNanos;
                        this.expirationNanos = nanos == 0L ? 1L : nanos;
                        return t;
                    }
                }
            }
            return NullnessCasts.uncheckedCastNullableTToT(this.value);
        }

        public String toString() {
            return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
        }
    }

    @VisibleForTesting
    static class NonSerializableMemoizingSupplier<T>
    implements Supplier<T> {
        private static final Supplier<Void> SUCCESSFULLY_COMPUTED = () -> {
            throw new IllegalStateException();
        };
        private volatile Supplier<T> delegate;
        @CheckForNull
        private T value;

        NonSerializableMemoizingSupplier(Supplier<T> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @ParametricNullness
        public T get() {
            if (this.delegate != SUCCESSFULLY_COMPUTED) {
                NonSerializableMemoizingSupplier nonSerializableMemoizingSupplier = this;
                synchronized (nonSerializableMemoizingSupplier) {
                    if (this.delegate != SUCCESSFULLY_COMPUTED) {
                        T t = this.delegate.get();
                        this.value = t;
                        this.delegate = SUCCESSFULLY_COMPUTED;
                        return t;
                    }
                }
            }
            return NullnessCasts.uncheckedCastNullableTToT(this.value);
        }

        public String toString() {
            Supplier<T> delegate = this.delegate;
            return "Suppliers.memoize(" + (delegate == SUCCESSFULLY_COMPUTED ? "<supplier that returned " + this.value + ">" : delegate) + ")";
        }
    }

    @VisibleForTesting
    static class MemoizingSupplier<T>
    implements Supplier<T>,
    Serializable {
        final Supplier<T> delegate;
        volatile transient boolean initialized;
        @CheckForNull
        transient T value;
        private static final long serialVersionUID = 0L;

        MemoizingSupplier(Supplier<T> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @ParametricNullness
        public T get() {
            if (!this.initialized) {
                MemoizingSupplier memoizingSupplier = this;
                synchronized (memoizingSupplier) {
                    if (!this.initialized) {
                        T t = this.delegate.get();
                        this.value = t;
                        this.initialized = true;
                        return t;
                    }
                }
            }
            return NullnessCasts.uncheckedCastNullableTToT(this.value);
        }

        public String toString() {
            return "Suppliers.memoize(" + (this.initialized ? "<supplier that returned " + this.value + ">" : this.delegate) + ")";
        }
    }

    private static class SupplierComposition<F, T>
    implements Supplier<T>,
    Serializable {
        final Function<? super F, T> function;
        final Supplier<F> supplier;
        private static final long serialVersionUID = 0L;

        SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
            this.function = Preconditions.checkNotNull(function);
            this.supplier = Preconditions.checkNotNull(supplier);
        }

        @Override
        @ParametricNullness
        public T get() {
            return this.function.apply(this.supplier.get());
        }

        public boolean equals(@CheckForNull Object obj) {
            if (obj instanceof SupplierComposition) {
                SupplierComposition that = (SupplierComposition)obj;
                return this.function.equals(that.function) && this.supplier.equals(that.supplier);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hashCode(this.function, this.supplier);
        }

        public String toString() {
            return "Suppliers.compose(" + this.function + ", " + this.supplier + ")";
        }
    }
}

