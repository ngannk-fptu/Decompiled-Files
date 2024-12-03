/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginEvent;

public class PluginInstallEvent
extends PluginEvent {
    private static final long serialVersionUID = -6896814298122617991L;

    public PluginInstallEvent(Object src, String pluginKey) {
        super(src, pluginKey);
    }
}

