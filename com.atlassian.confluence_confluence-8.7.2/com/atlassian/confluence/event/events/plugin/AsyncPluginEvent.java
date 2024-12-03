/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public abstract class AsyncPluginEvent
extends ConfluenceEvent {
    private final String pluginKey;

    public AsyncPluginEvent(Object src, String pluginKey) {
        super(src);
        this.pluginKey = pluginKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }
}

