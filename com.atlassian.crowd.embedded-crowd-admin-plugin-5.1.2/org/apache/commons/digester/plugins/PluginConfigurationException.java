/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

public class PluginConfigurationException
extends RuntimeException {
    private Throwable cause = null;

    public PluginConfigurationException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    public PluginConfigurationException(String msg) {
        super(msg);
    }

    public PluginConfigurationException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

