/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.collections.CompositeMap
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.renderer.RendererException
 *  com.atlassian.plugin.web.renderer.WebPanelRenderer
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 */
package com.atlassian.confluence.plugin.web.renderer;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.collections.CompositeMap;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class ConfluenceAwareWebPanelRenderer
implements WebPanelRenderer {
    public String getResourceType() {
        return "confluence-velocity";
    }

    public void render(String templateName, Plugin plugin, Map<String, Object> context, Writer writer) throws RendererException, IOException {
        Map<String, Object> defaultContext = MacroUtils.defaultVelocityContext();
        Map map = CompositeMap.of(context, defaultContext);
        try (Ticker ignored = Metrics.metric((String)"webTemplateRenderer").fromPluginKey(plugin.getKey()).tag("templateRenderer", "confluence").tag("templateName", templateName).withAnalytics().startTimer();){
            VelocityUtils.writeRenderedTemplate(writer, templateName, map);
        }
    }

    public String renderFragment(String fragment, Plugin plugin, Map<String, Object> context) throws RendererException {
        Map<String, Object> defaultContext = MacroUtils.defaultVelocityContext();
        Map map = CompositeMap.of(context, defaultContext);
        return VelocityUtils.getRenderedContent(fragment, map);
    }

    public void renderFragment(Writer writer, String fragment, Plugin plugin, Map<String, Object> context) throws RendererException, IOException {
        Map<String, Object> defaultContext = MacroUtils.defaultVelocityContext();
        Map map = CompositeMap.of(context, defaultContext);
        VelocityUtils.writeRenderedContent(writer, fragment, map);
    }
}

