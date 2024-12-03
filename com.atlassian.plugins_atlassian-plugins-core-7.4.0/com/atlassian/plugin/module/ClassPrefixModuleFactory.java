/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleClassNotFoundException
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleClassNotFoundException;
import com.atlassian.plugin.module.PrefixModuleFactory;
import com.google.common.base.Preconditions;

public class ClassPrefixModuleFactory
implements PrefixModuleFactory {
    protected final HostContainer hostContainer;

    public ClassPrefixModuleFactory(HostContainer hostContainer) {
        this.hostContainer = hostContainer;
    }

    public <T> T createModule(String name, ModuleDescriptor<T> moduleDescriptor) {
        Class<T> cls = this.getModuleClass(name, moduleDescriptor);
        Plugin plugin = moduleDescriptor.getPlugin();
        if (plugin instanceof ContainerManagedPlugin) {
            ContainerManagedPlugin cmPlugin = (ContainerManagedPlugin)plugin;
            ContainerAccessor containerAccessor = (ContainerAccessor)Preconditions.checkNotNull((Object)cmPlugin.getContainerAccessor(), (String)"Plugin container accessor is null. Plugin: %s. Module name: %s.", (Object)cmPlugin, (Object)name);
            return (T)containerAccessor.createBean(cls);
        }
        if (cls != null) {
            return (T)this.hostContainer.create(cls);
        }
        return null;
    }

    <T> Class<T> getModuleClass(String name, ModuleDescriptor moduleDescriptor) {
        try {
            return moduleDescriptor.getPlugin().loadClass(name, null);
        }
        catch (ClassNotFoundException e) {
            throw new ModuleClassNotFoundException(name, moduleDescriptor.getPluginKey(), moduleDescriptor.getKey(), (Exception)e, this.createErrorMsg(name));
        }
    }

    private String createErrorMsg(String className) {
        return "Couldn't load the class '" + className + "'. This could mean that you misspelled the name of the class (double check) or that you're using a class in your plugin that you haven't provided bundle instructions for. See https://developer.atlassian.com/x/mQAN for more details on how to fix this.";
    }

    @Override
    public String getPrefix() {
        return "class";
    }
}

