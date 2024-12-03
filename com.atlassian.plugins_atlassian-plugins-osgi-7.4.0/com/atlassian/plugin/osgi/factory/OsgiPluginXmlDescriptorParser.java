/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.parsers.XmlDescriptorParser
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.parsers.XmlDescriptorParser;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.util.Set;
import org.dom4j.Element;

public class OsgiPluginXmlDescriptorParser
extends XmlDescriptorParser {
    public OsgiPluginXmlDescriptorParser(InputStream source, Set<Application> applications) {
        super((InputStream)Preconditions.checkNotNull((Object)source, (Object)"The descriptor source must not be null"), applications);
    }

    public OsgiPluginXmlDescriptorParser(InputStream source, Iterable<InputStream> supplementalSources, Set<Application> applications) {
        super(source, supplementalSources, applications);
    }

    protected ModuleDescriptor<?> createModuleDescriptor(Plugin plugin, Element element, ModuleDescriptorFactory moduleDescriptorFactory) {
        ModuleDescriptor descriptor = super.createModuleDescriptor(plugin, element, moduleDescriptorFactory);
        this.passModuleDescriptorToPlugin(plugin, element, descriptor);
        return descriptor;
    }

    public ModuleDescriptor<?> addModule(ModuleDescriptorFactory moduleDescriptorFactory, Plugin plugin, Element module) {
        ModuleDescriptor descriptor = super.addModule(moduleDescriptorFactory, plugin, module);
        this.passModuleDescriptorToPlugin(plugin, module, descriptor);
        return descriptor;
    }

    private void passModuleDescriptorToPlugin(Plugin plugin, Element element, ModuleDescriptor<?> descriptor) {
        if (plugin instanceof OsgiPlugin) {
            String key = descriptor == null ? element.attributeValue("key") : descriptor.getKey();
            ((OsgiPlugin)plugin).addModuleDescriptorElement(key, element);
        }
    }
}

