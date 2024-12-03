/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event.request;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.event.request.Progress;

@ThreadSafe
public class ProgressSupport
extends Progress {
    private volatile long requestContentLength = -1L;
    private volatile long requestBytesTransferred;
    private volatile long responseContentLength = -1L;
    private volatile long responseBytesTransferred;
    private static final Object lock = new Object();

    @Override
    public long getRequestContentLength() {
        return this.requestContentLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRequestContentLength(long contentLength) {
        if (contentLength < 0L) {
            throw new IllegalArgumentException();
        }
        Object object = lock;
        synchronized (object) {
            this.requestContentLength = this.requestContentLength == -1L ? contentLength : (this.requestContentLength += contentLength);
        }
    }

    @Override
    public long getRequestBytesTransferred() {
        return this.requestBytesTransferred;
    }

    @Override
    public long getResponseContentLength() {
        return this.responseContentLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addResponseContentLength(long contentLength) {
        if (contentLength < 0L) {
            throw new IllegalArgumentException();
        }
        Object object = lock;
        synchronized (object) {
            this.responseContentLength = this.responseContentLength == -1L ? contentLength : (this.responseContentLength += contentLength);
        }
    }

    @Override
    public long getResponseBytesTransferred() {
        return this.responseBytesTransferred;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRequestBytesTransferred(long bytes) {
        Object object = lock;
        synchronized (object) {
            this.requestBytesTransferred += bytes;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addResponseBytesTransferred(long bytes) {
        Object object = lock;
        synchronized (object) {
            this.responseBytesTransferred += bytes;
        }
    }

    public String toString() {
        return String.format("Request: %d/%d, Response: %d/%d", this.requestBytesTransferred, this.requestContentLength, this.responseBytesTransferred, this.responseContentLength);
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }
}

