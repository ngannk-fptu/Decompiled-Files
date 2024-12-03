/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.waiters.WaiterState;

@SdkProtectedApi
public abstract class WaiterAcceptor<Output> {
    public boolean matches(Output output) {
        return false;
    }

    public boolean matches(AmazonServiceException output) {
        return false;
    }

    public abstract WaiterState getState();
}

