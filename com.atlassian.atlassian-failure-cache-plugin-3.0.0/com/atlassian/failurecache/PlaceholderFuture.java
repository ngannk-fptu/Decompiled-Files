/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ForwardingListenableFuture
 *  com.google.common.util.concurrent.ListenableFuture
 *  javax.annotation.Nullable
 */
package com.atlassian.failurecache;

import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class PlaceholderFuture<V>
extends ForwardingListenableFuture<V> {
    private static final long TEN_SECONDS = TimeUnit.SECONDS.toMillis(10L);
    private final long invalidAfterTimeInMillis = System.currentTimeMillis() + TEN_SECONDS;
    private volatile ListenableFuture<V> delegate;

    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegate != null && this.delegate.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return this.delegate != null && this.delegate.isCancelled();
    }

    public boolean isDone() {
        return this.delegate != null ? this.delegate.isDone() : System.currentTimeMillis() >= this.invalidAfterTimeInMillis;
    }

    protected ListenableFuture<V> delegate() {
        return this.delegate;
    }

    public void setDelegate(@Nullable ListenableFuture<V> delegate) {
        this.delegate = delegate;
    }
}

