/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

public class PluginAssertionFailure
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Throwable cause = null;

    public PluginAssertionFailure(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    public PluginAssertionFailure(String msg) {
        super(msg);
    }

    public PluginAssertionFailure(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

