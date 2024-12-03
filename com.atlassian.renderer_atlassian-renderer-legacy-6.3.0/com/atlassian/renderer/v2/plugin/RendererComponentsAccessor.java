/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptor
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator
 */
package com.atlassian.renderer.v2.plugin;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator;
import com.atlassian.renderer.v2.components.TextConverter;
import com.atlassian.renderer.v2.plugin.RendererComponentModuleDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class RendererComponentsAccessor {
    private static final Comparator<WeightedDescriptor> COMPARATOR = new WeightedDescriptorComparator();
    private final PluginAccessor pluginAccessor;

    RendererComponentsAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public List getActiveRendererComponents() {
        List<RendererComponentModuleDescriptor> moduleDescriptors = this.getEnabledRendererComponentModuleDescriptors();
        ArrayList<Object> components = new ArrayList<Object>(moduleDescriptors.size());
        for (RendererComponentModuleDescriptor descriptor : moduleDescriptors) {
            components.add(descriptor.getModule());
        }
        return components;
    }

    public List<TextConverter> getActiveTextConverterComponents() {
        List<RendererComponentModuleDescriptor> moduleDescriptors = this.getEnabledRendererComponentModuleDescriptors();
        ArrayList<TextConverter> converters = new ArrayList<TextConverter>(moduleDescriptors.size());
        for (RendererComponentModuleDescriptor descriptor : moduleDescriptors) {
            Object module = descriptor.getModule();
            if (!(module instanceof TextConverter)) continue;
            converters.add((TextConverter)module);
        }
        return converters;
    }

    private List<RendererComponentModuleDescriptor> getEnabledRendererComponentModuleDescriptors() {
        ArrayList<RendererComponentModuleDescriptor> modules = new ArrayList<RendererComponentModuleDescriptor>(this.pluginAccessor.getEnabledModuleDescriptorsByClass(RendererComponentModuleDescriptor.class));
        Collections.sort(modules, COMPARATOR);
        return modules;
    }
}

