/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginEvent;

public abstract class PluginModuleEvent
extends PluginEvent {
    private static final long serialVersionUID = -8834185592846618527L;

    public PluginModuleEvent(Object src, String completeModuleKey) {
        super(src, completeModuleKey);
    }
}

