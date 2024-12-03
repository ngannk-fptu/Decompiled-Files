/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.AsyncPluginEvent;
import com.atlassian.confluence.event.events.plugin.PluginDisableEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class AsyncPluginDisableEvent
extends AsyncPluginEvent {
    private static final long serialVersionUID = -5393835806944633764L;
    private final PluginDisableEvent.Scope scope;

    public AsyncPluginDisableEvent(Object src, String pluginKey, PluginDisableEvent.Scope scope) {
        super(src, pluginKey);
        this.scope = scope;
    }

    public PluginDisableEvent.Scope getScope() {
        return this.scope;
    }
}

