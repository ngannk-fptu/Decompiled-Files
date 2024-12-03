/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 */
package com.atlassian.troubleshooting.stp.scheduler.utils;

import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class RenderingUtils {
    public static String render(TemplateRenderer renderer, String template, Map<String, Object> map) throws RenderingException, IOException {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.putAll(map);
        StringWriter writer = new StringWriter();
        renderer.render(template, context, (Writer)writer);
        return writer.toString();
    }
}

