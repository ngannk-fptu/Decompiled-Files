/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli.logging;

public class LogConfigurationException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LogConfigurationException() {
    }

    public LogConfigurationException(String message) {
        super(message);
    }

    public LogConfigurationException(Throwable cause) {
        super(cause);
    }

    public LogConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

