/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.web.renderer.WebPanelRenderer
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.templaterenderer.velocity;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractCachingWebPanelRenderer
implements WebPanelRenderer,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(AbstractCachingWebPanelRenderer.class);
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Caches TemplateRenderer instances by plugin key")
    private final ConcurrentMap<String, TemplateRenderer> rendererCache = new ConcurrentHashMap<String, TemplateRenderer>();
    private final PluginEventManager pluginEventManager;

    protected AbstractCachingWebPanelRenderer(PluginEventManager pluginEventManager) {
        this.pluginEventManager = pluginEventManager;
    }

    public String getResourceType() {
        return "velocity";
    }

    private TemplateRenderer getRenderer(Plugin plugin) {
        TemplateRenderer templateRenderer = (TemplateRenderer)this.rendererCache.get(plugin.getKey());
        if (templateRenderer == null) {
            templateRenderer = this.createRenderer(plugin);
            TemplateRenderer cachedRenderer = this.rendererCache.putIfAbsent(plugin.getKey(), templateRenderer);
            templateRenderer = cachedRenderer == null ? templateRenderer : cachedRenderer;
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

    public void destroy() {
        log.debug("destroy()");
        this.pluginEventManager.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        log.debug("afterPropertiesSet()");
        this.pluginEventManager.register((Object)this);
    }

    @PluginEventListener
    public void pluginUnloaded(PluginDisabledEvent disabledEvent) {
        this.rendererCache.remove(disabledEvent.getPlugin().getKey());
    }
}

