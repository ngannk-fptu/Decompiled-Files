/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;

public class OperationFailedException
extends CrowdRuntimeException {
    public OperationFailedException() {
    }

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationFailedException(Throwable cause) {
        this(null, cause);
    }
}

