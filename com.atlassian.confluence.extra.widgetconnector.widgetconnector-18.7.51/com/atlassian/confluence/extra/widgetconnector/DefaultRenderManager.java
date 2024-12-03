/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.extra.widgetconnector.RenderManager;
import com.atlassian.confluence.extra.widgetconnector.WidgetConnectorUtil;
import com.atlassian.confluence.extra.widgetconnector.WidgetImagePlaceholder;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.analytics.RendererMatchAnalyticsEvent;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DefaultRenderManager
implements RenderManager {
    public static final String ERROR_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/error.vm";
    public static final String ERROR_MESSAGE_PARAM = "errorMessage";
    public static final String URL_PARAM = "urlHtml";
    public static final String BASE_URL_PARAM = "baseUrlHtml";
    private final EventPublisher eventPublisher;
    private final List<WidgetRenderer> renderSupporter;

    public DefaultRenderManager(List<WidgetRenderer> renderSupporter, EventPublisher eventPublisher) {
        this.renderSupporter = renderSupporter;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        for (WidgetRenderer widgetRenderer : this.renderSupporter) {
            String embedHtml;
            if (!widgetRenderer.matches(url) || !StringUtils.isNotEmpty((CharSequence)(embedHtml = widgetRenderer.getEmbeddedHtml(url, params)))) continue;
            this.publishRendererMatchEvent(widgetRenderer);
            return embedHtml;
        }
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        contextMap.put(URL_PARAM, HtmlUtil.htmlEncode((String)url));
        contextMap.put(BASE_URL_PARAM, HtmlUtil.htmlEncode((String)WidgetConnectorUtil.getBaseUrl(url)));
        return this.getRenderedTemplate(contextMap);
    }

    @Override
    public ImagePlaceholder getImagePlaceholder(String url, Map<String, String> params) {
        for (WidgetRenderer widgetRenderer : this.renderSupporter) {
            if (!widgetRenderer.matches(url)) continue;
            if (!(widgetRenderer instanceof WidgetImagePlaceholder)) break;
            ImagePlaceholder placeholder = ((WidgetImagePlaceholder)((Object)widgetRenderer)).getImagePlaceholder(url, params);
            if (placeholder == null) continue;
            return placeholder;
        }
        return WidgetConnectorUtil.generateDefaultImagePlaceholder(WidgetConnectorUtil.getBaseUrl(url));
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    protected String getRenderedTemplate(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)ERROR_TEMPLATE, contextMap);
    }

    private void publishRendererMatchEvent(WidgetRenderer widgetRenderer) {
        this.eventPublisher.publish((Object)new RendererMatchAnalyticsEvent(widgetRenderer));
    }
}

