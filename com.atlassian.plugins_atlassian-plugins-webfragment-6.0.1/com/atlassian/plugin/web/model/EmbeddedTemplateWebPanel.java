/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.model.AbstractWebPanel;
import com.atlassian.plugin.web.renderer.RendererException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedTemplateWebPanel
extends AbstractWebPanel {
    private String templateBody;
    private static final Logger logger = LoggerFactory.getLogger((String)EmbeddedTemplateWebPanel.class.getName());

    public EmbeddedTemplateWebPanel(PluginAccessor pluginAccessor) {
        super(pluginAccessor);
    }

    public void setTemplateBody(String templateBody) {
        this.templateBody = templateBody;
    }

    @Override
    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        try {
            this.getRenderer().renderFragment(writer, this.templateBody, this.plugin, context);
        }
        catch (RendererException e) {
            String message = String.format("Error rendering WebPanel: %s\nTemplate contents: %s", e.getMessage(), this.templateBody);
            logger.warn(message, (Throwable)e);
            writer.write(StringEscapeUtils.escapeHtml4((String)message));
        }
    }

    public String getHtml(Map<String, Object> context) {
        try {
            StringWriter out = new StringWriter();
            this.writeHtml(out, context);
            return out.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

