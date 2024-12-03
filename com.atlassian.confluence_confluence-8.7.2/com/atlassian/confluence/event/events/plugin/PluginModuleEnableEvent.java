/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginModuleEvent;

public class PluginModuleEnableEvent
extends PluginModuleEvent {
    private static final long serialVersionUID = 4269254696983206020L;

    public PluginModuleEnableEvent(Object src, String completeModuleKey) {
        super(src, completeModuleKey);
    }
}

