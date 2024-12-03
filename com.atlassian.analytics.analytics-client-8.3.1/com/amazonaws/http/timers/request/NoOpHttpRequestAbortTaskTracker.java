/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.timers.request;

import com.amazonaws.http.timers.request.HttpRequestAbortTaskTracker;

public class NoOpHttpRequestAbortTaskTracker
implements HttpRequestAbortTaskTracker {
    public static final NoOpHttpRequestAbortTaskTracker INSTANCE = new NoOpHttpRequestAbortTaskTracker();

    private NoOpHttpRequestAbortTaskTracker() {
    }

    @Override
    public boolean httpRequestAborted() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void cancelTask() {
    }
}

