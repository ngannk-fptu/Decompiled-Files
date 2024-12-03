/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.BatchedRenderRequest;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.view.BatchedRenderResult;
import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.descriptor.DeviceTypeRendererComponentModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceTypeAwareRenderer
implements Renderer {
    private final Renderer defaultRenderer;
    private final Map<String, Renderer> renderers = new ConcurrentHashMap<String, Renderer>(1, 0.75f, 3);

    public DeviceTypeAwareRenderer(Renderer defaultRenderer, PluginEventManager pluginEventManager) {
        this.defaultRenderer = defaultRenderer;
        pluginEventManager.register((Object)this);
    }

    @Override
    public String render(ContentEntityObject content) {
        return this.defaultRenderer.render(content);
    }

    @Override
    public String render(ContentEntityObject content, ConversionContext conversionContext) {
        return this.getRenderer(conversionContext.getOutputDeviceType()).render(content, conversionContext);
    }

    @Override
    public String render(String xml, ConversionContext conversionContext) {
        return this.getRenderer(conversionContext.getOutputDeviceType()).render(xml, conversionContext);
    }

    @Override
    public RenderResult renderWithResult(String xml, ConversionContext conversionContext) {
        return this.getRenderer(conversionContext.getOutputDeviceType()).renderWithResult(xml, conversionContext);
    }

    @Override
    public List<BatchedRenderResult> render(BatchedRenderRequest ... renderRequests) {
        ArrayList<BatchedRenderResult> renders = new ArrayList<BatchedRenderResult>();
        for (BatchedRenderRequest renderRequest : renderRequests) {
            Renderer renderer = this.getRenderer(renderRequest.getContext().getOutputDeviceType());
            renders.addAll(renderer.render(renderRequest));
        }
        return renders;
    }

    private Renderer getRenderer(String deviceType) {
        return this.renderers.getOrDefault(deviceType, this.defaultRenderer);
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (event.getModule() instanceof DeviceTypeRendererComponentModuleDescriptor) {
            DeviceTypeRendererComponentModuleDescriptor descriptor = (DeviceTypeRendererComponentModuleDescriptor)event.getModule();
            this.registerRenderer(descriptor.getDeviceTypes(), descriptor.getModule());
        }
    }

    private void registerRenderer(Set<String> deviceTypes, Renderer renderer) {
        Sets.SetView intersection = Sets.intersection(this.renderers.keySet(), deviceTypes);
        if (!intersection.isEmpty()) {
            throw new IllegalStateException("Cannot register new renderer of class " + renderer.getClass().getName() + " for device types " + deviceTypes + " due to there already being a renderer registered for one or more of these types: " + (Set)intersection);
        }
        deviceTypes.forEach(deviceType -> this.renderers.put((String)deviceType, renderer));
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        ModuleDescriptor moduleDescriptor = event.getModule();
        if (moduleDescriptor instanceof DeviceTypeRendererComponentModuleDescriptor) {
            ((DeviceTypeRendererComponentModuleDescriptor)moduleDescriptor).getDeviceTypes().forEach(this.renderers::remove);
        }
    }
}

