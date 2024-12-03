/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.client.AsyncResponseImpl;
import javax.xml.ws.WebServiceException;

public abstract class AsyncInvoker
implements Runnable {
    protected AsyncResponseImpl responseImpl;
    protected boolean nonNullAsyncHandlerGiven;

    public void setReceiver(AsyncResponseImpl responseImpl) {
        this.responseImpl = responseImpl;
    }

    public AsyncResponseImpl getResponseImpl() {
        return this.responseImpl;
    }

    public void setResponseImpl(AsyncResponseImpl responseImpl) {
        this.responseImpl = responseImpl;
    }

    public boolean isNonNullAsyncHandlerGiven() {
        return this.nonNullAsyncHandlerGiven;
    }

    public void setNonNullAsyncHandlerGiven(boolean nonNullAsyncHandlerGiven) {
        this.nonNullAsyncHandlerGiven = nonNullAsyncHandlerGiven;
    }

    @Override
    public void run() {
        try {
            this.do_run();
        }
        catch (WebServiceException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new WebServiceException(t);
        }
    }

    public abstract void do_run();
}

