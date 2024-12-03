/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpRequestBase
 */
package com.amazonaws.http.timers.client;

import com.amazonaws.annotation.SdkInternalApi;
import org.apache.http.client.methods.HttpRequestBase;

@SdkInternalApi
public interface ClientExecutionAbortTask
extends Runnable {
    public void setCurrentHttpRequest(HttpRequestBase var1);

    public boolean hasClientExecutionAborted();

    public boolean isEnabled();

    public void cancel();
}

