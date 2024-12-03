/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.notification.listeners.NotificationTemplate
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.model.AbstractWebPanel
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.mail.notification.listeners.NotificationTemplate;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.dailysummary.components.TemplateContextHelper;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentContext;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.model.AbstractWebPanel;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopularContentPanel
extends AbstractWebPanel {
    private static final String TEMPLATE_LOCATION = "com.atlassian.confluence.plugins.confluence-daily-summary-email:popular-content-template";
    private static final String TEMPLATE_NAME = "Confluence.Templates.Mail.Recommended.Content.excerpts.soy";
    private static final Logger log = LoggerFactory.getLogger(PopularContentPanel.class);
    private final TemplateRenderer templateRenderer;
    private final VelocityHelperService velocityHelper;

    public PopularContentPanel(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport TemplateRenderer templateRenderer, @ComponentImport VelocityHelperService velocityHelper) {
        super(pluginAccessor);
        this.templateRenderer = templateRenderer;
        this.velocityHelper = velocityHelper;
    }

    public String getHtml(Map<String, Object> context) {
        log.info("info: gettingHtml with webPanel id : " + ((Object)((Object)this)).toString() + "\nUsing context :" + context);
        try {
            StringWriter writer = new StringWriter();
            this.writeHtml(writer, context);
            return writer.toString();
        }
        catch (IOException io) {
            log.error("IOException writing webpanel to StringWriter", (Throwable)io);
            return "";
        }
    }

    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        PopularContentContext typedContext = new PopularContentContext(context);
        if (!this.hasContent(typedContext)) {
            return;
        }
        if (NotificationTemplate.ADG.isEnabled("recommended")) {
            this.templateRenderer.renderTo((Appendable)writer, TEMPLATE_LOCATION, TEMPLATE_NAME, TemplateContextHelper.VELOCITY2SOY.convert(context));
        } else {
            writer.write(this.velocityHelper.getRenderedTemplate("/templates/content/popular-content.vm", context));
        }
    }

    public boolean hasContent(PopularContentContext context) {
        List<PopularContentExcerptDto> excerpts = this.getPopularContentExcerpts(context);
        if (excerpts != null) {
            return !excerpts.isEmpty();
        }
        return false;
    }

    private List<PopularContentExcerptDto> getPopularContentExcerpts(PopularContentContext context) {
        return context.getPopularContentExcerpts();
    }
}

