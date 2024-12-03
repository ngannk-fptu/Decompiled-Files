/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.exception;

public class JfrWriteException
extends RuntimeException {
    public JfrWriteException(String message) {
        super(message);
    }

    public JfrWriteException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

