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
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.ResourceAware
 *  com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator
 *  com.atlassian.util.concurrent.LazyReference
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.V2CompatibilityMacro;
import com.atlassian.confluence.macro.V2CompatibilityModuleDescriptorPredicate;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroRegistrationHelper;
import com.atlassian.confluence.plugin.descriptor.CustomMacroModuleDescriptor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.renderer.v2.macro.ResourceAware;
import com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public class V2CompatibilityMacroManager
implements MacroManager {
    public static final String RESOURCE_PREFIX = "/download/resources/";
    private final Map<String, Macro> macros = new ConcurrentHashMap<String, Macro>();
    private final MacroRegistrationHelper registrationHelper;

    public V2CompatibilityMacroManager(V2CompatibilityModuleDescriptorPredicate v2CompatibilityModuleDescriptorPredicate, EventPublisher eventPublisher) {
        this.registrationHelper = new MacroRegistrationHelper(v2CompatibilityModuleDescriptorPredicate, (MacroManager)this, eventPublisher);
    }

    @Override
    public Macro getMacroByName(@NonNull String macroName) {
        return this.macros.get(macroName);
    }

    @Override
    public void registerMacro(@NonNull String name, @NonNull Macro macro) {
        this.macros.put(name, macro);
    }

    @Override
    public void unregisterMacro(@NonNull String name) {
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
                CustomMacroModuleDescriptor customMacroModuleDescriptor = (CustomMacroModuleDescriptor)moduleDescriptor;
                com.atlassian.renderer.v2.macro.Macro v2Macro = customMacroModuleDescriptor.getModule();
                if (!(v2Macro instanceof ResourceAware)) {
                    v2Macro = new ResourceAwareMacroDecorator(v2Macro);
                }
                ((ResourceAware)v2Macro).setResourcePath(V2CompatibilityMacroManager.RESOURCE_PREFIX + HtmlUtil.urlEncode(moduleDescriptor.getCompleteKey()));
                return new V2CompatibilityMacro(v2Macro, Macro.BodyType.NONE);
            }
        };
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }
}

