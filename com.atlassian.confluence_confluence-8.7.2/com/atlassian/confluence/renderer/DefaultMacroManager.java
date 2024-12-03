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
 *  com.atlassian.renderer.v2.macro.ResourceAware
 *  com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator
 *  com.google.common.collect.Maps
 *  io.atlassian.util.concurrent.LazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.plugin.descriptor.MacroModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.UserMacroModuleDescriptor;
import com.atlassian.confluence.renderer.LazyLoadedMacroDecorator;
import com.atlassian.confluence.renderer.MacroManager;
import com.atlassian.confluence.renderer.UserMacroLibrary;
import com.atlassian.confluence.renderer.v2.macros.V2UserMacroAdapter;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.ResourceAware;
import com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator;
import com.google.common.collect.Maps;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMacroManager
implements MacroManager {
    public static final String RESOURCE_PREFIX = "/download/resources/";
    private static final Logger log = LoggerFactory.getLogger(DefaultMacroManager.class);
    private final Map<String, Macro> macros = Maps.newConcurrentMap();
    private UserMacroLibrary userMacroLibrary;

    public Macro getEnabledMacro(String name) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to retrieve macro: " + name);
        }
        if (name == null) {
            return null;
        }
        Macro pluginMacro = this.macros.get(name = name.toLowerCase());
        if (pluginMacro != null) {
            if (pluginMacro instanceof LazyLoadedMacroDecorator) {
                return ((LazyLoadedMacroDecorator)pluginMacro).getMacro();
            }
            return pluginMacro;
        }
        if (this.userMacroLibrary.hasMacro(name)) {
            return new V2UserMacroAdapter(this.userMacroLibrary.getMacro(name).toMacro());
        }
        return null;
    }

    @Override
    public Map<String, Macro> getMacros() {
        return Collections.unmodifiableMap(this.macros);
    }

    @Override
    public void registerMacro(String name, Macro macro) {
        if (this.macros.containsKey(name = name.toLowerCase())) {
            log.warn("Unregistering existing macro '" + name + "' (" + this.macros.get(name) + ") to replace with macro with same name (" + macro + ")");
        }
        this.macros.put(name, macro);
        log.debug("Registered macro '" + name + "', is now: " + this.macros.get(name));
    }

    @Override
    public void unregisterMacro(String name) {
        name = name.toLowerCase();
        Macro macro = this.macros.remove(name.toLowerCase());
        log.debug("Unregistered macro '" + name + "', was: " + macro);
    }

    private void registerPluginMacro(final MacroModuleDescriptor descriptor) {
        log.info("Enabling plugin macro: " + descriptor.getCompleteKey());
        this.registerMacro(descriptor.getName(), new LazyLoadedMacroDecorator(descriptor.getCompleteKey(), new LazyReference<Macro>(){

            protected Macro create() {
                Macro macro = (Macro)descriptor.getModule();
                if (!(macro instanceof ResourceAware)) {
                    macro = new ResourceAwareMacroDecorator(macro);
                }
                ((ResourceAware)macro).setResourcePath(DefaultMacroManager.RESOURCE_PREFIX + HtmlUtil.urlEncode(descriptor.getCompleteKey()));
                return macro;
            }
        }));
    }

    private void registerPluginUserMacro(final UserMacroModuleDescriptor descriptor) {
        log.info("Enabling plugin user macro: " + descriptor.getCompleteKey());
        this.registerMacro(descriptor.getName(), new LazyLoadedMacroDecorator(descriptor.getCompleteKey(), new LazyReference<Macro>(){

            protected Macro create() {
                return new V2UserMacroAdapter(descriptor.getModule());
            }
        }));
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        ModuleDescriptor moduleDescriptor = event.getModule();
        if (moduleDescriptor instanceof MacroModuleDescriptor) {
            this.registerPluginMacro((MacroModuleDescriptor)moduleDescriptor);
        } else if (moduleDescriptor instanceof UserMacroModuleDescriptor) {
            this.registerPluginUserMacro((UserMacroModuleDescriptor)moduleDescriptor);
        }
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        ModuleDescriptor moduleDescriptor = event.getModule();
        if (moduleDescriptor instanceof MacroModuleDescriptor || moduleDescriptor instanceof UserMacroModuleDescriptor) {
            log.info("Disabling plugin macro: " + moduleDescriptor.getCompleteKey());
            this.unregisterMacro(moduleDescriptor.getName());
        }
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }

    public void setUserMacroLibrary(UserMacroLibrary userMacroLibrary) {
        this.userMacroLibrary = userMacroLibrary;
    }
}

