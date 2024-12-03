/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client.async;

import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.async.AsyncResult;
import org.apache.axis.client.async.IAsyncCallback;
import org.apache.axis.client.async.IAsyncResult;

public class AsyncCall {
    private Call call = null;
    private IAsyncCallback callback = null;

    public AsyncCall(Call call) {
        this(call, null);
    }

    public AsyncCall(Call call, IAsyncCallback callback) {
        this.call = call;
        this.callback = callback;
    }

    public IAsyncCallback getCallback() {
        return this.callback;
    }

    public void setCallback(IAsyncCallback callback) {
        this.callback = callback;
    }

    public IAsyncResult invoke(Object[] inputParams) {
        return new AsyncResult(this, null, inputParams);
    }

    public IAsyncResult invoke(QName qName, Object[] inputParams) {
        return new AsyncResult(this, qName, inputParams);
    }

    public Call getCall() {
        return this.call;
    }
}

