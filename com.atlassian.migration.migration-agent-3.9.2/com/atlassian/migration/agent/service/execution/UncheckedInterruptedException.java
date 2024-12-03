/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.execution;

public class UncheckedInterruptedException
extends RuntimeException {
    public UncheckedInterruptedException() {
    }

    public UncheckedInterruptedException(String message) {
        super(message);
    }

    public UncheckedInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedInterruptedException(Throwable cause) {
        super(cause);
    }

    public UncheckedInterruptedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

