/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ConstantThrowable;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedBlockingCallback {
    private static final Logger LOG = LoggerFactory.getLogger(SharedBlockingCallback.class);
    private static final Throwable IDLE = new ConstantThrowable("IDLE");
    private static final Throwable SUCCEEDED = new ConstantThrowable("SUCCEEDED");
    private static final Throwable FAILED = new ConstantThrowable("FAILED");
    private final ReentrantLock _lock = new ReentrantLock();
    private final Condition _idle = this._lock.newCondition();
    private final Condition _complete = this._lock.newCondition();
    private Blocker _blocker = new Blocker();

    public Blocker acquire() throws IOException {
        this._lock.lock();
        try {
            while (this._blocker._state != IDLE) {
                this._idle.await();
            }
            this._blocker._state = null;
            Blocker blocker = this._blocker;
            return blocker;
        }
        catch (InterruptedException x) {
            throw new InterruptedIOException();
        }
        finally {
            this._lock.unlock();
        }
    }

    public boolean fail(Throwable cause) {
        Objects.requireNonNull(cause);
        this._lock.lock();
        try {
            if (this._blocker._state == null) {
                this._blocker._state = new BlockerFailedException(cause);
                this._complete.signalAll();
                boolean bl = true;
                return bl;
            }
        }
        finally {
            this._lock.unlock();
        }
        return false;
    }

    protected void notComplete(Blocker blocker) {
        LOG.warn("Blocker not complete {}", (Object)blocker);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Blocker not complete stacktrace", new Throwable());
        }
    }

    public class Blocker
    implements Callback,
    Closeable {
        private Throwable _state = IDLE;

        protected Blocker() {
        }

        @Override
        public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
        }

        @Override
        public void succeeded() {
            SharedBlockingCallback.this._lock.lock();
            try {
                if (this._state == null) {
                    this._state = SUCCEEDED;
                    SharedBlockingCallback.this._complete.signalAll();
                } else {
                    LOG.warn("Succeeded after {}", (Object)this._state.toString());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("State", this._state);
                    }
                }
            }
            finally {
                SharedBlockingCallback.this._lock.unlock();
            }
        }

        @Override
        public void failed(Throwable cause) {
            SharedBlockingCallback.this._lock.lock();
            try {
                if (this._state == null) {
                    this._state = cause == null ? FAILED : (cause instanceof BlockerTimeoutException ? new IOException(cause) : cause);
                    SharedBlockingCallback.this._complete.signalAll();
                } else if (this._state instanceof BlockerTimeoutException || this._state instanceof BlockerFailedException) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Failed after {}", this._state);
                    }
                } else {
                    String msg = String.format("Failed after %s: %s", this._state, cause);
                    LOG.warn(msg);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(msg, this._state);
                        LOG.debug(msg, cause);
                    }
                }
            }
            finally {
                SharedBlockingCallback.this._lock.unlock();
            }
        }

        public void block() throws IOException {
            SharedBlockingCallback.this._lock.lock();
            try {
                while (this._state == null) {
                    SharedBlockingCallback.this._complete.await();
                }
                if (this._state == SUCCEEDED) {
                    return;
                }
                try {
                    if (this._state == IDLE) {
                        throw new IllegalStateException("IDLE");
                    }
                    if (this._state instanceof IOException) {
                        throw (IOException)this._state;
                    }
                    if (this._state instanceof CancellationException) {
                        throw (CancellationException)this._state;
                    }
                    if (this._state instanceof RuntimeException) {
                        throw (RuntimeException)this._state;
                    }
                    if (this._state instanceof Error) {
                        throw (Error)this._state;
                    }
                    throw new IOException(this._state);
                }
                catch (InterruptedException e) {
                    this._state = e;
                    throw new InterruptedIOException();
                }
            }
            finally {
                SharedBlockingCallback.this._lock.unlock();
            }
        }

        @Override
        public void close() {
            SharedBlockingCallback.this._lock.lock();
            try {
                if (this._state == IDLE) {
                    throw new IllegalStateException("IDLE");
                }
                if (this._state == null) {
                    SharedBlockingCallback.this.notComplete(this);
                }
            }
            finally {
                try {
                    if (this._state != null && this._state != SUCCEEDED) {
                        SharedBlockingCallback.this._blocker = new Blocker();
                    } else {
                        this._state = IDLE;
                    }
                    SharedBlockingCallback.this._idle.signalAll();
                    SharedBlockingCallback.this._complete.signalAll();
                }
                finally {
                    SharedBlockingCallback.this._lock.unlock();
                }
            }
        }

        public String toString() {
            SharedBlockingCallback.this._lock.lock();
            try {
                String string = String.format("%s@%x{%s}", Blocker.class.getSimpleName(), this.hashCode(), this._state);
                return string;
            }
            finally {
                SharedBlockingCallback.this._lock.unlock();
            }
        }
    }

    private static class BlockerFailedException
    extends Exception {
        public BlockerFailedException(Throwable cause) {
            super(cause);
        }
    }

    private static class BlockerTimeoutException
    extends TimeoutException {
        private BlockerTimeoutException() {
        }
    }
}

