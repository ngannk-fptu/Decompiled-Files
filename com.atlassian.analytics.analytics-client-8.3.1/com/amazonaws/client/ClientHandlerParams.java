/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.client;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.client.AwsSyncClientParams;

@SdkProtectedApi
@NotThreadSafe
public class ClientHandlerParams {
    private AwsSyncClientParams clientParams;
    private boolean disableStrictHostnameVerification = false;

    public AwsSyncClientParams getClientParams() {
        return this.clientParams;
    }

    public ClientHandlerParams withClientParams(AwsSyncClientParams clientParams) {
        this.clientParams = clientParams;
        return this;
    }

    public boolean isDisableStrictHostnameVerification() {
        return this.disableStrictHostnameVerification;
    }

    public ClientHandlerParams withDisableStrictHostnameVerification(boolean disableStrictHostnameVerification) {
        this.disableStrictHostnameVerification = disableStrictHostnameVerification;
        return this;
    }
}

