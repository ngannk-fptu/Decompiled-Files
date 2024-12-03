/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.plugin.module;

import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.spring.container.ContainerManager;

public class DefaultPluginModuleFactory<T>
implements PluginModuleFactory<T> {
    private final ModuleDescriptor<? extends T> moduleDescriptor;

    public DefaultPluginModuleFactory(ModuleDescriptor<? extends T> moduleDescriptor) {
        this.moduleDescriptor = moduleDescriptor;
    }

    @Override
    public T createModule() {
        Plugin plugin = this.moduleDescriptor.getPlugin();
        if (plugin instanceof ContainerManagedPlugin) {
            return LegacySpringContainerAccessor.createBean(plugin, this.moduleDescriptor.getModuleClass());
        }
        return (T)ContainerManager.getInstance().getContainerContext().createCompleteComponent(this.moduleDescriptor.getModuleClass());
    }
}

