/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.OperationFailedException;

public class InvalidCrowdServiceException
extends OperationFailedException {
    public InvalidCrowdServiceException(String message) {
        super(message);
    }

    public InvalidCrowdServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

