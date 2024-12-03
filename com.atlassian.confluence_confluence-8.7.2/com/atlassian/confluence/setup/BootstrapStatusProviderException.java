/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

public class BootstrapStatusProviderException
extends UnsupportedOperationException {
    private static final String DEFAULT_MSG = "Method no longer supported due to security considerations.";

    public BootstrapStatusProviderException() {
        this(DEFAULT_MSG);
    }

    public BootstrapStatusProviderException(String message) {
        super(message);
    }

    public BootstrapStatusProviderException(Throwable cause) {
        super(cause);
    }

    public BootstrapStatusProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}

