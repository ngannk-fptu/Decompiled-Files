/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.ex;

public class ConfigurationRuntimeException
extends RuntimeException {
    private static final long serialVersionUID = -7838702245512140996L;

    public ConfigurationRuntimeException() {
    }

    public ConfigurationRuntimeException(String message) {
        super(message);
    }

    public ConfigurationRuntimeException(String message, Object ... args) {
        super(String.format(message, args));
    }

    public ConfigurationRuntimeException(Throwable cause) {
        super(cause);
    }

    public ConfigurationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

