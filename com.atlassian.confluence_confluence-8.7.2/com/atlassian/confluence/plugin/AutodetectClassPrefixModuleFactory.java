/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ClassPrefixModuleFactory
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleClassNotFoundException
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ClassPrefixModuleFactory;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleClassNotFoundException;

@Deprecated
public class AutodetectClassPrefixModuleFactory
extends ClassPrefixModuleFactory {
    public AutodetectClassPrefixModuleFactory(HostContainer hostContainer) {
        super(hostContainer);
    }

    public <T> T createModule(String name, ModuleDescriptor<T> moduleDescriptor) throws PluginParseException {
        Class<T> cls = this.getModuleClass(name, moduleDescriptor);
        Plugin plugin = moduleDescriptor.getPlugin();
        if (plugin instanceof ContainerManagedPlugin) {
            return LegacySpringContainerAccessor.createBean(plugin, cls);
        }
        if (cls != null) {
            return (T)this.hostContainer.create(cls);
        }
        return null;
    }

    <T> Class<T> getModuleClass(String name, ModuleDescriptor moduleDescriptor) throws ModuleClassNotFoundException {
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

    public String getPrefix() {
        return "class";
    }
}

