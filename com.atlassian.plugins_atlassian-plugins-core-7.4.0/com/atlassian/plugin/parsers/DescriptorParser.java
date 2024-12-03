/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  org.dom4j.Element
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import org.dom4j.Element;

public interface DescriptorParser {
    public Plugin configurePlugin(ModuleDescriptorFactory var1, Plugin var2);

    public String getKey();

    @Deprecated
    public boolean isSystemPlugin();

    public int getPluginsVersion();

    public PluginInformation getPluginInformation();

    public ModuleDescriptor<?> addModule(ModuleDescriptorFactory var1, Plugin var2, Element var3);
}

