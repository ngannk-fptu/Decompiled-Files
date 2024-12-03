/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.xhtml.MacroManager
 *  com.atlassian.confluence.plugin.descriptor.OutputDeviceTypeMacroModuleDescriptorPredicate
 *  com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.mobile.render;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.plugin.descriptor.OutputDeviceTypeMacroModuleDescriptorPredicate;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class MobileMacroManager
implements MacroManager {
    public static final String OUTPUT_DEVICE_TYPE = "mobile";
    private static final ModuleDescriptorPredicate<Macro> PREDICATE = new OutputDeviceTypeMacroModuleDescriptorPredicate((Set)ImmutableSet.of((Object)"mobile"));
    private final PluginAccessor pluginAccessor;
    private final MacroManager delegateMacroManager;
    private final MacroManager userMacroManager;
    private final Set<String> whiteListedMacroNames;
    private Set<String> mobileMacroNames;

    public MobileMacroManager(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, MacroManager delegateMacroManager, MacroManager userMacroManager, Set<String> whiteListedMacroNames) {
        this.pluginAccessor = pluginAccessor;
        this.delegateMacroManager = delegateMacroManager;
        this.userMacroManager = userMacroManager;
        this.whiteListedMacroNames = whiteListedMacroNames;
        this.findMobileSpecificMacros();
        pluginEventManager.register((Object)this);
    }

    private void findMobileSpecificMacros() {
        HashSet<String> macroNames = new HashSet<String>();
        Collection moduleDescriptors = this.pluginAccessor.getModuleDescriptors(arg_0 -> PREDICATE.matches(arg_0));
        for (ModuleDescriptor descriptor : moduleDescriptors) {
            macroNames.add(descriptor.getName());
        }
        this.mobileMacroNames = new CopyOnWriteArraySet<String>(macroNames);
    }

    public Macro getMacroByName(String macroName) {
        Macro userMacro = this.userMacroManager.getMacroByName(macroName);
        if (userMacro != null) {
            return userMacro;
        }
        if (this.mobileMacroNames.contains(macroName) || this.whiteListedMacroNames.contains(macroName)) {
            return this.delegateMacroManager.getMacroByName(macroName);
        }
        return null;
    }

    public void registerMacro(String name, Macro macro) {
        this.mobileMacroNames.add(name);
    }

    public void unregisterMacro(String name) {
        this.mobileMacroNames.remove(name);
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (this.isMobileMacro(event.getModule())) {
            this.registerMacro(event.getModule().getName(), null);
        }
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        if (this.isMobileMacro(event.getModule())) {
            this.unregisterMacro(event.getModule().getName());
        }
    }

    public LazyReference<Macro> createLazyMacroReference(ModuleDescriptor<?> moduleDescriptor) {
        return this.delegateMacroManager.createLazyMacroReference(moduleDescriptor);
    }

    private boolean isMobileMacro(ModuleDescriptor descriptor) {
        if (descriptor instanceof XhtmlMacroModuleDescriptor) {
            XhtmlMacroModuleDescriptor xhtmlModuleDescriptor = (XhtmlMacroModuleDescriptor)descriptor;
            return xhtmlModuleDescriptor.isOutputDeviceTypeSupported(OUTPUT_DEVICE_TYPE);
        }
        return false;
    }
}

