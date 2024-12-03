/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.SdkBaseException;
import com.amazonaws.annotation.SdkInternalApi;

public class AmazonClientException
extends SdkBaseException {
    private static final long serialVersionUID = 1L;

    public AmazonClientException(String message, Throwable t) {
        super(message, t);
    }

    public AmazonClientException(String message) {
        super(message);
    }

    public AmazonClientException(Throwable t) {
        super(t);
    }

    @SdkInternalApi
    public boolean isRetryable() {
        return true;
    }
}

