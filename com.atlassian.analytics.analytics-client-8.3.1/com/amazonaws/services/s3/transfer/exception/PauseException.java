/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.exception;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.transfer.PauseStatus;

public class PauseException
extends SdkClientException {
    private static final long serialVersionUID = 1L;
    private final PauseStatus status;

    public PauseException(PauseStatus status) {
        super("Failed to pause operation; status=" + (Object)((Object)status));
        if (status == null || status == PauseStatus.SUCCESS) {
            throw new IllegalArgumentException();
        }
        this.status = status;
    }

    public PauseStatus getPauseStatus() {
        return this.status;
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}

