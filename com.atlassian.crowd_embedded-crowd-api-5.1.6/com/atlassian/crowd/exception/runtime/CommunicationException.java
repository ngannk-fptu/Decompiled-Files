/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

import com.atlassian.crowd.exception.runtime.OperationFailedException;

public class CommunicationException
extends OperationFailedException {
    public CommunicationException(Throwable cause) {
        super(cause);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

