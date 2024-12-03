/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginRegistry$ReadOnly
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.scope.ScopeManager
 *  org.dom4j.Element
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginRegistry;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.manager.EnabledModuleCachingPluginAccessor;
import com.atlassian.plugin.manager.ForwardingPluginAccessor;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.manager.ProductPluginAccessorBase;
import com.atlassian.plugin.scope.ScopeManager;
import java.util.Set;
import org.dom4j.Element;

public class ProductPluginAccessor
extends ForwardingPluginAccessor
implements PluginAccessor {
    private static PluginController noopDisablePluginPluginController = new PluginController(){

        public void enablePlugins(String ... keys) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void disablePlugin(String key) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void disablePluginWithoutPersisting(String key) {
        }

        public void enablePluginModule(String completeKey) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void disablePluginModule(String completeKey) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Set<String> installPlugins(PluginArtifact ... pluginArtifacts) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void uninstall(Plugin plugin) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void revertRestartRequiredChange(String pluginKey) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int scanForNewPlugins() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public ModuleDescriptor<?> addDynamicModule(Plugin plugin, Element module) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void removeDynamicModule(Plugin plugin, ModuleDescriptor<?> module) {
            throw new UnsupportedOperationException("Not implemented");
        }
    };

    public ProductPluginAccessor(PluginRegistry.ReadOnly pluginRegistry, PluginPersistentStateStore store, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager) {
        super(new EnabledModuleCachingPluginAccessor(new ProductPluginAccessorBase(pluginRegistry, store, moduleDescriptorFactory, pluginEventManager), pluginEventManager, noopDisablePluginPluginController));
    }

    @Deprecated
    public ProductPluginAccessor(PluginRegistry.ReadOnly pluginRegistry, PluginPersistentStateStore store, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, ScopeManager ignored) {
        this(pluginRegistry, store, moduleDescriptorFactory, pluginEventManager);
    }
}

