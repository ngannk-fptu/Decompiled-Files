/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.AsyncHandler
 *  javax.xml.ws.Response
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Cancelable;
import com.sun.xml.ws.client.ResponseContext;
import com.sun.xml.ws.client.ResponseContextReceiver;
import com.sun.xml.ws.util.CompletedFuture;
import java.util.Map;
import java.util.concurrent.FutureTask;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

public final class AsyncResponseImpl<T>
extends FutureTask<T>
implements Response<T>,
ResponseContextReceiver {
    private final AsyncHandler<T> handler;
    private ResponseContext responseContext;
    private final Runnable callable;
    private Cancelable cancelable;

    public AsyncResponseImpl(Runnable runnable, @Nullable AsyncHandler<T> handler) {
        super(runnable, null);
        this.callable = runnable;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            this.callable.run();
        }
        catch (WebServiceException e) {
            this.set(null, e);
        }
        catch (Throwable e) {
            this.set(null, new WebServiceException(e));
        }
    }

    public ResponseContext getContext() {
        return this.responseContext;
    }

    @Override
    public void setResponseContext(ResponseContext rc) {
        this.responseContext = rc;
    }

    public void set(T v, Throwable t) {
        if (this.handler != null) {
            try {
                class CallbackFuture<T>
                extends CompletedFuture<T>
                implements Response<T> {
                    public CallbackFuture(T v, Throwable t) {
                        super(v, t);
                    }

                    public Map<String, Object> getContext() {
                        return AsyncResponseImpl.this.getContext();
                    }
                }
                this.handler.handleResponse(new CallbackFuture<T>(v, t));
            }
            catch (Throwable e) {
                super.setException(e);
                return;
            }
        }
        if (t != null) {
            super.setException(t);
        } else {
            super.set(v);
        }
    }

    public void setCancelable(Cancelable cancelable) {
        this.cancelable = cancelable;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.cancelable != null) {
            this.cancelable.cancel(mayInterruptIfRunning);
        }
        return super.cancel(mayInterruptIfRunning);
    }
}

