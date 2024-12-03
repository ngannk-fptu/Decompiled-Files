/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.util.concurrent.LazyReference
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroRegistrationHelper;
import com.atlassian.confluence.plugin.descriptor.UserMacroModuleDescriptor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMacroPluginMacroManager
implements MacroManager {
    private static final Logger log = LoggerFactory.getLogger(UserMacroPluginMacroManager.class);
    private final Map<String, Macro> macros = new ConcurrentHashMap<String, Macro>();
    private final MacroRegistrationHelper registrationHelper;

    public UserMacroPluginMacroManager(EventPublisher eventPublisher) {
        this.registrationHelper = new MacroRegistrationHelper(UserMacroModuleDescriptor.class, (MacroManager)this, eventPublisher);
    }

    @Override
    public Macro getMacroByName(@NonNull String macroName) {
        return this.macros.get(macroName);
    }

    @Override
    public void registerMacro(@NonNull String name, @NonNull Macro macro) {
        log.debug("Registering user macro: {}", (Object)name);
        this.macros.put(name, macro);
    }

    @Override
    public void unregisterMacro(@NonNull String name) {
        log.debug("Unregistering user macro: {}", (Object)name);
        this.macros.remove(name);
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.registrationHelper.pluginModuleEnabled(event.getModule());
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.registrationHelper.pluginModuleDisabled(event.getModule());
    }

    @Override
    @Deprecated
    public LazyReference<Macro> createLazyMacroReference(final ModuleDescriptor<?> moduleDescriptor) {
        return new LazyReference<Macro>(){

            protected Macro create() throws Exception {
                return ((UserMacroModuleDescriptor)moduleDescriptor).getModule();
            }
        };
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }
}

