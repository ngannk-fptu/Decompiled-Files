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
public class AsyncPluginEnableEvent
extends AsyncPluginEvent {
    private static final long serialVersionUID = 1039973161668684828L;

    public AsyncPluginEnableEvent(Object src, String pluginKey) {
        super(src, pluginKey);
    }
}

