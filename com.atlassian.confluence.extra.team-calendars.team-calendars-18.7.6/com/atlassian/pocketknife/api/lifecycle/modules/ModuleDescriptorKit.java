/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.dom4j.Element
 */
package com.atlassian.pocketknife.api.lifecycle.modules;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.pocketknife.api.lifecycle.modules.ModuleRegistrationHandle;
import com.atlassian.pocketknife.internal.lifecycle.modules.GhettoCode;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Map;
import org.dom4j.Element;

public class ModuleDescriptorKit {
    @Internal
    public static final String DYNAMIC_MODULE_ATTRNAME = "com.atlassian.pocketknife.internal.lifecycle.modules.DynamicModule";

    public static Iterable<ModuleDescriptor<?>> getStaticModules(Plugin plugin) {
        Map<String, Element> moduleElements = GhettoCode.getModuleElements(plugin);
        Collection moduleDescriptors = plugin.getModuleDescriptors();
        return Lists.newArrayList((Iterable)Iterables.filter((Iterable)moduleDescriptors, (Predicate)Predicates.not(ModuleDescriptorKit.isDynamicModule(moduleElements))));
    }

    public static Iterable<ModuleDescriptor<?>> getDynamicModules(Plugin plugin) {
        Map<String, Element> moduleElements = GhettoCode.getModuleElements(plugin);
        Collection moduleDescriptors = plugin.getModuleDescriptors();
        return Lists.newArrayList((Iterable)Iterables.filter((Iterable)moduleDescriptors, ModuleDescriptorKit.isDynamicModule(moduleElements)));
    }

    public static Iterable<ModuleDescriptor<?>> getDynamicModulesNotInHandle(Plugin plugin, ModuleRegistrationHandle handle) {
        Iterable<ModuleDescriptor<?>> dynamicModules = ModuleDescriptorKit.getDynamicModules(plugin);
        return Lists.newArrayList((Iterable)Iterables.filter(dynamicModules, (Predicate)Predicates.not(ModuleDescriptorKit.isModuleInHandle(handle))));
    }

    private static Predicate<ModuleDescriptor<?>> isModuleInHandle(ModuleRegistrationHandle handle) {
        final Iterable<ModuleCompleteKey> handleModules = handle.getModules();
        return new Predicate<ModuleDescriptor<?>>(){

            public boolean apply(final ModuleDescriptor<?> moduleDescriptor) {
                ModuleCompleteKey moduleCompleteKey = (ModuleCompleteKey)Iterables.find((Iterable)handleModules, (Predicate)new Predicate<ModuleCompleteKey>(){

                    public boolean apply(ModuleCompleteKey input) {
                        return input.getCompleteKey().equals(moduleDescriptor.getCompleteKey());
                    }
                }, null);
                return moduleCompleteKey != null;
            }
        };
    }

    private static Predicate<ModuleDescriptor<?>> isDynamicModule(final Map<String, Element> moduleElements) {
        return new Predicate<ModuleDescriptor<?>>(){

            public boolean apply(ModuleDescriptor<?> input) {
                Element element = (Element)moduleElements.get(input.getKey());
                return element != null && "true".equals(element.attributeValue(ModuleDescriptorKit.DYNAMIC_MODULE_ATTRNAME));
            }
        };
    }
}

