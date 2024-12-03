/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.setup;

public class SetupException
extends Exception {
    public SetupException() {
    }

    public SetupException(String message) {
        super(message);
    }

    public SetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetupException(Throwable cause) {
        super(cause);
    }
}

