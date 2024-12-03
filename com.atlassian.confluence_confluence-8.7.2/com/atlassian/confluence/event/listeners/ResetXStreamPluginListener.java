/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginUninstallEvent;
import com.atlassian.confluence.event.events.plugin.XStreamStateChangeEvent;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.atlassian.event.api.EventListener;

public final class ResetXStreamPluginListener {
    private final ConfluenceXStreamManager xStreamManager;

    public ResetXStreamPluginListener(ConfluenceXStreamManager xStreamManager) {
        this.xStreamManager = xStreamManager;
    }

    @EventListener
    public void handlePluginInstallEvent(PluginInstallEvent pluginInstallEvent) {
        this.xStreamManager.resetXStream();
    }

    @EventListener
    public void handlePluginUninstallEvent(PluginUninstallEvent pluginUninstallEvent) {
        this.xStreamManager.resetXStream();
    }

    @EventListener
    public void handleXStreamStateChangeEvent(XStreamStateChangeEvent xStreamEvent) {
        this.xStreamManager.resetXStream();
    }
}

