/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.exception;

public class JfrException
extends RuntimeException {
    public JfrException(String message, Throwable cause) {
        super(message, cause);
    }

    public JfrException(String message) {
        super(message);
    }
}

