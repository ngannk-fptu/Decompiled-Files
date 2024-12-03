/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.internal.util.executor.UnblockableThread;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

@SuppressFBWarnings(value={"DLS_DEAD_STORE_OF_CLASS_LITERAL"}, justification="Recommended way to prevent classloading bug")
public abstract class AbstractInvocationFuture<V>
implements InternalCompletableFuture<V> {
    static final Object VOID = "VOID";
    private static final AtomicReferenceFieldUpdater<AbstractInvocationFuture, Object> STATE;
    protected final Executor defaultExecutor;
    protected final ILogger logger;
    private volatile Object state = VOID;

    protected AbstractInvocationFuture(Executor defaultExecutor, ILogger logger) {
        this.defaultExecutor = defaultExecutor;
        this.logger = logger;
    }

    boolean compareAndSetState(Object oldState, Object newState) {
        return STATE.compareAndSet(this, oldState, newState);
    }

    protected final Object getState() {
        return this.state;
    }

    @Override
    public final boolean isDone() {
        return AbstractInvocationFuture.isDone(this.state);
    }

    private static boolean isDone(Object state) {
        if (state == null) {
            return true;
        }
        return state != VOID && !(state instanceof WaitNode) && !(state instanceof Thread) && !(state instanceof ExecutionCallback);
    }

    protected void onInterruptDetected() {
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.complete(new CancellationException());
    }

    @Override
    public boolean isCancelled() {
        return this.state instanceof CancellationException;
    }

    @Override
    public final V join() {
        try {
            return this.get();
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    @Override
    public final V get() throws InterruptedException, ExecutionException {
        Object response = this.registerWaiter(Thread.currentThread(), null);
        if (response != VOID) {
            return this.resolveAndThrowIfException(response);
        }
        boolean interrupted = false;
        try {
            while (true) {
                LockSupport.park();
                if (this.isDone()) {
                    V v = this.resolveAndThrowIfException(this.state);
                    return v;
                }
                if (!Thread.interrupted()) continue;
                interrupted = true;
                this.onInterruptDetected();
            }
        }
        finally {
            AbstractInvocationFuture.restoreInterrupt(interrupted);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Object response = this.registerWaiter(Thread.currentThread(), null);
        if (response != VOID) {
            return this.resolveAndThrowIfException(response);
        }
        long deadlineNanos = System.nanoTime() + unit.toNanos(timeout);
        boolean interrupted = false;
        try {
            long timeoutNanos = unit.toNanos(timeout);
            while (timeoutNanos > 0L) {
                LockSupport.parkNanos(timeoutNanos);
                timeoutNanos = deadlineNanos - System.nanoTime();
                if (this.isDone()) {
                    V v = this.resolveAndThrowIfException(this.state);
                    return v;
                }
                if (!Thread.interrupted()) continue;
                interrupted = true;
                this.onInterruptDetected();
            }
        }
        finally {
            AbstractInvocationFuture.restoreInterrupt(interrupted);
        }
        this.unregisterWaiter(Thread.currentThread());
        throw this.newTimeoutException(timeout, unit);
    }

    private static void restoreInterrupt(boolean interrupted) {
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void andThen(ExecutionCallback<V> callback) {
        this.andThen(callback, this.defaultExecutor);
    }

    @Override
    public void andThen(ExecutionCallback<V> callback, Executor executor) {
        Preconditions.isNotNull(callback, "callback");
        Preconditions.isNotNull(executor, "executor");
        Object response = this.registerWaiter(callback, executor);
        if (response != VOID) {
            this.unblock(callback, executor);
        }
    }

    private void unblockAll(Object waiter, Executor executor) {
        while (waiter != null) {
            if (waiter instanceof Thread) {
                LockSupport.unpark((Thread)waiter);
                return;
            }
            if (waiter instanceof ExecutionCallback) {
                this.unblock((ExecutionCallback)waiter, executor);
                return;
            }
            if (waiter.getClass() == WaitNode.class) {
                WaitNode waitNode = (WaitNode)waiter;
                this.unblockAll(waitNode.waiter, waitNode.executor);
                waiter = waitNode.next;
                continue;
            }
            return;
        }
    }

    private void unblock(final ExecutionCallback<V> callback, Executor executor) {
        try {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    try {
                        Object value = AbstractInvocationFuture.this.resolve(AbstractInvocationFuture.this.state);
                        if (value instanceof Throwable) {
                            Throwable error = AbstractInvocationFuture.this.unwrap((Throwable)value);
                            callback.onFailure(error);
                        } else {
                            callback.onResponse(value);
                        }
                    }
                    catch (Throwable cause) {
                        AbstractInvocationFuture.this.logger.severe("Failed asynchronous execution of execution callback: " + callback + "for call " + AbstractInvocationFuture.this.invocationToString(), cause);
                    }
                }
            });
        }
        catch (RejectedExecutionException e) {
            callback.onFailure(e);
        }
    }

    protected Throwable unwrap(Throwable throwable) {
        if (throwable instanceof ExecutionException && throwable.getCause() != null) {
            return throwable.getCause();
        }
        return throwable;
    }

    protected abstract String invocationToString();

    protected Object resolve(Object value) {
        return value;
    }

    protected abstract V resolveAndThrowIfException(Object var1) throws ExecutionException, InterruptedException;

    protected abstract TimeoutException newTimeoutException(long var1, TimeUnit var3);

    private Object registerWaiter(Object waiter, Executor executor) {
        Object newState;
        Object oldState;
        assert (!(waiter instanceof UnblockableThread)) : "Waiting for response on this thread is illegal";
        WaitNode waitNode = null;
        do {
            if (AbstractInvocationFuture.isDone(oldState = this.state)) {
                return oldState;
            }
            if (oldState == VOID && (executor == null || executor == this.defaultExecutor)) {
                newState = waiter;
                continue;
            }
            if (waitNode == null) {
                waitNode = new WaitNode(waiter, executor);
            }
            waitNode.next = oldState;
            newState = waitNode;
        } while (!this.compareAndSetState(oldState, newState));
        return VOID;
    }

    void unregisterWaiter(Thread waiter) {
        WaitNode prev = null;
        Object current = this.state;
        while (current != null) {
            Object next;
            Object currentWaiter = current.getClass() == WaitNode.class ? ((WaitNode)current).waiter : current;
            Object object = next = current.getClass() == WaitNode.class ? ((WaitNode)current).next : null;
            if (currentWaiter == waiter) {
                if (prev == null) {
                    Object n = next == null ? VOID : next;
                    current = this.compareAndSetState(current, n) ? null : this.state;
                    continue;
                }
                prev.next = next;
                current = null;
                continue;
            }
            prev = current.getClass() == WaitNode.class ? (WaitNode)current : null;
            current = next;
        }
    }

    @Override
    public final boolean complete(Object value) {
        Object oldState;
        do {
            if (!AbstractInvocationFuture.isDone(oldState = this.state)) continue;
            this.warnIfSuspiciousDoubleCompletion(oldState, value);
            return false;
        } while (!this.compareAndSetState(oldState, value));
        this.onComplete();
        this.unblockAll(oldState, this.defaultExecutor);
        return true;
    }

    protected void onComplete() {
    }

    private void warnIfSuspiciousDoubleCompletion(Object s0, Object s1) {
        if (s0 != s1 && !(s0 instanceof CancellationException) && !(s1 instanceof CancellationException)) {
            this.logger.warning(String.format("Future.complete(Object) on completed future. Request: %s, current value: %s, offered value: %s", this.invocationToString(), s0, s1));
        }
    }

    public String toString() {
        Object state = this.getState();
        if (AbstractInvocationFuture.isDone(state)) {
            return "InvocationFuture{invocation=" + this.invocationToString() + ", value=" + state + '}';
        }
        return "InvocationFuture{invocation=" + this.invocationToString() + ", done=false}";
    }

    static {
        Class<LockSupport> clazz = LockSupport.class;
        STATE = AtomicReferenceFieldUpdater.newUpdater(AbstractInvocationFuture.class, Object.class, "state");
    }

    static final class WaitNode {
        final Object waiter;
        volatile Object next;
        private final Executor executor;

        WaitNode(Object waiter, Executor executor) {
            this.waiter = waiter;
            this.executor = executor;
        }
    }
}

