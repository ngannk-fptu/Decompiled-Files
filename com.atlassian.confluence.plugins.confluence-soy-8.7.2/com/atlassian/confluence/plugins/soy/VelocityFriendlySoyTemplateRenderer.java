/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.renderer.template.TemplateRenderingException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.renderer.template.TemplateRenderingException;
import java.io.StringWriter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityFriendlySoyTemplateRenderer {
    private static final Logger log = LoggerFactory.getLogger(VelocityFriendlySoyTemplateRenderer.class);
    private final TemplateRenderer templateRenderer;

    public VelocityFriendlySoyTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public String getRenderedTemplateHtml(String templateProviderPluginKey, String templateName, Map<String, Object> data) {
        StringWriter writer = new StringWriter();
        try {
            this.templateRenderer.renderTo((Appendable)writer, templateProviderPluginKey, templateName, data);
        }
        catch (TemplateRenderingException ex) {
            log.warn("Error while rendering the template " + templateProviderPluginKey + ":" + templateName, (Throwable)ex);
        }
        return writer.toString();
    }
}

