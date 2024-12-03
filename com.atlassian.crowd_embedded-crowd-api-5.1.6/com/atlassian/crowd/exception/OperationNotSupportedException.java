/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.OperationFailedException;

public class OperationNotSupportedException
extends OperationFailedException {
    public OperationNotSupportedException() {
    }

    public OperationNotSupportedException(Throwable cause) {
        super(cause);
    }

    public OperationNotSupportedException(String message) {
        super(message);
    }

    public OperationNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}

