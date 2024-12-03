/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.retry.internal.CredentialsEndpointRetryParameters;

@SdkInternalApi
public interface CredentialsEndpointRetryPolicy {
    public static final CredentialsEndpointRetryPolicy NO_RETRY = new CredentialsEndpointRetryPolicy(){

        @Override
        public boolean shouldRetry(int retriesAttempted, CredentialsEndpointRetryParameters retryParams) {
            return false;
        }
    };

    public boolean shouldRetry(int var1, CredentialsEndpointRetryParameters var2);
}

