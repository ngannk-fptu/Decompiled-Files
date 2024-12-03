/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.exception;

import com.amazonaws.SdkClientException;

public class FileLockException
extends SdkClientException {
    private static final long serialVersionUID = 1L;

    public FileLockException(Throwable t) {
        super(t);
    }

    public FileLockException(String msg) {
        super(msg);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}

