/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.pocketknife.internal.querydsl.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class MemoizingResettingReference<P, T> {
    private final AtomicReference<Supplier<T>> supplierRef = new AtomicReference();
    private final Function<P, T> valueCreator;

    public MemoizingResettingReference(Function<P, T> valueCreator) {
        this.valueCreator = (Function)Preconditions.checkNotNull(valueCreator);
    }

    public T get(P parameter) {
        Supplier<T> supplier = this.supplierRef.get();
        if (supplier == null) {
            Supplier delegate = () -> this.valueCreator.apply(parameter);
            this.supplierRef.compareAndSet(null, new SmarterMemoizingSupplier(delegate));
        }
        supplier = this.supplierRef.get();
        return this.safelyGetT(supplier);
    }

    private T safelyGetT(Supplier<T> supplier) {
        try {
            Object t = supplier.get();
            Preconditions.checkNotNull((Object)t, (Object)"You MUST not provide null values to MemoizingResettingReference");
            return (T)t;
        }
        catch (RuntimeException rte) {
            this.reset();
            throw rte;
        }
    }

    public T getMemoizedValue() throws MemoizedValueNotPresentException {
        Supplier<T> supplier = this.supplierRef.get();
        if (supplier == null) {
            throw new MemoizedValueNotPresentException();
        }
        return this.safelyGetT(supplier);
    }

    public void reset() {
        this.supplierRef.set(null);
    }

    private static class SmarterMemoizingSupplier<T>
    implements Supplier<T> {
        private AtomicReference<Supplier<T>> delegate;
        private AtomicReference<T> value;
        private AtomicBoolean initialized = new AtomicBoolean(false);

        private SmarterMemoizingSupplier(Supplier<T> delegate) {
            this.delegate = new AtomicReference<Supplier<T>>(delegate);
            this.value = new AtomicReference();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public T get() {
            if (!this.initialized.get()) {
                SmarterMemoizingSupplier smarterMemoizingSupplier = this;
                synchronized (smarterMemoizingSupplier) {
                    if (!this.initialized.get()) {
                        Object t = this.delegate.get().get();
                        this.value.set(t);
                        this.delegate.set(null);
                        this.initialized.set(true);
                    }
                }
            }
            return this.value.get();
        }
    }

    public static class MemoizedValueNotPresentException
    extends RuntimeException {
        public MemoizedValueNotPresentException() {
            super("MemoizingResettingReference.getMemoizedValue called and the value is not previously been initialised via MemoizingResettingReference.get(<P>)");
        }
    }
}

