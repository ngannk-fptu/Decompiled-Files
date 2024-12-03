/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.descriptors.WebPanelRendererModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.plugin.web.renderer.StaticWebPanelRenderer;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractWebPanel
implements WebPanel {
    private final PluginAccessor pluginAccessor;
    protected Plugin plugin;
    private String resourceType;

    protected AbstractWebPanel(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = (String)Preconditions.checkNotNull((Object)resourceType);
    }

    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        writer.write(this.getHtml(context));
    }

    protected final WebPanelRenderer getRenderer() {
        if ("static".equals(this.resourceType)) {
            return StaticWebPanelRenderer.RENDERER;
        }
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebPanelRendererModuleDescriptor.class).stream().map(ModuleDescriptor::getModule).filter(webPanelRenderer -> Objects.equals(webPanelRenderer.getResourceType(), this.resourceType)).findFirst().orElseThrow(() -> new RendererException("No renderer found for resource type: " + this.resourceType));
    }
}

