/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.HtmlToTextConverter
 *  com.opensymphony.util.TextUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.v2;

import com.atlassian.mail.HtmlToTextConverter;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.MutableRenderer;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.opensymphony.util.TextUtils;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V2Renderer
implements MutableRenderer {
    private static final Logger log = LoggerFactory.getLogger(V2Renderer.class);
    public static final String RENDERER_TYPE = "atlassian-wiki-renderer";
    private RendererComponent[] components = new RendererComponent[0];

    public V2Renderer() {
    }

    public V2Renderer(List components) {
        this.components = components.toArray(new RendererComponent[components.size()]);
    }

    @Override
    public void setComponents(List components) {
        this.components = components.toArray(new RendererComponent[components.size()]);
    }

    @Override
    public String render(String wiki, RenderContext renderContext) {
        try {
            if (!TextUtils.stringSet((String)wiki)) {
                return "";
            }
            if (renderContext.getRenderMode().renderNothing()) {
                return wiki;
            }
            String renderedWiki = wiki;
            for (int i = 0; i < this.components.length; ++i) {
                RendererComponent rendererComponent = this.components[i];
                RenderMode renderMode = renderContext.getRenderMode();
                if (!rendererComponent.shouldRender(renderMode)) continue;
                renderedWiki = rendererComponent.render(renderedWiki, renderContext);
            }
            return renderedWiki;
        }
        catch (Throwable t) {
            log.error("Unable to render content due to system error: " + t.getMessage(), t);
            return RenderUtils.error("Unable to render content due to system error: " + t.getMessage());
        }
    }

    @Override
    public String renderAsText(String originalContent, RenderContext context) {
        try {
            return new HtmlToTextConverter().convert(this.render(originalContent, context));
        }
        catch (IOException e) {
            return originalContent;
        }
    }

    @Override
    public String getRendererType() {
        return RENDERER_TYPE;
    }
}

