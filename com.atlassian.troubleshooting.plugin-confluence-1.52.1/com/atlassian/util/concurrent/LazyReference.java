/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Supplier;
import java.lang.ref.WeakReference;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import net.jcip.annotations.ThreadSafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ThreadSafe
public abstract class LazyReference<T>
extends WeakReference<T>
implements Supplier<T>,
com.google.common.base.Supplier<T> {
    private final Sync sync = new Sync();

    public LazyReference() {
        super(null);
    }

    protected abstract T create() throws Exception;

    @Override
    public final T get() {
        boolean interrupted = false;
        while (true) {
            try {
                T t = this.getInterruptibly();
                return t;
            }
            catch (InterruptedException ignore) {
                interrupted = true;
                continue;
            }
            break;
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public final T getInterruptibly() throws InterruptedException {
        if (!this.sync.isDone()) {
            this.sync.run();
        }
        try {
            return this.sync.get();
        }
        catch (ExecutionException e) {
            throw new InitializationException(e);
        }
    }

    public boolean isInitialized() {
        return this.sync.isDone();
    }

    public void cancel() {
        this.sync.cancel(true);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class Sync
    extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -1645412544240373524L;
        private static final int RUNNING = 1;
        private static final int RAN = 2;
        private static final int CANCELLED = 4;
        private T result;
        private Throwable exception;
        private volatile Thread runner;

        private Sync() {
        }

        private boolean ranOrCancelled(int state) {
            return (state & 6) != 0;
        }

        @Override
        protected int tryAcquireShared(int ignore) {
            return this.isDone() ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignore) {
            this.runner = null;
            return true;
        }

        boolean isDone() {
            return this.ranOrCancelled(this.getState()) && this.runner == null;
        }

        T get() throws InterruptedException, ExecutionException {
            this.acquireSharedInterruptibly(0);
            if (this.getState() == 4) {
                throw new CancellationException();
            }
            if (this.exception != null) {
                throw new ExecutionException(this.exception);
            }
            return this.result;
        }

        void set(T v) {
            int s;
            do {
                if ((s = this.getState()) == 2) {
                    return;
                }
                if (s != 4) continue;
                this.releaseShared(0);
                return;
            } while (!this.compareAndSetState(s, 2));
            this.result = v;
            this.releaseShared(0);
        }

        void setException(Throwable t) {
            int s;
            do {
                if ((s = this.getState()) == 2) {
                    return;
                }
                if (s != 4) continue;
                this.releaseShared(0);
                return;
            } while (!this.compareAndSetState(s, 2));
            this.exception = t;
            this.result = null;
            this.releaseShared(0);
        }

        void cancel(boolean mayInterruptIfRunning) {
            Thread r;
            int s;
            do {
                if (!this.ranOrCancelled(s = this.getState())) continue;
                return;
            } while (!this.compareAndSetState(s, 4));
            if (mayInterruptIfRunning && (r = this.runner) != null) {
                r.interrupt();
            }
            this.releaseShared(0);
        }

        void run() {
            if (!this.compareAndSetState(0, 1)) {
                return;
            }
            try {
                this.runner = Thread.currentThread();
                if (this.getState() == 1) {
                    this.set(LazyReference.this.create());
                } else {
                    this.releaseShared(0);
                }
            }
            catch (Throwable ex) {
                this.setException(ex);
            }
        }
    }

    public static class InitializationException
    extends RuntimeException {
        private static final long serialVersionUID = 3638376010285456759L;

        InitializationException(ExecutionException e) {
            super(e.getCause() != null ? e.getCause() : e);
        }
    }
}

