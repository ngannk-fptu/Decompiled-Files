/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.impl.async;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.FutureListener;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class FutureClientResponseListener<T>
extends FutureTask<T>
implements FutureListener<ClientResponse> {
    private static final Callable NO_OP_CALLABLE = new Callable(){

        public Object call() throws Exception {
            throw new IllegalStateException();
        }
    };
    private Future<ClientResponse> f;

    public FutureClientResponseListener() {
        super(NO_OP_CALLABLE);
    }

    public void setCancelableFuture(Future<ClientResponse> f) {
        this.f = f;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.f.isCancelled()) {
            if (!super.isCancelled()) {
                super.cancel(true);
            }
            return false;
        }
        boolean cancelled = this.f.cancel(mayInterruptIfRunning);
        if (cancelled) {
            super.cancel(true);
        }
        return cancelled;
    }

    @Override
    public boolean isCancelled() {
        if (this.f.isCancelled()) {
            if (!super.isCancelled()) {
                super.cancel(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onComplete(Future<ClientResponse> response) {
        try {
            this.set(this.get(response.get()));
        }
        catch (CancellationException ex) {
            super.cancel(true);
        }
        catch (ExecutionException ex) {
            this.setException(ex.getCause());
        }
        catch (Throwable t) {
            this.setException(t);
        }
    }

    protected abstract T get(ClientResponse var1);
}

