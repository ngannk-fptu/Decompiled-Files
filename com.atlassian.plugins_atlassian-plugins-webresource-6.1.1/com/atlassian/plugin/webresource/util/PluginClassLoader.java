/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 */
package com.atlassian.plugin.webresource.util;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ContainerManagedPlugin;

public class PluginClassLoader {
    public static <T> T create(Plugin plugin, Class<?> callingClass, HostContainer hostContainer, String className) throws ClassNotFoundException {
        Class clazz = plugin.loadClass(className, callingClass);
        if (plugin instanceof ContainerManagedPlugin) {
            return (T)((ContainerManagedPlugin)plugin).getContainerAccessor().createBean(clazz);
        }
        return (T)hostContainer.create(clazz);
    }
}

