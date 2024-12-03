/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.ConfluenceEvent;

public class PluginFrameworkStartedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -1679493987533723208L;

    public PluginFrameworkStartedEvent(Object source) {
        super(source);
    }
}

