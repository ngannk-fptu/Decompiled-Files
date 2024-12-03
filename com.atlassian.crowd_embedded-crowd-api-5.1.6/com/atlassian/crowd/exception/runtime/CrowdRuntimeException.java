/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

public class CrowdRuntimeException
extends RuntimeException {
    public CrowdRuntimeException() {
    }

    public CrowdRuntimeException(String message) {
        super(message);
    }

    public CrowdRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrowdRuntimeException(Throwable cause) {
        super(cause);
    }
}

