/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.legacy.BatchPluginResource;
import com.atlassian.plugin.webresource.legacy.PluginResource;
import com.atlassian.plugin.webresource.legacy.PluginResourceLocator;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginResourceLocatorImpl
implements PluginResourceLocator {
    private static final Logger log = LoggerFactory.getLogger(PluginResourceLocatorImpl.class);
    private final PluginAccessor pluginAccessor;

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration) {
        this.pluginAccessor = webResourceIntegration.getPluginAccessor();
    }

    @Override
    public List<PluginResource> getPluginResources(String moduleCompleteKey) {
        if (moduleCompleteKey.contains(":")) {
            Option<WebResourceModuleDescriptor> option = this.getDescriptor(moduleCompleteKey);
            if (option.isEmpty()) {
                return Collections.emptyList();
            }
            WebResourceModuleDescriptor wrmd = (WebResourceModuleDescriptor)((Object)option.get());
            LinkedHashSet<BatchPluginResource> resources = new LinkedHashSet<BatchPluginResource>();
            for (ResourceDescriptor resourceDescriptor : wrmd.getResourceDescriptors()) {
                resources.add(new BatchPluginResource(moduleCompleteKey, wrmd.getCompleteKey()));
            }
            return ImmutableList.copyOf(resources);
        }
        ArrayList<PluginResource> resources = new ArrayList<PluginResource>();
        resources.add(new BatchPluginResource(moduleCompleteKey, moduleCompleteKey));
        return resources;
    }

    private Option<WebResourceModuleDescriptor> getDescriptor(String moduleCompleteKey) {
        ModuleDescriptor moduleDescriptor = this.pluginAccessor.getEnabledPluginModule(moduleCompleteKey);
        if (moduleDescriptor == null || !(moduleDescriptor instanceof WebResourceModuleDescriptor)) {
            log.error("Error loading resource \"{}\". Resource is not a Web Resource Module", (Object)moduleCompleteKey);
            return Option.none();
        }
        return Option.some((Object)((Object)((WebResourceModuleDescriptor)moduleDescriptor)));
    }
}

