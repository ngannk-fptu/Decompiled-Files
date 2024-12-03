/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginException;

public class PluginParseException
extends PluginException {
    public PluginParseException() {
    }

    public PluginParseException(String s) {
        super(s);
    }

    public PluginParseException(Throwable throwable) {
        super(throwable);
    }

    public PluginParseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

