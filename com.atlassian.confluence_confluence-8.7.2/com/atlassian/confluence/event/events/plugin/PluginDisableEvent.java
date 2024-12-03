/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginEvent;

public class PluginDisableEvent
extends PluginEvent {
    private static final long serialVersionUID = 435854861965885406L;
    private final Scope scope = Scope.PERSISTENT;

    public PluginDisableEvent(Object src, String pluginKey) {
        this(src, pluginKey, Scope.PERSISTENT);
    }

    public PluginDisableEvent(Object src, String pluginKey, Scope scope) {
        super(src, pluginKey);
    }

    public Scope getScope() {
        return this.scope;
    }

    public static enum Scope {
        TEMPORARY,
        PERSISTENT;

    }
}

