/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginEvent;

public class PluginEnableEvent
extends PluginEvent {
    private static final long serialVersionUID = 778607435153698887L;

    public PluginEnableEvent(Object src, String pluginKey) {
        super(src, pluginKey);
    }
}

