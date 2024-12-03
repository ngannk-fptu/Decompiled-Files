/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptor;
import com.atlassian.plugin.impl.UnloadablePlugin;
import java.util.ArrayList;

public final class UnloadablePluginFactory {
    public static UnloadablePlugin createUnloadablePlugin(Plugin oldPlugin) {
        return UnloadablePluginFactory.createUnloadablePlugin(oldPlugin, null);
    }

    public static UnloadablePlugin createUnloadablePlugin(Plugin oldPlugin, UnloadableModuleDescriptor unloadableDescriptor) {
        UnloadablePlugin newPlugin = new UnloadablePlugin();
        newPlugin.setName(oldPlugin.getName());
        newPlugin.setKey(oldPlugin.getKey());
        newPlugin.setI18nNameKey(oldPlugin.getI18nNameKey());
        newPlugin.setUninstallable(oldPlugin.isUninstallable());
        newPlugin.setDeletable(oldPlugin.isDeleteable());
        newPlugin.setPluginsVersion(oldPlugin.getPluginsVersion());
        newPlugin.setDynamicallyLoaded(oldPlugin.isDynamicallyLoaded());
        newPlugin.setSystemPlugin(false);
        newPlugin.setPluginInformation(oldPlugin.getPluginInformation());
        ArrayList moduleDescriptors = new ArrayList(oldPlugin.getModuleDescriptors());
        for (ModuleDescriptor descriptor : moduleDescriptors) {
            if (unloadableDescriptor != null && descriptor.getKey().equals(unloadableDescriptor.getKey())) continue;
            newPlugin.addModuleDescriptor(descriptor);
        }
        if (unloadableDescriptor != null) {
            newPlugin.addModuleDescriptor(unloadableDescriptor);
        }
        return newPlugin;
    }
}

