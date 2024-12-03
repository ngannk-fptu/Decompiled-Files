/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

public class PluginException
extends RuntimeException {
    public PluginException() {
    }

    public PluginException(String s) {
        super(s);
    }

    public PluginException(Throwable throwable) {
        super(throwable);
    }

    public PluginException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

