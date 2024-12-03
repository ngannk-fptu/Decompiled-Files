/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@SuppressFBWarnings(value={"NN_NAKED_NOTIFY"}, justification="State handled with CAS, naked notify correct")
public abstract class AbstractCompletableFuture<V>
implements ICompletableFuture<V> {
    private static final Object INITIAL_STATE = new ExecutionCallbackNode(null, null, null);
    private static final Object CANCELLED_STATE = new Object();
    private static final AtomicReferenceFieldUpdater<AbstractCompletableFuture, Object> STATE = AtomicReferenceFieldUpdater.newUpdater(AbstractCompletableFuture.class, Object.class, "state");
    private volatile Object state = INITIAL_STATE;
    private final ILogger logger;
    private final Executor defaultExecutor;

    protected AbstractCompletableFuture(NodeEngine nodeEngine, ILogger logger) {
        this(nodeEngine.getExecutionService().getExecutor("hz:async"), logger);
    }

    protected AbstractCompletableFuture(Executor defaultExecutor, ILogger logger) {
        this.defaultExecutor = defaultExecutor;
        this.logger = logger;
    }

    @Override
    public void andThen(ExecutionCallback<V> callback) {
        this.andThen(callback, this.defaultExecutor);
    }

    @Override
    public void andThen(ExecutionCallback<V> callback, Executor executor) {
        ExecutionCallbackNode newState;
        Object currentState;
        Preconditions.isNotNull(callback, "callback");
        Preconditions.isNotNull(executor, "executor");
        do {
            if (AbstractCompletableFuture.isCancelledState(currentState = this.state)) {
                return;
            }
            if (!AbstractCompletableFuture.isDoneState(currentState)) continue;
            this.runAsynchronous(callback, executor, currentState);
            return;
        } while (!STATE.compareAndSet(this, currentState, newState = new ExecutionCallbackNode(callback, executor, (ExecutionCallbackNode)currentState)));
    }

    @Override
    public boolean isDone() {
        return AbstractCompletableFuture.isDoneState(this.state);
    }

    private static boolean isDoneState(Object state) {
        return !(state instanceof ExecutionCallbackNode);
    }

    @Override
    public final boolean cancel(boolean mayInterruptIfRunning) {
        Object currentState;
        Boolean shouldCancel = null;
        do {
            if (AbstractCompletableFuture.isDoneState(currentState = this.state)) {
                return false;
            }
            if (shouldCancel == null) {
                shouldCancel = this.shouldCancel(mayInterruptIfRunning);
            }
            if (shouldCancel.booleanValue()) continue;
            return false;
        } while (!STATE.compareAndSet(this, currentState, CANCELLED_STATE));
        this.cancelled(mayInterruptIfRunning);
        this.notifyThreadsWaitingOnGet();
        return true;
    }

    protected boolean shouldCancel(boolean mayInterruptIfRunning) {
        return true;
    }

    protected void cancelled(boolean mayInterruptIfRunning) {
    }

    private static boolean isCancelledState(Object state) {
        return state == CANCELLED_STATE;
    }

    @Override
    public boolean isCancelled() {
        return AbstractCompletableFuture.isCancelledState(this.state);
    }

    @Override
    public final V get() throws InterruptedException, ExecutionException {
        while (true) {
            try {
                return this.get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException ignored) {
                EmptyStatement.ignore(ignored);
                continue;
            }
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        long deadlineTimeMillis = System.currentTimeMillis() + unit.toMillis(timeout);
        while (true) {
            Object currentState;
            if (AbstractCompletableFuture.isCancelledState(currentState = this.state)) {
                throw new CancellationException();
            }
            if (AbstractCompletableFuture.isDoneState(currentState)) {
                return AbstractCompletableFuture.getResult(currentState);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            long millisToWait = deadlineTimeMillis - System.currentTimeMillis();
            if (millisToWait <= 0L) {
                throw new TimeoutException();
            }
            AbstractCompletableFuture abstractCompletableFuture = this;
            synchronized (abstractCompletableFuture) {
                if (!AbstractCompletableFuture.isDoneState(this.state)) {
                    this.wait(millisToWait);
                }
            }
        }
    }

    protected boolean setResult(Object result) {
        Object currentState;
        do {
            if (!AbstractCompletableFuture.isDoneState(currentState = this.state)) continue;
            return false;
        } while (!STATE.compareAndSet(this, currentState, result));
        this.done();
        this.notifyThreadsWaitingOnGet();
        this.runAsynchronous((ExecutionCallbackNode)currentState, result);
        return true;
    }

    protected void done() {
    }

    protected V getResult() {
        return AbstractCompletableFuture.getResult(this.state);
    }

    private static <V> V getResult(Object state) {
        if (AbstractCompletableFuture.isCancelledState(state)) {
            return null;
        }
        if (!AbstractCompletableFuture.isDoneState(state)) {
            return null;
        }
        if (state instanceof Throwable) {
            ExceptionUtil.sneakyThrow((Throwable)state);
        }
        return (V)state;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyThreadsWaitingOnGet() {
        AbstractCompletableFuture abstractCompletableFuture = this;
        synchronized (abstractCompletableFuture) {
            this.notifyAll();
        }
    }

    private void runAsynchronous(ExecutionCallbackNode head, Object result) {
        while (head != INITIAL_STATE) {
            this.runAsynchronous(head.callback, head.executor, result);
            head = head.next;
        }
    }

    private void runAsynchronous(ExecutionCallback<V> callback, Executor executor, Object result) {
        executor.execute(new ExecutionCallbackRunnable<V>(this.getClass(), result, callback, this.logger));
    }

    private static final class ExecutionCallbackRunnable<V>
    implements Runnable {
        private final Class<?> caller;
        private final Object result;
        private final ExecutionCallback<V> callback;
        private final ILogger logger;

        public ExecutionCallbackRunnable(Class<?> caller, Object result, ExecutionCallback<V> callback, ILogger logger) {
            this.caller = caller;
            this.result = result;
            this.callback = callback;
            this.logger = logger;
        }

        @Override
        public void run() {
            try {
                if (this.result instanceof Throwable) {
                    this.callback.onFailure((Throwable)this.result);
                } else {
                    this.callback.onResponse(this.result);
                }
            }
            catch (Throwable cause) {
                this.logger.severe("Failed asynchronous execution of execution callback: " + this.callback + " for call " + this.caller, cause);
            }
        }
    }

    private static final class ExecutionCallbackNode<E> {
        final ExecutionCallback<E> callback;
        final Executor executor;
        final ExecutionCallbackNode<E> next;

        private ExecutionCallbackNode(ExecutionCallback<E> callback, Executor executor, ExecutionCallbackNode<E> next) {
            this.callback = callback;
            this.executor = executor;
            this.next = next;
        }
    }
}

