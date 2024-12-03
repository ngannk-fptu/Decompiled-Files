/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.waiters.WaiterHandler;

@SdkInternalApi
public class NoOpWaiterHandler
extends WaiterHandler<AmazonWebServiceRequest> {
    @Override
    public void onWaitSuccess(AmazonWebServiceRequest request) {
    }

    @Override
    public void onWaitFailure(Exception e) {
    }
}

