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
public class AsyncPluginInstallEvent
extends AsyncPluginEvent {
    private static final long serialVersionUID = -7713444093072157229L;

    public AsyncPluginInstallEvent(Object src, String pluginKey) {
        super(src, pluginKey);
    }
}

