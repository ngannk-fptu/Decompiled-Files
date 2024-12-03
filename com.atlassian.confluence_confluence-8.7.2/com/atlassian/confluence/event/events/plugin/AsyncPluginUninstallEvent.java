/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.AsyncPluginEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class AsyncPluginUninstallEvent
extends AsyncPluginEvent {
    private static final long serialVersionUID = 8291451507661018081L;
    private String pluginName;

    public AsyncPluginUninstallEvent(Object src, String pluginKey, String pluginName) {
        super(src, pluginKey);
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return this.pluginName;
    }
}

