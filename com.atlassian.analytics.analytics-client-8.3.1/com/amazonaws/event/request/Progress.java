/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event.request;

import com.amazonaws.annotation.ThreadSafe;

@ThreadSafe
public class Progress {
    public static final Progress NOOP = new Progress();
    private static final String MSG = "No progress tracking configured";

    protected Progress() {
    }

    public boolean isEnabled() {
        return false;
    }

    public void addRequestBytesTransferred(long bytes) {
    }

    public void addResponseBytesTransferred(long bytes) {
    }

    public long getRequestContentLength() {
        throw new UnsupportedOperationException(MSG);
    }

    public void addRequestContentLength(long contentLength) {
    }

    public long getRequestBytesTransferred() {
        throw new UnsupportedOperationException(MSG);
    }

    public long getResponseContentLength() {
        throw new UnsupportedOperationException(MSG);
    }

    public void addResponseContentLength(long contentLength) {
    }

    public long getResponseBytesTransferred() {
        throw new UnsupportedOperationException(MSG);
    }
}

