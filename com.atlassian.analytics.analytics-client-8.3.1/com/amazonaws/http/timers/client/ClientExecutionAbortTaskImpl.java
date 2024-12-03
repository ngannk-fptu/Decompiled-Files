/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpRequestBase
 */
package com.amazonaws.http.timers.client;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.timers.client.ClientExecutionAbortTask;
import org.apache.http.client.methods.HttpRequestBase;

@SdkInternalApi
public class ClientExecutionAbortTaskImpl
implements ClientExecutionAbortTask {
    private volatile boolean hasTaskExecuted;
    private HttpRequestBase currentHttpRequest;
    private final Thread thread;
    private volatile boolean isCancelled;
    private final Object lock = new Object();

    public ClientExecutionAbortTaskImpl(Thread thread) {
        this.thread = thread;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Object object = this.lock;
        synchronized (object) {
            if (this.isCancelled) {
                return;
            }
            this.hasTaskExecuted = true;
            if (!this.thread.isInterrupted()) {
                this.thread.interrupt();
            }
            if (!this.currentHttpRequest.isAborted()) {
                this.currentHttpRequest.abort();
            }
        }
    }

    @Override
    public void setCurrentHttpRequest(HttpRequestBase newRequest) {
        this.currentHttpRequest = newRequest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hasClientExecutionAborted() {
        Object object = this.lock;
        synchronized (object) {
            return this.hasTaskExecuted;
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancel() {
        Object object = this.lock;
        synchronized (object) {
            this.isCancelled = true;
        }
    }
}

