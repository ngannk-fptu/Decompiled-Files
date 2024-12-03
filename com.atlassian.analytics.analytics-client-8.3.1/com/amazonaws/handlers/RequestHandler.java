/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.Request;
import com.amazonaws.util.TimingInfo;

@Deprecated
public interface RequestHandler {
    public void beforeRequest(Request<?> var1);

    public void afterResponse(Request<?> var1, Object var2, TimingInfo var3);

    public void afterError(Request<?> var1, Exception var2);
}

