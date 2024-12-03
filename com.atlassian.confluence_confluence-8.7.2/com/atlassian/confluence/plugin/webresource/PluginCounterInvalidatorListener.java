/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.event.events.plugin.PluginEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.plugin.webresource.Counter;
import com.atlassian.event.api.EventListener;

public final class PluginCounterInvalidatorListener {
    private final Counter pluginCounter;
    private boolean started = false;

    public PluginCounterInvalidatorListener(Counter pluginCounter) {
        this.pluginCounter = pluginCounter;
    }

    @EventListener
    public void handlePluginEvent(PluginEvent pluginEvent) {
        if (this.started) {
            this.pluginCounter.updateCounter();
        }
    }

    @EventListener
    public void handlePluginFrameworkStartedEvent(PluginFrameworkStartedEvent pluginFrameworkStartedEvent) {
        this.started = true;
    }
}

