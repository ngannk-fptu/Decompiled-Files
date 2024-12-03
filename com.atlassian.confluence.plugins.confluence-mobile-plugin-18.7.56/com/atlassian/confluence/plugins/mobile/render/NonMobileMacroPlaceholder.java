/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.mobile.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.plugins.mobile.analytic.MobileUnsupportedMacroAnalyticEvent;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.event.api.EventPublisher;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class NonMobileMacroPlaceholder {
    private static final List<String> HIDDEN_MOBILE_MACRO_LIST = Collections.unmodifiableList(Arrays.asList("create-from-template"));
    private TemplateRenderer templateRenderer;
    private EventPublisher eventPublisher;

    public NonMobileMacroPlaceholder(TemplateRenderer templateRenderer, EventPublisher eventPublisher) {
        this.templateRenderer = templateRenderer;
        this.eventPublisher = eventPublisher;
    }

    public String create(String macroName, Macro macro, ConversionContext context) {
        ContentEntityObject entity = this.getOriginalEntity(context);
        StringWriter writer = new StringWriter();
        if (!HIDDEN_MOBILE_MACRO_LIST.contains(macroName)) {
            UrlBuilder builder = new UrlBuilder(entity.getUrlPath());
            builder.add("desktop", true);
            builder.add("macroName", macroName);
            Map<String, Object> data = Collections.singletonMap("desktopUrl", builder.toUrl());
            if (macro.getOutputType() == Macro.OutputType.INLINE) {
                this.createInline(writer, macroName, data);
            } else {
                this.createBlock(writer, macroName, data);
            }
        }
        this.publishAnalyticEvent(entity.getId(), macroName);
        return writer.toString();
    }

    private void publishAnalyticEvent(Long contentId, String macroName) {
        HttpServletRequest req = ServletContextThreadLocal.getRequest();
        if (req == null) {
            return;
        }
        this.eventPublisher.publish((Object)new MobileUnsupportedMacroAnalyticEvent(req, macroName, contentId));
    }

    private void createInline(Appendable appendable, String macroName, Map<String, Object> data) {
        this.templateRenderer.renderTo(appendable, "com.atlassian.confluence.plugins.confluence-mobile:view-content-resources", "Confluence.Templates.Mobile.Macros.inlineNonMobilePlaceholder.soy", data);
    }

    private void createBlock(Appendable appendable, String macroName, Map<String, Object> data) {
        this.templateRenderer.renderTo(appendable, "com.atlassian.confluence.plugins.confluence-mobile:view-content-resources", "Confluence.Templates.Mobile.Macros.blockNonMobilePlaceholder.soy", data);
    }

    private ContentEntityObject getOriginalEntity(ConversionContext context) {
        PageContext pageContext = context.getPageContext();
        if (pageContext != null) {
            while (pageContext.getOriginalContext() != pageContext) {
                pageContext = pageContext.getOriginalContext();
            }
            return pageContext.getEntity();
        }
        return context.getEntity();
    }
}

