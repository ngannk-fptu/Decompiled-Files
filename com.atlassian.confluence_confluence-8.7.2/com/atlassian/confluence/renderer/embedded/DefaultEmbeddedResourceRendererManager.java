/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.renderer.embedded;

import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRendererManager;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;

public class DefaultEmbeddedResourceRendererManager
implements EmbeddedResourceRendererManager {
    private final PluginAccessor pluginAccessor;

    public DefaultEmbeddedResourceRendererManager(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public EmbeddedResourceRenderer getResourceRenderer(EmbeddedObject resource) {
        for (EmbeddedResourceRenderer renderer : this.getEmbeddedResourceRenderers()) {
            if (!renderer.matchesType(resource)) continue;
            return renderer;
        }
        return null;
    }

    private List<EmbeddedResourceRenderer> getEmbeddedResourceRenderers() {
        return this.pluginAccessor.getEnabledModulesByClass(EmbeddedResourceRenderer.class);
    }
}

