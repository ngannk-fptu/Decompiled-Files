/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  org.dom4j.Element
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptorFactory;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import org.dom4j.Element;

public final class UnrecognisedModuleDescriptorFactory {
    public static UnrecognisedModuleDescriptor createUnrecognisedModuleDescriptor(Plugin plugin, Element element, Throwable e, ModuleDescriptorFactory moduleDescriptorFactory) {
        return UnloadableModuleDescriptorFactory.initNoOpModuleDescriptor(new UnrecognisedModuleDescriptor(), plugin, element, e, moduleDescriptorFactory);
    }
}

