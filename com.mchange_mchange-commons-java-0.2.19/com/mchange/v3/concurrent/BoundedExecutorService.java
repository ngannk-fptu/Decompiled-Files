/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.concurrent;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public final class BoundedExecutorService
extends AbstractExecutorService {
    static final MLogger logger = MLog.getLogger(BoundedExecutorService.class);
    final ExecutorService inner;
    final int blockBound;
    final int restartBeneath;
    State state;
    int permits;
    Map<Thread, Runnable> waiters = new HashMap<Thread, Runnable>();

    public BoundedExecutorService(ExecutorService executorService, int n, int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new IllegalArgumentException("blockBound and restartBeneath must both be greater than zero!");
        }
        if (n2 > n) {
            throw new IllegalArgumentException("restartBeneath must be less than or equal to blockBound!");
        }
        this.inner = executorService;
        this.blockBound = n;
        this.restartBeneath = n2;
        this.state = State.ACCEPTING;
        this.permits = 0;
    }

    public BoundedExecutorService(ExecutorService executorService, int n) {
        this(executorService, n, n);
    }

    public synchronized State getState() {
        return this.state;
    }

    @Override
    public synchronized boolean isShutdown() {
        return this.state == State.SHUTDOWN || this.state == State.SHUTDOWN_NOW;
    }

    @Override
    public synchronized boolean isTerminated() {
        return this.isShutdown() && this.permits == 0;
    }

    @Override
    public synchronized void shutdown() {
        this.inner.shutdown();
        this.updateState(State.SHUTDOWN);
        this.notifyAll();
    }

    @Override
    public synchronized List<Runnable> shutdownNow() {
        this.updateState(State.SHUTDOWN_NOW);
        List<Runnable> list = this.inner.shutdownNow();
        Collection<Runnable> collection = this.waiters.values();
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>(list.size() + collection.size());
        arrayList.addAll(list);
        arrayList.addAll(collection);
        Iterator<Thread> iterator = this.waiters.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().interrupt();
        }
        this.waiters.clear();
        return Collections.unmodifiableList(arrayList);
    }

    @Override
    public synchronized boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        long l2 = System.currentTimeMillis();
        long l3 = l2 + TimeUnit.MILLISECONDS.convert(l, timeUnit);
        boolean bl = this.inner.awaitTermination(l, timeUnit);
        if (bl) {
            long l4 = System.currentTimeMillis();
            while (!this.isTerminated()) {
                if (l4 > l3) {
                    return false;
                }
                this.wait(l3 - l4);
            }
            return true;
        }
        return false;
    }

    @Override
    public void execute(Runnable runnable) {
        this.inner.execute(this.newTaskFor(runnable, (V)null));
    }

    protected <V> RunnableFuture<V> newTaskFor(Callable<V> callable) {
        ReleasingFutureTask<V> releasingFutureTask = new ReleasingFutureTask<V>(callable);
        this.acquirePermit(releasingFutureTask);
        return releasingFutureTask;
    }

    protected <V> RunnableFuture<V> newTaskFor(Runnable runnable, V v) {
        ReleasingFutureTask<V> releasingFutureTask = new ReleasingFutureTask<V>(runnable, v);
        this.acquirePermit(releasingFutureTask);
        return releasingFutureTask;
    }

    private boolean shouldWait() {
        switch (this.state) {
            case SHUTDOWN: 
            case SHUTDOWN_NOW: {
                return this.permits == this.blockBound;
            }
            case ACCEPTING: {
                return false;
            }
            case SATURATED: 
            case UNWINDING: {
                return true;
            }
        }
        throw new AssertionError((Object)"This should be dead code.");
    }

    private synchronized void acquirePermit(Runnable runnable) {
        try {
            switch (this.state) {
                case SHUTDOWN: 
                case SHUTDOWN_NOW: {
                    throw new RejectedExecutionException(this + " has been shut down. [state=" + (Object)((Object)this.state) + "]");
                }
                case ACCEPTING: 
                case SATURATED: 
                case UNWINDING: {
                    while (this.shouldWait()) {
                        try {
                            this.waiters.put(Thread.currentThread(), runnable);
                            this.wait();
                        }
                        finally {
                            this.waiters.remove(Thread.currentThread());
                        }
                    }
                    if (this.state == State.SHUTDOWN_NOW) break;
                    ++this.permits;
                    if (this.permits != this.blockBound) break;
                    this.updateState(State.SATURATED);
                }
            }
        }
        catch (InterruptedException interruptedException) {
            throw new RejectedExecutionException(this + " has been forcibly shut down. [state=" + (Object)((Object)this.state) + "]", interruptedException);
        }
    }

    private synchronized void releasePermit() {
        --this.permits;
        if (this.permits < this.restartBeneath) {
            this.updateState(State.ACCEPTING);
        } else if (this.state == State.SATURATED && this.permits < this.blockBound) {
            this.updateState(State.UNWINDING);
        }
    }

    private void updateState(State state) {
        switch (this.state) {
            case ACCEPTING: 
            case SATURATED: 
            case UNWINDING: {
                if (this.state == state) break;
                this.doUpdateState(state);
                break;
            }
            case SHUTDOWN: {
                if (state != State.SHUTDOWN_NOW) break;
                this.doUpdateState(state);
                break;
            }
        }
    }

    private void doUpdateState(State state) {
        if (logger.isLoggable(MLevel.FINE)) {
            logger.log(MLevel.FINE, "State transition " + (Object)((Object)this.state) + " => " + (Object)((Object)state) + "; blockBound=" + this.blockBound + "; restartBeneath=" + this.restartBeneath + "; permits=" + this.permits);
        }
        this.state = state;
        if (this.state == State.SHUTDOWN_NOW) {
            this.permits = 0;
        }
        this.notifyAll();
    }

    class ReleasingFutureTask<V>
    extends FutureTask<V> {
        ReleasingFutureTask(Callable<V> callable) {
            super(callable);
        }

        ReleasingFutureTask(Runnable runnable, V v) {
            super(runnable, v);
        }

        @Override
        protected void done() {
            BoundedExecutorService.this.releasePermit();
        }
    }

    static enum State {
        ACCEPTING,
        SATURATED,
        UNWINDING,
        SHUTDOWN,
        SHUTDOWN_NOW;

    }
}

