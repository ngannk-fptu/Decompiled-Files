/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client.async;

import javax.xml.namespace.QName;
import org.apache.axis.client.async.AsyncCall;
import org.apache.axis.client.async.IAsyncCallback;
import org.apache.axis.client.async.IAsyncResult;
import org.apache.axis.client.async.Status;

public class AsyncResult
implements IAsyncResult,
Runnable {
    private Thread thread = null;
    private Object response = null;
    private Throwable exception = null;
    private AsyncCall ac = null;
    private QName opName = null;
    private Object[] params = null;
    private Status status = Status.NONE;

    public AsyncResult(AsyncCall ac, QName opName, Object[] params) {
        this.ac = ac;
        this.opName = opName;
        this.params = params;
        if (opName == null) {
            this.opName = ac.getCall().getOperationName();
        }
        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void abort() {
        this.thread.interrupt();
        this.status = Status.INTERRUPTED;
    }

    public Status getStatus() {
        return this.status;
    }

    public void waitFor(long timeout) throws InterruptedException {
        this.thread.wait(timeout);
    }

    public Object getResponse() {
        return this.response;
    }

    public Throwable getException() {
        return this.exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        try {
            try {
                this.response = this.ac.getCall().invoke(this.opName, this.params);
                this.status = Status.COMPLETED;
            }
            catch (Throwable e) {
                this.exception = e;
                this.status = Status.EXCEPTION;
                Object var3_2 = null;
                IAsyncCallback callback = this.ac.getCallback();
                if (callback != null) {
                    callback.onCompletion(this);
                }
            }
            Object var3_1 = null;
            IAsyncCallback callback = this.ac.getCallback();
            if (callback != null) {
                callback.onCompletion(this);
            }
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            IAsyncCallback callback = this.ac.getCallback();
            if (callback != null) {
                callback.onCompletion(this);
            }
            throw throwable;
        }
    }
}

