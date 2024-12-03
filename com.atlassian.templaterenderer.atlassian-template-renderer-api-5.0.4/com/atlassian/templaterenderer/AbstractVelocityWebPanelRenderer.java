/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.renderer.WebPanelRenderer
 */
package com.atlassian.templaterenderer;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Deprecated
public abstract class AbstractVelocityWebPanelRenderer
implements WebPanelRenderer {
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Caches TemplateRenderer instances by plugin key, the class is deprecated and replaced by AbstractCachingWebPanelRenderer")
    private final Map<String, TemplateRenderer> rendererCache = this.createCacheMap();

    public String getResourceType() {
        return "velocity";
    }

    private TemplateRenderer getRenderer(Plugin plugin) {
        TemplateRenderer templateRenderer = this.rendererCache.get(plugin.getKey());
        if (templateRenderer == null) {
            templateRenderer = this.createRenderer(plugin);
            this.rendererCache.put(plugin.getKey(), templateRenderer);
        }
        return templateRenderer;
    }

    public void render(String s, Plugin plugin, Map<String, Object> stringObjectMap, Writer writer) throws IOException {
        this.getRenderer(plugin).render(s, stringObjectMap, writer);
    }

    public String renderFragment(String fragment, Plugin plugin, Map<String, Object> stringObjectMap) {
        return this.getRenderer(plugin).renderFragment(fragment, stringObjectMap);
    }

    public void renderFragment(Writer writer, String fragment, Plugin plugin, Map<String, Object> stringObjectMap) throws IOException {
        writer.write(this.getRenderer(plugin).renderFragment(fragment, stringObjectMap));
    }

    protected abstract TemplateRenderer createRenderer(Plugin var1);

    protected Map<String, TemplateRenderer> createCacheMap() {
        return Collections.synchronizedMap(new WeakHashMap());
    }
}

