/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.plugins.PluginException;

public class PluginInvalidInputException
extends PluginException {
    private Throwable cause = null;

    public PluginInvalidInputException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    public PluginInvalidInputException(String msg) {
        super(msg);
    }

    public PluginInvalidInputException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

