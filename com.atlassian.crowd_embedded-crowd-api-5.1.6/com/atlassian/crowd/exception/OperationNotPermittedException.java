/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class OperationNotPermittedException
extends CrowdException {
    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }

    public OperationNotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationNotPermittedException(Throwable cause) {
        super(cause);
    }
}

