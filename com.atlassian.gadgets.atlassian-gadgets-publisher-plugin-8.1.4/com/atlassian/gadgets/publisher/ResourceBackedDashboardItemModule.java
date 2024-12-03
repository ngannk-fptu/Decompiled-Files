/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.DashboardItemModule$DirectoryDefinition
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.descriptors.WebPanelRendererModuleDescriptor
 *  com.atlassian.plugin.web.renderer.RendererException
 *  com.atlassian.plugin.web.renderer.StaticWebPanelRenderer
 *  com.atlassian.plugin.web.renderer.WebPanelRenderer
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.gadgets.publisher;

import com.atlassian.gadgets.plugins.DashboardItemModule;
import com.atlassian.gadgets.publisher.AbstractDashboardItemModule;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.descriptors.WebPanelRendererModuleDescriptor;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.plugin.web.renderer.StaticWebPanelRenderer;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResourceBackedDashboardItemModule
extends AbstractDashboardItemModule {
    private final PluginAccessor pluginAccessor;
    private final ContextProvider contextProvider;
    private final String resourceType;
    private final String templatePath;
    private final Plugin currentPlugin;

    public ResourceBackedDashboardItemModule(PluginAccessor pluginAccessor, ResourceDescriptor resourceDescriptor, Plugin currentPlugin, ContextProvider contextProvider, Option<String> amdModule, boolean configurable, Option<DashboardItemModule.DirectoryDefinition> description, Condition condition, Option<String> webResourceKey) {
        super(description, amdModule, configurable, condition, webResourceKey);
        this.pluginAccessor = pluginAccessor;
        this.contextProvider = contextProvider;
        this.resourceType = resourceDescriptor.getType();
        this.templatePath = resourceDescriptor.getLocation();
        this.currentPlugin = currentPlugin;
    }

    public void renderContent(Writer writer, Map<String, Object> context) {
        try {
            this.getRenderer().render(this.templatePath, this.currentPlugin, this.contextProvider.getContextMap(context), writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private WebPanelRenderer getRenderer() {
        if ("static".equals(this.resourceType)) {
            return StaticWebPanelRenderer.RENDERER;
        }
        try {
            return ((WebPanelRendererModuleDescriptor)Iterables.find((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebPanelRendererModuleDescriptor.class), (Predicate)new Predicate<WebPanelRendererModuleDescriptor>(){

                public boolean apply(WebPanelRendererModuleDescriptor descriptor) {
                    WebPanelRenderer renderer = descriptor.getModule();
                    return ((String)Preconditions.checkNotNull((Object)ResourceBackedDashboardItemModule.this.resourceType)).equals(renderer.getResourceType());
                }
            })).getModule();
        }
        catch (NoSuchElementException e) {
            throw new RendererException("No renderer found for resource type: " + this.resourceType, (Throwable)e);
        }
    }
}

