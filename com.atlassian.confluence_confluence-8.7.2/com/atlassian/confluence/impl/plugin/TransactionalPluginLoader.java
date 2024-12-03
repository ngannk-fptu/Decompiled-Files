/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.loaders.DiscardablePluginLoader
 *  com.atlassian.plugin.loaders.DynamicPluginLoader
 *  org.dom4j.Element
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.loaders.DiscardablePluginLoader;
import com.atlassian.plugin.loaders.DynamicPluginLoader;
import java.util.Objects;
import org.dom4j.Element;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor={PluginException.class})
public class TransactionalPluginLoader
implements DynamicPluginLoader,
DiscardablePluginLoader {
    private final DynamicPluginLoader delegateLoader;
    private final DiscardablePluginLoader delegateDiscardableLoader;

    public TransactionalPluginLoader(DynamicPluginLoader delegateLoader, DiscardablePluginLoader delegateDiscardableLoader) {
        this.delegateLoader = Objects.requireNonNull(delegateLoader);
        this.delegateDiscardableLoader = Objects.requireNonNull(delegateDiscardableLoader);
    }

    public String canLoad(PluginArtifact pluginArtifact) {
        return this.delegateLoader.canLoad(pluginArtifact);
    }

    @Transactional(readOnly=true)
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        return this.delegateLoader.loadAllPlugins(moduleDescriptorFactory);
    }

    @Transactional(readOnly=true)
    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        return this.delegateLoader.loadFoundPlugins(moduleDescriptorFactory);
    }

    @Transactional(readOnly=true)
    public boolean supportsAddition() {
        return this.delegateLoader.supportsAddition();
    }

    @Transactional(readOnly=true)
    public boolean supportsRemoval() {
        return this.delegateLoader.supportsRemoval();
    }

    public void removePlugin(Plugin plugin) {
        this.delegateLoader.removePlugin(plugin);
    }

    @Transactional(readOnly=true)
    public boolean isDynamicPluginLoader() {
        return this.delegateLoader.isDynamicPluginLoader();
    }

    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        return this.delegateLoader.createModule(plugin, module, moduleDescriptorFactory);
    }

    public void discardPlugin(Plugin plugin) {
        this.delegateDiscardableLoader.discardPlugin(plugin);
    }
}

