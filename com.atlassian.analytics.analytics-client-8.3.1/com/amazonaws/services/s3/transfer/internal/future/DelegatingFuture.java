/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal.future;

import com.amazonaws.util.ValidationUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DelegatingFuture<T>
implements Future<T> {
    private final Object mutationLock = new Object();
    private volatile Future<T> delegate;
    private volatile CancelState cancelState = CancelState.NOT_CANCELLED;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDelegateIfUnset(Future<T> delegate) {
        if (this.hasDelegate()) {
            return;
        }
        Object object = this.mutationLock;
        synchronized (object) {
            if (this.hasDelegate()) {
                return;
            }
            this.setDelegate(delegate);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDelegate(Future<T> delegate) {
        Object object = this.mutationLock;
        synchronized (object) {
            ValidationUtils.assertAllAreNull("Delegate may only be set once.", this.delegate);
            switch (this.cancelState) {
                case NOT_CANCELLED: {
                    break;
                }
                case CANCELLED: {
                    delegate.cancel(false);
                    break;
                }
                case CANCELLED_MAY_INTERRUPT: {
                    delegate.cancel(true);
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
            this.delegate = delegate;
            this.mutationLock.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Future<T> delegate = this.delegate;
        if (delegate != null) {
            return delegate.cancel(mayInterruptIfRunning);
        }
        Object object = this.mutationLock;
        synchronized (object) {
            delegate = this.delegate;
            if (delegate != null) {
                return delegate.cancel(mayInterruptIfRunning);
            }
            if (this.cancelState != CancelState.NOT_CANCELLED) {
                return false;
            }
            this.cancelState = mayInterruptIfRunning ? CancelState.CANCELLED_MAY_INTERRUPT : CancelState.CANCELLED;
            this.mutationLock.notifyAll();
            return true;
        }
    }

    @Override
    public boolean isCancelled() {
        Future<T> delegate = this.delegate;
        if (delegate != null) {
            return delegate.isCancelled();
        }
        return this.cancelState != CancelState.NOT_CANCELLED;
    }

    @Override
    public boolean isDone() {
        Future<T> delegate = this.delegate;
        if (delegate != null) {
            return delegate.isDone();
        }
        return this.cancelState != CancelState.NOT_CANCELLED;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        Future<T> delegate = this.delegate;
        if (delegate != null) {
            return delegate.get();
        }
        Object object = this.mutationLock;
        synchronized (object) {
            delegate = this.delegate;
            while (delegate == null) {
                if (this.cancelState != CancelState.NOT_CANCELLED) {
                    throw new CancellationException("Future being waited on has been cancelled.");
                }
                this.mutationLock.wait();
                delegate = this.delegate;
            }
        }
        return delegate.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Future<T> delegate = this.delegate;
        if (delegate != null) {
            return delegate.get(timeout, unit);
        }
        long nanosToWait = unit.toNanos(timeout);
        long waitEndTime = System.nanoTime() + nanosToWait;
        Object object = this.mutationLock;
        synchronized (object) {
            delegate = this.delegate;
            while (delegate == null) {
                if (this.cancelState != CancelState.NOT_CANCELLED) {
                    throw new CancellationException("Future being waited on has been cancelled.");
                }
                long totalNanosRemainingOnWait = this.nanosUntil(waitEndTime);
                long millisRemainingPart = TimeUnit.NANOSECONDS.toMillis(totalNanosRemainingOnWait);
                int nanosRemainingPart = DelegatingFuture.toIntExact(totalNanosRemainingOnWait % 1000000L);
                this.mutationLock.wait(millisRemainingPart, nanosRemainingPart);
                delegate = this.delegate;
            }
        }
        return delegate.get(this.nanosUntil(waitEndTime), TimeUnit.NANOSECONDS);
    }

    private boolean hasDelegate() {
        return this.delegate != null;
    }

    private long nanosUntil(long time) throws TimeoutException {
        long nanosRemainingOnWait = time - System.nanoTime();
        if (nanosRemainingOnWait <= 0L) {
            throw new TimeoutException("Timed out waiting for future.");
        }
        return nanosRemainingOnWait;
    }

    private static int toIntExact(long value) {
        if ((long)((int)value) != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)value;
    }

    private static enum CancelState {
        NOT_CANCELLED,
        CANCELLED_MAY_INTERRUPT,
        CANCELLED;

    }
}

