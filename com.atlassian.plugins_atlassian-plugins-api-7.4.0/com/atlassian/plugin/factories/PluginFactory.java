/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.plugin.factories;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import org.dom4j.Element;

public interface PluginFactory {
    public String canCreate(PluginArtifact var1);

    public Plugin create(PluginArtifact var1, ModuleDescriptorFactory var2);

    public ModuleDescriptor<?> createModule(Plugin var1, Element var2, ModuleDescriptorFactory var3);
}

