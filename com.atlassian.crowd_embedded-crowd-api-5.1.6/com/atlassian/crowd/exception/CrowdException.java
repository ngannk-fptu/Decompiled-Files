/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

public abstract class CrowdException
extends Exception {
    public CrowdException() {
    }

    public CrowdException(String message) {
        super(message);
    }

    public CrowdException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrowdException(Throwable cause) {
        super(cause);
    }
}

