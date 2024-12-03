/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginException;

public class IllegalPluginStateException
extends PluginException {
    public IllegalPluginStateException() {
    }

    public IllegalPluginStateException(String s) {
        super(s);
    }

    public IllegalPluginStateException(Throwable throwable) {
        super(throwable);
    }

    public IllegalPluginStateException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

