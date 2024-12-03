/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginUninstallEvent;
import com.atlassian.event.api.EventListener;

public final class ConfluenceCachingBandanaListener {
    private final BandanaPersister bandanaPersister;

    public ConfluenceCachingBandanaListener(BandanaPersister bandanaPersister) {
        this.bandanaPersister = bandanaPersister;
    }

    @EventListener
    public void handlePluginInstallEvent(PluginInstallEvent pluginInstallEvent) {
        this.bandanaPersister.flushCaches();
    }

    @EventListener
    public void handlePluginUninstallEvent(PluginUninstallEvent pluginUninstallEvent) {
        this.bandanaPersister.flushCaches();
    }
}

