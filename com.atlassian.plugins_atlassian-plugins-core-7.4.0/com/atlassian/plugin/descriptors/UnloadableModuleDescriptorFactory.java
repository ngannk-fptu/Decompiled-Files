/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  org.dom4j.Element
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractNoOpModuleDescriptor;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptor;
import org.dom4j.Element;

public final class UnloadableModuleDescriptorFactory {
    public static UnloadableModuleDescriptor createUnloadableModuleDescriptor(Plugin plugin, Element element, Throwable e, ModuleDescriptorFactory moduleDescriptorFactory) {
        return UnloadableModuleDescriptorFactory.initNoOpModuleDescriptor(new UnloadableModuleDescriptor(), plugin, element, e, moduleDescriptorFactory);
    }

    public static <T extends AbstractNoOpModuleDescriptor> T initNoOpModuleDescriptor(T descriptor, Plugin plugin, Element element, Throwable e, ModuleDescriptorFactory moduleDescriptorFactory) {
        descriptor.init(plugin, element);
        String name = element.getName();
        Class descriptorClass = moduleDescriptorFactory.getModuleDescriptorClass(name);
        String descriptorClassName = descriptorClass == null ? descriptor.getKey() : descriptorClass.getName();
        String errorMsg = UnloadableModuleDescriptorFactory.constructErrorMessage(name, descriptorClassName, e);
        descriptor.setErrorText(errorMsg);
        return descriptor;
    }

    public static UnloadableModuleDescriptor createUnloadableModuleDescriptor(Plugin plugin, ModuleDescriptor<?> descriptor, Throwable e) {
        UnloadableModuleDescriptor unloadableDescriptor = new UnloadableModuleDescriptor();
        unloadableDescriptor.setName(descriptor.getName());
        unloadableDescriptor.setKey(descriptor.getKey());
        unloadableDescriptor.setPlugin(plugin);
        String errorMsg = UnloadableModuleDescriptorFactory.constructErrorMessage(descriptor.getDisplayName(), descriptor.getModuleClass() == null ? descriptor.getName() : descriptor.getModuleClass().getName(), e);
        unloadableDescriptor.setErrorText(errorMsg);
        return unloadableDescriptor;
    }

    private static String constructErrorMessage(String moduleName, String moduleClass, Throwable e) {
        String errorMsg = e instanceof PluginParseException ? "There was a problem loading the descriptor for module '" + moduleName + "." : (e instanceof InstantiationException ? "Could not instantiate module descriptor: " + moduleClass + "." : (e instanceof IllegalAccessException ? "Exception instantiating module descriptor: " + moduleClass + "." : (e instanceof ClassNotFoundException ? "Could not find module descriptor class: " + moduleClass + "." : (e instanceof NoClassDefFoundError ? "A required class was missing: " + moduleClass + ". Please check that you have all of the required dependencies." : "There was a problem loading the module descriptor: " + moduleClass + "."))));
        return errorMsg + " " + e.getMessage();
    }
}

