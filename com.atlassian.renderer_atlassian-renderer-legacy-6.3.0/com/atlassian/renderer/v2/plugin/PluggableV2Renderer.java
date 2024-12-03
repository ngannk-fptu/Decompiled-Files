/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.renderer.v2.plugin;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.Renderer;
import com.atlassian.renderer.v2.V2Renderer;
import com.atlassian.renderer.v2.plugin.RendererComponentsAccessor;

public class PluggableV2Renderer
implements Renderer {
    private final RendererComponentsAccessor rendererComponentsAccessor;

    public PluggableV2Renderer(PluginAccessor pluginAccessor) {
        this.rendererComponentsAccessor = new RendererComponentsAccessor(pluginAccessor);
    }

    @Override
    public String render(String originalContent, RenderContext renderContext) {
        return this.getRenderer().render(originalContent, renderContext);
    }

    @Override
    public String renderAsText(String originalContent, RenderContext context) {
        return this.getRenderer().renderAsText(originalContent, context);
    }

    @Override
    public String getRendererType() {
        return "atlassian-wiki-renderer";
    }

    private V2Renderer getRenderer() {
        return new V2Renderer(this.rendererComponentsAccessor.getActiveRendererComponents());
    }
}

