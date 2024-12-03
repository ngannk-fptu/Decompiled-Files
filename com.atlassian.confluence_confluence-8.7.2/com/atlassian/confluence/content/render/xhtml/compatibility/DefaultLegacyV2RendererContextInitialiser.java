/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.embedded.EmbeddedResourceRenderer
 *  com.atlassian.renderer.links.LinkRenderer
 */
package com.atlassian.confluence.content.render.xhtml.compatibility;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.compatibility.LegacyV2RendererContextInitialiser;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.links.LinkRenderer;

public class DefaultLegacyV2RendererContextInitialiser
implements LegacyV2RendererContextInitialiser {
    private final ContextPathHolder contextPathHolder;
    private final LinkRenderer defaultLinkRenderer;
    private final EmbeddedResourceRenderer defaultEmbeddedRenderer;

    public DefaultLegacyV2RendererContextInitialiser(ContextPathHolder contextPathHolder, LinkRenderer defaultLinkRenderer, EmbeddedResourceRenderer defaultEmbeddedRenderer) {
        this.contextPathHolder = contextPathHolder;
        this.defaultLinkRenderer = defaultLinkRenderer;
        this.defaultEmbeddedRenderer = defaultEmbeddedRenderer;
    }

    @Override
    public ConversionContext initialise(ConversionContext conversionContext) {
        PageContext renderContext;
        PageContext pageContext = renderContext = conversionContext != null ? conversionContext.getPageContext() : null;
        if (renderContext != null) {
            if (renderContext.getSiteRoot() == null) {
                renderContext.setSiteRoot(this.contextPathHolder.getContextPath());
            }
            if (renderContext.getImagePath() == null) {
                renderContext.setImagePath(renderContext.getSiteRoot() + "/images");
            }
            if (renderContext.getCharacterEncoding() == null) {
                renderContext.setCharacterEncoding(GeneralUtil.getCharacterEncoding());
            }
            if (renderContext.getLinkRenderer() == null) {
                renderContext.setLinkRenderer(this.defaultLinkRenderer);
            }
            if (renderContext.getEmbeddedResourceRenderer() == null) {
                renderContext.setEmbeddedResourceRenderer(this.defaultEmbeddedRenderer);
            }
        }
        return conversionContext;
    }
}

