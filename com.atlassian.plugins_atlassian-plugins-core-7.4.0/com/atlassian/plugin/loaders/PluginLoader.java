/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  org.dom4j.Element
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import org.dom4j.Element;

public interface PluginLoader {
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory var1);

    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory var1);

    public boolean supportsAddition();

    public boolean supportsRemoval();

    public void removePlugin(Plugin var1);

    public boolean isDynamicPluginLoader();

    public ModuleDescriptor<?> createModule(Plugin var1, Element var2, ModuleDescriptorFactory var3);
}

