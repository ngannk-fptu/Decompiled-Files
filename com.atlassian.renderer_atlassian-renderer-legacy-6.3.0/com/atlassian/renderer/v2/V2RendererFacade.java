/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.renderer.v2;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RendererConfiguration;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.v2.Renderer;
import com.opensymphony.util.TextUtils;

public class V2RendererFacade
implements WikiStyleRenderer {
    private RendererConfiguration rendererConfiguration;
    private LinkRenderer defaultLinkRenderer;
    private EmbeddedResourceRenderer defaultEmbeddedRenderer;
    private Renderer renderer;

    public V2RendererFacade() {
    }

    public V2RendererFacade(RendererConfiguration rendererConfiguration, LinkRenderer defaultLinkRenderer, EmbeddedResourceRenderer defaultEmbeddedRenderer, Renderer renderer) {
        this.rendererConfiguration = rendererConfiguration;
        this.defaultLinkRenderer = defaultLinkRenderer;
        this.defaultEmbeddedRenderer = defaultEmbeddedRenderer;
        this.renderer = renderer;
    }

    public void setRendererConfiguration(RendererConfiguration rendererConfiguration) {
        this.rendererConfiguration = rendererConfiguration;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public String convertWikiToXHtml(RenderContext context, String wiki) {
        if (!TextUtils.stringSet((String)wiki)) {
            return "";
        }
        if (context.getRenderMode() != null && context.getRenderMode().renderNothing()) {
            return wiki;
        }
        this.initializeContext(context);
        return this.renderer.render(wiki, context);
    }

    public String convertWikiToText(RenderContext context, String wiki) {
        if (!TextUtils.stringSet((String)wiki)) {
            return "";
        }
        if (context.getRenderMode().renderNothing()) {
            return wiki;
        }
        this.initializeContext(context);
        return this.renderer.renderAsText(wiki, context);
    }

    public void setDefaultLinkRenderer(LinkRenderer linkRenderer) {
        this.defaultLinkRenderer = linkRenderer;
    }

    public void setDefaultEmbeddedRenderer(EmbeddedResourceRenderer embeddedRenderer) {
        this.defaultEmbeddedRenderer = embeddedRenderer;
    }

    private void initializeContext(RenderContext context) {
        if (context.getSiteRoot() == null) {
            context.setSiteRoot(this.rendererConfiguration.getWebAppContextPath());
        }
        if (context.getImagePath() == null) {
            context.setImagePath(context.getSiteRoot() + "/images");
        }
        if (context.getLinkRenderer() == null) {
            context.setLinkRenderer(this.defaultLinkRenderer);
        }
        if (context.getEmbeddedResourceRenderer() == null) {
            context.setEmbeddedResourceRenderer(this.defaultEmbeddedRenderer);
        }
        if (context.getCharacterEncoding() == null) {
            context.setCharacterEncoding(this.rendererConfiguration.getCharacterEncoding());
        }
    }
}

