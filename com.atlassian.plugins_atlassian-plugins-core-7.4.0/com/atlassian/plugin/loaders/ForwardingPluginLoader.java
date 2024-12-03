/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ForwardingObject
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.loaders.DiscardablePluginLoader;
import com.atlassian.plugin.loaders.DynamicPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingObject;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwardingPluginLoader
extends ForwardingObject
implements DynamicPluginLoader,
DiscardablePluginLoader {
    private static final Logger log = LoggerFactory.getLogger(ForwardingPluginLoader.class);
    private final PluginLoader delegate;

    public ForwardingPluginLoader(PluginLoader delegate) {
        this.delegate = (PluginLoader)Preconditions.checkNotNull((Object)delegate);
    }

    protected final PluginLoader delegate() {
        return this.delegate;
    }

    @Override
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        return this.delegate().loadAllPlugins(moduleDescriptorFactory);
    }

    @Override
    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        return this.delegate().loadFoundPlugins(moduleDescriptorFactory);
    }

    @Override
    public boolean supportsAddition() {
        return this.delegate().supportsAddition();
    }

    @Override
    public boolean supportsRemoval() {
        return this.delegate().supportsRemoval();
    }

    @Override
    public void removePlugin(Plugin plugin) {
        this.delegate().removePlugin(plugin);
    }

    @Override
    public boolean isDynamicPluginLoader() {
        return this.delegate().isDynamicPluginLoader();
    }

    @Override
    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        return this.delegate().createModule(plugin, module, moduleDescriptorFactory);
    }

    @Override
    public String canLoad(PluginArtifact pluginArtifact) {
        if (this.isDynamicPluginLoader()) {
            return ((DynamicPluginLoader)this.delegate()).canLoad(pluginArtifact);
        }
        throw new IllegalStateException("Should not call on non-dynamic plugin loader");
    }

    @Override
    public void discardPlugin(Plugin plugin) {
        PluginLoader delegate = this.delegate();
        if (delegate instanceof DiscardablePluginLoader) {
            ((DiscardablePluginLoader)delegate).discardPlugin(plugin);
        } else {
            log.debug("Ignoring discardPlugin({}, version {}) as delegate is not a DiscardablePluginLoader", (Object)plugin.getKey(), (Object)plugin.getPluginInformation().getVersion());
        }
    }
}

