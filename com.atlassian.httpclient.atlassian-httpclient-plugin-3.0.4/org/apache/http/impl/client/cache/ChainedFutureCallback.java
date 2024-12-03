/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;

class ChainedFutureCallback<T>
implements FutureCallback<T> {
    private final BasicFuture<T> wrapped;

    public ChainedFutureCallback(BasicFuture<T> delegate) {
        this.wrapped = delegate;
    }

    @Override
    public void completed(T result) {
        this.wrapped.completed(result);
    }

    @Override
    public void failed(Exception ex) {
        this.wrapped.failed(ex);
    }

    @Override
    public void cancelled() {
        this.wrapped.cancel();
    }
}

