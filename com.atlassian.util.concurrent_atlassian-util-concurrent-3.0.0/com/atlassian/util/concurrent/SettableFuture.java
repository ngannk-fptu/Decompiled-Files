/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.TimedOutException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class SettableFuture<T>
implements Future<T> {
    private final AtomicReference<Value<T>> ref = new AtomicReference();
    private final CountDownLatch latch = new CountDownLatch(1);

    public SettableFuture<T> set(T value) {
        this.setAndCheckValue(new ReferenceValue<T>(value));
        return this;
    }

    public SettableFuture<T> setException(Throwable throwable) {
        this.setAndCheckValue(new ThrowableValue(throwable));
        return this;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        this.latch.await();
        return this.ref.get().get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!this.latch.await(timeout, unit)) {
            throw new TimedOutException(timeout, unit);
        }
        return this.ref.get().get();
    }

    @Override
    public boolean isDone() {
        return this.ref.get() != null;
    }

    @Override
    public boolean isCancelled() {
        return this.isDone() && this.ref.get() instanceof CancelledValue;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.setValue(new CancelledValue()) == null;
    }

    private void setAndCheckValue(Value<T> value) {
        Value<T> oldValue = this.setValue(value);
        if (oldValue != null && !value.equals(oldValue)) {
            throw new IllegalStateException("cannot change value after it has been set");
        }
    }

    private Value<T> setValue(Value<T> value) {
        do {
            Value<T> oldValue;
            if ((oldValue = this.ref.get()) == null) continue;
            return oldValue;
        } while (!this.ref.compareAndSet(null, value));
        this.latch.countDown();
        return null;
    }

    private static class CancelledValue<T>
    implements Value<T> {
        private CancelledValue() {
        }

        @Override
        public T get() throws ExecutionException {
            throw new CancellationException();
        }
    }

    private static class ThrowableValue<T>
    implements Value<T> {
        private final Throwable throwable;

        ThrowableValue(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public T get() throws ExecutionException {
            throw new ExecutionException(this.throwable);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ThrowableValue)) {
                return false;
            }
            return this.throwable.equals(((ThrowableValue)obj).throwable);
        }

        public int hashCode() {
            throw new UnsupportedOperationException();
        }
    }

    private static class ReferenceValue<T>
    implements Value<T> {
        private final T value;

        ReferenceValue(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return this.value;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ReferenceValue)) {
                return false;
            }
            ReferenceValue other = (ReferenceValue)obj;
            return this.value == null ? other.value == null : this.value.equals(other.value);
        }

        public int hashCode() {
            throw new UnsupportedOperationException();
        }
    }

    private static interface Value<T> {
        public T get() throws ExecutionException;
    }
}

