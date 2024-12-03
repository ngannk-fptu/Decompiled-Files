/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginModuleEvent;

public class PluginModuleDisableEvent
extends PluginModuleEvent {
    private static final long serialVersionUID = -1317777846350163374L;

    public PluginModuleDisableEvent(Object src, String completeModuleKey) {
        super(src, completeModuleKey);
    }
}

