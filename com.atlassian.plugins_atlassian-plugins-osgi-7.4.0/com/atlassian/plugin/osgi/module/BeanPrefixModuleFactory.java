/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.PrefixModuleFactory
 */
package com.atlassian.plugin.osgi.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.PrefixModuleFactory;

public class BeanPrefixModuleFactory
implements PrefixModuleFactory {
    public <T> T createModule(String name, ModuleDescriptor<T> moduleDescriptor) {
        if (moduleDescriptor.getPlugin() instanceof ContainerManagedPlugin) {
            ContainerManagedPlugin containerManagedPlugin = (ContainerManagedPlugin)moduleDescriptor.getPlugin();
            return (T)containerManagedPlugin.getContainerAccessor().getBean(name);
        }
        throw new IllegalArgumentException("Failed to resolve '" + name + "'. You cannot use 'bean' prefix with non spring plugins");
    }

    public String getPrefix() {
        return "bean";
    }
}

