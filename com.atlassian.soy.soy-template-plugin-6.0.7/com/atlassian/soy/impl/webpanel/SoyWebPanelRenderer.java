/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.renderer.RendererException
 *  com.atlassian.plugin.web.renderer.WebPanelRenderer
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.apache.commons.lang3.NotImplementedException
 */
package com.atlassian.soy.impl.webpanel;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.atlassian.soy.impl.webpanel.TemplateAddressing;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;

public class SoyWebPanelRenderer
implements WebPanelRenderer {
    private final EventPublisher eventPublisher;
    private final SoyTemplateRenderer soyTemplateRenderer;

    public SoyWebPanelRenderer(EventPublisher eventPublisher, SoyTemplateRenderer soyTemplateRenderer) {
        this.eventPublisher = eventPublisher;
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    public String getResourceType() {
        return "soy";
    }

    public void render(String templateAddress, Plugin plugin, Map<String, Object> context, Writer writer) throws RendererException, IOException {
        try {
            TemplateAddressing.Address address = TemplateAddressing.parseTemplateAddress(templateAddress, plugin.getKey());
            this.soyTemplateRenderer.render((Appendable)writer, address.getCompleteKey().toString(), address.getTemplateName(), context);
        }
        catch (SoyException e) {
            throw new RendererException(e.getMessage(), (Throwable)e);
        }
    }

    public String renderFragment(String fragment, Plugin plugin, Map<String, Object> context) throws RendererException {
        throw new NotImplementedException("Not implemented for SoyWebPanelRenderer");
    }

    public void renderFragment(Writer writer, String fragment, Plugin plugin, Map<String, Object> context) throws RendererException, IOException {
        throw new NotImplementedException("Not implemented for SoyWebPanelRenderer");
    }
}

