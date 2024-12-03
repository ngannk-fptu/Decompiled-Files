/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInternal;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.loaders.ForwardingPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PermissionCheckingPluginLoader
extends ForwardingPluginLoader {
    private static final Logger logger = LoggerFactory.getLogger(PermissionCheckingPluginLoader.class);

    public PermissionCheckingPluginLoader(PluginLoader delegate) {
        super(delegate);
    }

    @Override
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        return ImmutableList.copyOf((Iterable)Iterables.transform(this.delegate().loadAllPlugins(moduleDescriptorFactory), (Function)new CheckPluginPermissionFunction()));
    }

    @Override
    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        return ImmutableList.copyOf((Iterable)Iterables.transform(this.delegate().loadFoundPlugins(moduleDescriptorFactory), (Function)new CheckPluginPermissionFunction()));
    }

    @Override
    public void removePlugin(Plugin plugin) {
        if (!(plugin instanceof UnloadablePlugin)) {
            super.removePlugin(plugin);
        } else {
            logger.debug("Detected an unloadable plugin '{}', so skipping removal", (Object)plugin.getKey());
        }
    }

    @Override
    public void discardPlugin(Plugin plugin) {
        if (!(plugin instanceof UnloadablePlugin)) {
            super.discardPlugin(plugin);
        } else {
            logger.debug("Detected an unloadable plugin '{}', so skipping discard", (Object)plugin.getKey());
        }
    }

    private final class CheckPluginPermissionFunction
    implements Function<Plugin, Plugin> {
        private CheckPluginPermissionFunction() {
        }

        public Plugin apply(Plugin p) {
            if (p.hasAllPermissions()) {
                return p;
            }
            if (p instanceof PluginInternal) {
                return this.checkPlugin((PluginInternal)p);
            }
            return p;
        }

        private Plugin checkPlugin(PluginInternal p) {
            PluginArtifact pluginArtifact = p.getPluginArtifact();
            if (pluginArtifact != null) {
                if (pluginArtifact.containsJavaExecutableCode() && !p.getActivePermissions().contains("execute_java")) {
                    UnloadablePlugin unloadablePlugin = new UnloadablePlugin("Plugin doesn't require 'execute_java' permission yet references some java executable code. This could be either embedded java classes, embedded java libraries, spring context files or bundle activator.");
                    unloadablePlugin.setKey(p.getKey());
                    unloadablePlugin.setName(p.getName());
                    logger.warn("Plugin '{}' only requires permission {} which doesn't include '{}', yet has some java code (classes, libs, spring context, etc), making it un-loadable.", new Object[]{p.getKey(), p.getActivePermissions(), "execute_java"});
                    PermissionCheckingPluginLoader.this.discardPlugin(p);
                    return unloadablePlugin;
                }
                if (this.hasSystemModules(p) && !p.getActivePermissions().contains("create_system_modules")) {
                    UnloadablePlugin unloadablePlugin = new UnloadablePlugin("Plugin doesn't require 'create_system_modules' permission yet declared modules use the 'system' attribute. ");
                    unloadablePlugin.setKey(p.getKey());
                    unloadablePlugin.setName(p.getName());
                    logger.warn("Plugin '{}' only requires permission {} which doesn't include '{}', yet has system modules , making it un-loadable.", new Object[]{p.getKey(), p.getActivePermissions(), "create_system_modules"});
                    PermissionCheckingPluginLoader.this.discardPlugin(p);
                    return unloadablePlugin;
                }
            }
            return p;
        }

        private boolean hasSystemModules(Plugin plugin) {
            for (ModuleDescriptor descriptor : plugin.getModuleDescriptors()) {
                if (!descriptor.isSystemModule()) continue;
                return true;
            }
            return false;
        }
    }
}

