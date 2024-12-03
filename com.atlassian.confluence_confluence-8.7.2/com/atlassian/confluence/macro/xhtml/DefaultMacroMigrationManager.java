/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.Supplier
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.macro.xhtml.MacroMigrationManager;
import com.atlassian.confluence.plugin.descriptor.xhtml.MacroMigrationModuleDescriptor;
import com.atlassian.confluence.renderer.v2.macros.V2UserMacroAdapter;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMacroMigrationManager
implements MacroMigrationManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultMacroMigrationManager.class);
    private final Map<String, MacroMigration> upgradeTasksByMacroName = new ConcurrentHashMap<String, MacroMigration>();
    private final MacroMigration richTextMacroMigration;
    private final MacroMigration plainTextMacroMigration;
    private final MacroMigration v2MacroToUnmigratedWikiMarkupMacroMigration;
    private final MacroManager xhtmlOnlyMacroManager;
    private final MacroManager v2CompatibleMacroManager;
    private final com.atlassian.confluence.renderer.MacroManager v2MacroManager;
    private final Set<String> whitelistedNames;

    public DefaultMacroMigrationManager(MacroMigration richTextMacroMigration, MacroMigration plainTextMacroMigration, MacroMigration v2MacroToUnmigratedWikiMarkupMacroMigration, MacroManager xhtmlOnlyMacroManager, MacroManager v2CompatibileMacroManager, com.atlassian.confluence.renderer.MacroManager v2MacroManager, Set<String> whitelistedNames) {
        this.richTextMacroMigration = richTextMacroMigration;
        this.plainTextMacroMigration = plainTextMacroMigration;
        this.v2MacroToUnmigratedWikiMarkupMacroMigration = v2MacroToUnmigratedWikiMarkupMacroMigration;
        this.xhtmlOnlyMacroManager = xhtmlOnlyMacroManager;
        this.v2CompatibleMacroManager = v2CompatibileMacroManager;
        this.v2MacroManager = v2MacroManager;
        this.whitelistedNames = new HashSet<String>(whitelistedNames);
        this.readAdditionalWhitelistedMacroNames();
    }

    private void readAdditionalWhitelistedMacroNames() {
        String whitelist = System.getProperty("confluence.macro.migration.white.list");
        if (StringUtils.isNotBlank((CharSequence)whitelist)) {
            String[] macroNames;
            for (String macroName : macroNames = StringUtils.split((String)whitelist, (char)',')) {
                this.whitelistedNames.add(StringUtils.trim((String)macroName));
            }
        }
    }

    @Override
    public MacroMigration getMacroMigration(String macroName) {
        com.atlassian.renderer.v2.macro.Macro v2Macro = this.v2MacroManager.getEnabledMacro(macroName);
        if (v2Macro == null) {
            log.info("The macro '{}' is not found so cannot be migrated.", (Object)macroName);
            return null;
        }
        if (this.upgradeTasksByMacroName.containsKey(macroName)) {
            return this.upgradeTasksByMacroName.get(macroName);
        }
        Macro xhtmlMacro = this.getXhtmlMacro(macroName, v2Macro);
        if (xhtmlMacro == null) {
            xhtmlMacro = this.getXhtmlCompatibleWrappedV2Macro(macroName);
        }
        if (xhtmlMacro == null) {
            return this.v2MacroToUnmigratedWikiMarkupMacroMigration;
        }
        MacroMigration macroMigration = this.plainTextMacroMigration;
        if (xhtmlMacro.getBodyType() == Macro.BodyType.RICH_TEXT) {
            macroMigration = this.richTextMacroMigration;
        }
        return macroMigration;
    }

    private Macro getXhtmlMacro(String macroName, com.atlassian.renderer.v2.macro.Macro v2Macro) {
        Macro xhtmlMacro = null;
        xhtmlMacro = v2Macro instanceof V2UserMacroAdapter ? ((V2UserMacroAdapter)v2Macro).getXhtmlMacro() : this.xhtmlOnlyMacroManager.getMacroByName(macroName);
        return xhtmlMacro;
    }

    private Macro getXhtmlCompatibleWrappedV2Macro(String name) {
        if (!this.whitelistedNames.contains(name)) {
            return null;
        }
        return this.v2CompatibleMacroManager.getMacroByName(name);
    }

    private void registerMacroMigrator(final MacroMigrationModuleDescriptor migratorDescriptor) {
        String macroName = migratorDescriptor.getMacroName();
        this.upgradeTasksByMacroName.put(macroName, new LazyMacroMigrationWrapper((Supplier<MacroMigration>)new LazyReference<MacroMigration>(){

            protected MacroMigration create() throws Exception {
                return migratorDescriptor.getModule();
            }
        }));
    }

    private void unregisterMacroMigrator(MacroMigrationModuleDescriptor migratorDescriptor) {
        this.upgradeTasksByMacroName.remove(migratorDescriptor.getMacroName());
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        ModuleDescriptor moduleDescriptor = event.getModule();
        if (!(moduleDescriptor instanceof MacroMigrationModuleDescriptor)) {
            return;
        }
        this.registerMacroMigrator((MacroMigrationModuleDescriptor)moduleDescriptor);
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        ModuleDescriptor moduleDescriptor = event.getModule();
        if (!(moduleDescriptor instanceof MacroMigrationModuleDescriptor)) {
            return;
        }
        this.unregisterMacroMigrator((MacroMigrationModuleDescriptor)moduleDescriptor);
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }

    private static class LazyMacroMigrationWrapper
    implements MacroMigration {
        private final Supplier<MacroMigration> delegate;

        LazyMacroMigrationWrapper(Supplier<MacroMigration> delegate) {
            this.delegate = delegate;
        }

        @Override
        public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
            return ((MacroMigration)this.delegate.get()).migrate(macro, context);
        }
    }
}

