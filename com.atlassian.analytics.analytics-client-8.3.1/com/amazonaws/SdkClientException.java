/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.AmazonClientException;

public class SdkClientException
extends AmazonClientException {
    public SdkClientException(String message, Throwable t) {
        super(message, t);
    }

    public SdkClientException(String message) {
        super(message);
    }

    public SdkClientException(Throwable t) {
        super(t);
    }
}

