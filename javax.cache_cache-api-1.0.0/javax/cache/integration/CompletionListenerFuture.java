/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.integration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.cache.integration.CompletionListener;

public class CompletionListenerFuture
implements CompletionListener,
Future<Void> {
    private boolean isCompleted = false;
    private Exception exception = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCompletion() throws IllegalStateException {
        CompletionListenerFuture completionListenerFuture = this;
        synchronized (completionListenerFuture) {
            if (this.isCompleted) {
                throw new IllegalStateException("Attempted to use a CompletionListenerFuture instance more than once");
            }
            this.isCompleted = true;
            this.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onException(Exception e) throws IllegalStateException {
        CompletionListenerFuture completionListenerFuture = this;
        synchronized (completionListenerFuture) {
            if (this.isCompleted) {
                throw new IllegalStateException("Attempted to use a CompletionListenerFuture instance more than once");
            }
            this.isCompleted = true;
            this.exception = e;
            this.notify();
        }
    }

    @Override
    public boolean cancel(boolean b) {
        throw new UnsupportedOperationException("CompletionListenerFutures can't be cancelled");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isDone() {
        CompletionListenerFuture completionListenerFuture = this;
        synchronized (completionListenerFuture) {
            return this.isCompleted;
        }
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        CompletionListenerFuture completionListenerFuture = this;
        synchronized (completionListenerFuture) {
            while (!this.isCompleted) {
                this.wait();
            }
            if (this.exception == null) {
                return null;
            }
            throw new ExecutionException(this.exception);
        }
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        CompletionListenerFuture completionListenerFuture = this;
        synchronized (completionListenerFuture) {
            if (!this.isCompleted) {
                unit.timedWait(this, timeout);
            }
            if (this.isCompleted) {
                if (this.exception == null) {
                    return null;
                }
                throw new ExecutionException(this.exception);
            }
            throw new TimeoutException();
        }
    }
}

