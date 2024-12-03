/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.event.events.admin.MacroMetadataChangedEvent;
import com.atlassian.confluence.event.events.admin.PluginMacroRegisteredEvent;
import com.atlassian.confluence.event.events.admin.PluginMacroUnregisteredEvent;
import com.atlassian.confluence.event.events.admin.UserMacroAddedEvent;
import com.atlassian.confluence.event.events.admin.UserMacroRemovedEvent;
import com.atlassian.confluence.macro.browser.MacroMetadataClientCacheKeyManager;
import com.atlassian.event.api.EventListener;

public class MacroMetadataListener {
    private final MacroMetadataClientCacheKeyManager macroMetadataClientCacheKeyManager;

    public MacroMetadataListener(MacroMetadataClientCacheKeyManager macroMetadataClientCacheKeyManager) {
        this.macroMetadataClientCacheKeyManager = macroMetadataClientCacheKeyManager;
    }

    @EventListener
    public void pluginMacroRegistered(PluginMacroRegisteredEvent event) {
        this.refreshKey();
    }

    @EventListener
    public void pluginMacroRegistered(PluginMacroUnregisteredEvent event) {
        this.refreshKey();
    }

    @EventListener
    public void userMacroAdded(UserMacroAddedEvent event) {
        this.refreshKey();
    }

    @EventListener
    public void userMacroRemoved(UserMacroRemovedEvent event) {
        this.refreshKey();
    }

    @EventListener
    public void macroMetadataChanged(MacroMetadataChangedEvent event) {
        this.refreshKey();
    }

    private void refreshKey() {
        this.macroMetadataClientCacheKeyManager.refreshKey();
    }
}

