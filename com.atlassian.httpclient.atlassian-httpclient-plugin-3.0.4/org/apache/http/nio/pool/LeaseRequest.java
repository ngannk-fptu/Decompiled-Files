/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.pool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.pool.PoolEntry;
import org.apache.http.util.Asserts;

class LeaseRequest<T, C, E extends PoolEntry<T, C>>
implements Cancellable {
    private final T route;
    private final Object state;
    private final long connectTimeout;
    private final long deadline;
    private final BasicFuture<E> future;
    private final AtomicReference<SessionRequest> sessionRequestRef;
    private final AtomicBoolean completed;
    private volatile E result;
    private volatile Exception ex;

    public LeaseRequest(T route, Object state, long connectTimeout, long leaseTimeout, BasicFuture<E> future) {
        this.route = route;
        this.state = state;
        this.connectTimeout = connectTimeout;
        this.deadline = leaseTimeout > 0L ? System.currentTimeMillis() + leaseTimeout : Long.MAX_VALUE;
        this.future = future;
        this.sessionRequestRef = new AtomicReference<Object>(null);
        this.completed = new AtomicBoolean(false);
    }

    public T getRoute() {
        return this.route;
    }

    public Object getState() {
        return this.state;
    }

    public long getConnectTimeout() {
        return this.connectTimeout;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public boolean isDone() {
        return this.completed.get();
    }

    public void attachSessionRequest(SessionRequest sessionRequest) {
        Asserts.check(this.sessionRequestRef.compareAndSet(null, sessionRequest), "Session request has already been set");
    }

    @Override
    public boolean cancel() {
        boolean cancelled = this.completed.compareAndSet(false, true);
        SessionRequest sessionRequest = this.sessionRequestRef.getAndSet(null);
        if (sessionRequest != null) {
            sessionRequest.cancel();
        }
        return cancelled;
    }

    public void failed(Exception ex) {
        if (this.completed.compareAndSet(false, true)) {
            this.ex = ex;
        }
    }

    public void completed(E result) {
        if (this.completed.compareAndSet(false, true)) {
            this.result = result;
        }
    }

    public BasicFuture<E> getFuture() {
        return this.future;
    }

    public E getResult() {
        return this.result;
    }

    public Exception getException() {
        return this.ex;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        buffer.append(this.route);
        buffer.append("][");
        buffer.append(this.state);
        buffer.append("]");
        return buffer.toString();
    }
}

