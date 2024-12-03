/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.exception;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.exception.PluginExceptionInterception;

public class NoOpPluginExceptionInterception
implements PluginExceptionInterception {
    public static final PluginExceptionInterception NOOP_INTERCEPTION = new NoOpPluginExceptionInterception();

    private NoOpPluginExceptionInterception() {
    }

    @Override
    public boolean onEnableException(Plugin plugin, Exception pluginException) {
        return true;
    }
}

