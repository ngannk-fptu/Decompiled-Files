/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.renderer.template.TemplateRenderingException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timer
 *  com.atlassian.util.profiling.Timers
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.renderer.template.TemplateRenderingException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timer;
import com.atlassian.util.profiling.Timers;
import java.util.Collections;
import java.util.Map;

public class ConfluenceSoyTemplateRenderer
implements TemplateRenderer {
    private static final Timer TIMER_RENDER_TO = Timers.timer((String)"Rendering soy template");
    private final SoyTemplateRenderer delegate;

    public ConfluenceSoyTemplateRenderer(SoyTemplateRenderer delegate) {
        this.delegate = delegate;
    }

    public void renderTo(Appendable appendable, String templateProviderPluginKey, String templateName, Map<String, Object> data) throws TemplateRenderingException {
        this.renderTo(appendable, templateProviderPluginKey, templateName, data, Collections.emptyMap());
    }

    public Streamable render(String templateProviderPluginKey, String templateName, Map<String, Object> data) throws TemplateRenderingException {
        return writer -> this.renderTo(writer, templateProviderPluginKey, templateName, data);
    }

    public void renderTo(Appendable appendable, String templateProviderPluginKey, String templateName, Map<String, Object> data, Map<String, Object> injectedData) throws TemplateRenderingException {
        templateName = ConfluenceSoyTemplateRenderer.removeSuffix(templateName);
        try (Ticker ignored = TIMER_RENDER_TO.start(new String[]{templateProviderPluginKey, templateName});){
            this.delegate.render(appendable, templateProviderPluginKey, templateName, data, injectedData);
        }
        catch (SoyException e) {
            throw new TemplateRenderingException(e.getMessage(), (Throwable)e);
        }
    }

    public Streamable render(String templateProviderPluginKey, String templateName, Map<String, Object> data, Map<String, Object> injectedData) throws TemplateRenderingException {
        return writer -> this.renderTo(writer, templateProviderPluginKey, templateName, data, injectedData);
    }

    private static String removeSuffix(String templateName) {
        if (templateName.length() > 4 && templateName.endsWith(".soy")) {
            return templateName.substring(0, templateName.length() - 4);
        }
        return templateName;
    }
}

