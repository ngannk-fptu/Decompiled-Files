/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpRequestBase
 */
package com.amazonaws.http.timers.client;

import com.amazonaws.http.timers.client.ClientExecutionAbortTrackerTask;
import org.apache.http.client.methods.HttpRequestBase;

public class NoOpClientExecutionAbortTrackerTask
implements ClientExecutionAbortTrackerTask {
    public static final NoOpClientExecutionAbortTrackerTask INSTANCE = new NoOpClientExecutionAbortTrackerTask();

    private NoOpClientExecutionAbortTrackerTask() {
    }

    @Override
    public void setCurrentHttpRequest(HttpRequestBase newRequest) {
    }

    @Override
    public boolean hasTimeoutExpired() {
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

