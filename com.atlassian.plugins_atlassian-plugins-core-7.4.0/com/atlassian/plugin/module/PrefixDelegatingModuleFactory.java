/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ClassPrefixModuleFactory;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.module.PrefixModuleFactory;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixDelegatingModuleFactory
implements ModuleFactory {
    Logger log = LoggerFactory.getLogger(PrefixDelegatingModuleFactory.class);
    private final Map<String, ModuleFactory> delegateModuleFactories;

    public PrefixDelegatingModuleFactory(Set<PrefixModuleFactory> delegates) {
        HashMap<String, ModuleFactory> factories = new HashMap<String, ModuleFactory>();
        for (PrefixModuleFactory factory : delegates) {
            factories.put(factory.getPrefix(), factory);
        }
        this.delegateModuleFactories = factories;
    }

    public void addPrefixModuleFactory(PrefixModuleFactory prefixModuleFactory) {
        this.delegateModuleFactories.put(prefixModuleFactory.getPrefix(), prefixModuleFactory);
    }

    protected ModuleFactory getModuleFactoryForPrefix(ModuleReference moduleReference, ModuleDescriptor<?> moduleDescriptor) {
        Plugin plugin;
        ModuleFactory moduleFactory = this.delegateModuleFactories.get(moduleReference.prefix);
        if (moduleFactory == null && (plugin = moduleDescriptor.getPlugin()) instanceof ContainerManagedPlugin) {
            Collection containerFactories = ((ContainerManagedPlugin)plugin).getContainerAccessor().getBeansOfType(PrefixModuleFactory.class);
            for (PrefixModuleFactory prefixModuleFactory : containerFactories) {
                if (!moduleReference.prefix.equals(prefixModuleFactory.getPrefix())) continue;
                moduleFactory = prefixModuleFactory;
                break;
            }
        }
        return moduleFactory;
    }

    public <T> T createModule(String className, ModuleDescriptor<T> moduleDescriptor) {
        Object result;
        Preconditions.checkNotNull((Object)className, (Object)"The className cannot be null");
        Preconditions.checkNotNull(moduleDescriptor, (Object)"The moduleDescriptor cannot be null");
        ModuleReference moduleReference = this.getBeanReference(className);
        ModuleFactory moduleFactory = this.getModuleFactoryForPrefix(moduleReference, moduleDescriptor);
        if (moduleFactory == null) {
            throw new PluginParseException("Failed to create a module. Prefix '" + moduleReference.prefix + "' not supported");
        }
        try {
            result = moduleFactory.createModule(moduleReference.beanIdentifier, moduleDescriptor);
        }
        catch (NoClassDefFoundError error) {
            this.log.error("Detected an error (NoClassDefFoundError) instantiating the module for plugin '{}' for module '{}': {}. This error is usually caused by your plugin using a imported component class that itself relies on other packages in the product. You can probably fix this by adding the missing class's package to your <Import-Package> instructions; for more details on how to fix this, see https://developer.atlassian.com/display/DOCS/NoClassDefFoundError .", new Object[]{moduleDescriptor.getPlugin().getKey(), moduleDescriptor.getKey(), error.getMessage()});
            throw error;
        }
        catch (LinkageError error) {
            this.log.error("Detected an error (LinkageError) instantiating the module for plugin '{}' for module '{}': {}.  This error is usually caused by your plugin including copies of libraries in META-INF/lib unnecessarily. For more details on how to fix this, see https://developer.atlassian.com/x/EgAN .", new Object[]{moduleDescriptor.getPlugin().getKey(), moduleDescriptor.getKey(), error.getMessage()});
            throw error;
        }
        catch (RuntimeException ex) {
            if (ex.getClass().getSimpleName().equals("UnsatisfiedDependencyException")) {
                this.log.error("Detected an error instantiating the module via Spring. This usually means that you haven't created a <component-import> for the interface you're trying to use. https://developer.atlassian.com/x/TAEr  for more details.");
            }
            throw ex;
        }
        if (result != null) {
            return (T)result;
        }
        throw new PluginParseException("Unable to create module instance from '" + className + "'");
    }

    private ModuleReference getBeanReference(String className) {
        String prefix = "class";
        int prefixIndex = className.indexOf(58);
        if (prefixIndex != -1) {
            prefix = className.substring(0, prefixIndex);
            className = className.substring(prefixIndex + 1);
        }
        return new ModuleReference(prefix, className);
    }

    @Deprecated
    public <T> Class<T> guessModuleClass(String name, ModuleDescriptor<T> moduleDescriptor) {
        Preconditions.checkNotNull((Object)name, (Object)"The class name cannot be null");
        Preconditions.checkNotNull(moduleDescriptor, (Object)"The module descriptor cannot be null");
        ModuleReference moduleReference = this.getBeanReference(name);
        ModuleFactory moduleFactory = this.getModuleFactoryForPrefix(moduleReference, moduleDescriptor);
        Class result = null;
        if (moduleFactory instanceof ClassPrefixModuleFactory) {
            result = ((ClassPrefixModuleFactory)moduleFactory).getModuleClass(moduleReference.beanIdentifier, moduleDescriptor);
        }
        return result;
    }

    private static class ModuleReference {
        public String prefix;
        public String beanIdentifier;

        ModuleReference(String prefix, String beanIdentifier) {
            this.prefix = prefix;
            this.beanIdentifier = beanIdentifier;
        }
    }
}

