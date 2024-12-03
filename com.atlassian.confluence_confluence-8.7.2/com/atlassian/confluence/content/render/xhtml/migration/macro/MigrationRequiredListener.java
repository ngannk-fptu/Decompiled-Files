/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.confluence.content.render.xhtml.migration.macro.MacroMigrationService;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationRequiredListener {
    private static final Logger log = LoggerFactory.getLogger(MigrationRequiredListener.class);
    private final MacroMigrationService macroMigrationService;

    public MigrationRequiredListener(MacroMigrationService macroMigrationService) {
        this.macroMigrationService = macroMigrationService;
    }

    @EventListener
    public void handlePluginInstalledEvent(PluginModuleEnabledEvent e) {
        if (e.getModule() instanceof XhtmlMacroModuleDescriptor) {
            log.debug("Received plugin enabled event, updating migration required" + e.getModule().getClass());
            this.macroMigrationService.updateMigrationRequired();
        }
    }
}

