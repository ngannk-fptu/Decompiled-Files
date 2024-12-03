/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.persistence.confluence;

public class StaleObjectStateException
extends RuntimeException {
    public StaleObjectStateException() {
    }

    public StaleObjectStateException(String message) {
        super(message);
    }

    public StaleObjectStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaleObjectStateException(Throwable cause) {
        super(cause);
    }
}

