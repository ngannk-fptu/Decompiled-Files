/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class OperationFailedException
extends CrowdException {
    public OperationFailedException() {
    }

    public OperationFailedException(Throwable cause) {
        super(cause);
    }

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

