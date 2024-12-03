/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.timers.request;

import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public interface HttpRequestAbortTaskTracker {
    public boolean httpRequestAborted();

    public boolean isEnabled();

    public void cancelTask();
}

