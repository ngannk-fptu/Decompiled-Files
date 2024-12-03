/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.model.AbstractWebPanel;
import com.atlassian.plugin.web.renderer.RendererException;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTemplateWebPanel
extends AbstractWebPanel {
    private static final Logger logger = LoggerFactory.getLogger((String)ResourceTemplateWebPanel.class.getName());
    private String resourceFilename;

    public ResourceTemplateWebPanel(PluginAccessor pluginAccessor) {
        super(pluginAccessor);
    }

    public void setResourceFilename(String resourceFilename) {
        this.resourceFilename = (String)Preconditions.checkNotNull((Object)resourceFilename, (Object)"resourceFilename");
    }

    @Override
    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        try {
            this.getRenderer().render(this.resourceFilename, this.plugin, context, writer);
        }
        catch (RendererException e) {
            String message = String.format("Error rendering WebPanel (%s): %s", this.resourceFilename, e.getMessage());
            logger.warn(message, (Throwable)e);
            writer.write(StringEscapeUtils.escapeHtml4((String)message));
        }
    }

    public String getHtml(Map<String, Object> context) {
        try {
            StringWriter sink = new StringWriter();
            this.writeHtml(sink, context);
            return sink.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

