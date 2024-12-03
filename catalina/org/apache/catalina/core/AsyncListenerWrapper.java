/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AsyncListenerWrapper {
    private AsyncListener listener = null;
    private ServletRequest servletRequest = null;
    private ServletResponse servletResponse = null;

    public void fireOnStartAsync(AsyncEvent event) throws IOException {
        this.listener.onStartAsync(this.customizeEvent(event));
    }

    public void fireOnComplete(AsyncEvent event) throws IOException {
        this.listener.onComplete(this.customizeEvent(event));
    }

    public void fireOnTimeout(AsyncEvent event) throws IOException {
        this.listener.onTimeout(this.customizeEvent(event));
    }

    public void fireOnError(AsyncEvent event) throws IOException {
        this.listener.onError(this.customizeEvent(event));
    }

    public AsyncListener getListener() {
        return this.listener;
    }

    public void setListener(AsyncListener listener) {
        this.listener = listener;
    }

    public void setServletRequest(ServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public void setServletResponse(ServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    private AsyncEvent customizeEvent(AsyncEvent event) {
        if (this.servletRequest != null && this.servletResponse != null) {
            return new AsyncEvent(event.getAsyncContext(), this.servletRequest, this.servletResponse, event.getThrowable());
        }
        return event;
    }
}

