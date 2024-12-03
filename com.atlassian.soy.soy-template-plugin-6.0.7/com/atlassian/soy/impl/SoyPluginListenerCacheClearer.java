/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.soy.impl.SoyManager
 */
package com.atlassian.soy.impl;

import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.soy.impl.SoyManager;

public class SoyPluginListenerCacheClearer {
    private final SoyManager soyManager;
    private final PluginEventManager pluginEventManager;

    public SoyPluginListenerCacheClearer(SoyManager soyManager, PluginEventManager pluginEventManager) {
        this.soyManager = soyManager;
        this.pluginEventManager = pluginEventManager;
    }

    public void registerEventListeners() {
        this.pluginEventManager.register((Object)this);
    }

    public void unregisterEventListeners() {
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.soyManager.clearCaches(null);
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.soyManager.clearCaches(null);
    }
}

