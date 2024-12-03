/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.exception;

public class InfrastructureException
extends RuntimeException {
    public InfrastructureException(Throwable cause) {
        super(cause);
    }

    public InfrastructureException(String msg) {
        super(msg);
    }

    public InfrastructureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

