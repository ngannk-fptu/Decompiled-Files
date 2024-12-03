/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.confluence.content.render.xhtml.compatibility;

import com.atlassian.confluence.content.render.xhtml.BatchedRenderRequest;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.view.BatchedRenderResult;
import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;

public class BodyTypeAwareRenderer
implements Renderer {
    private final WikiStyleRenderer wikiRenderer;
    private final Renderer xhtmlRenderer;

    public BodyTypeAwareRenderer(Renderer xhtmlRenderer, WikiStyleRenderer wikiRenderer) {
        this.wikiRenderer = wikiRenderer;
        this.xhtmlRenderer = xhtmlRenderer;
    }

    @Override
    public String render(ContentEntityObject content) {
        return this.render(content, (ConversionContext)new DefaultConversionContext(content.toPageContext()));
    }

    @Override
    public String render(ContentEntityObject content, ConversionContext conversionContext) {
        BodyContent bodyContent = content.getBodyContent();
        String result = BodyType.WIKI.equals(bodyContent.getBodyType()) ? this.wikiRenderer.convertWikiToXHtml((RenderContext)(conversionContext != null ? conversionContext.getPageContext() : null), bodyContent.getBody()) : (BodyType.XHTML.equals(bodyContent.getBodyType()) ? this.xhtmlRenderer.render(content, conversionContext) : StringEscapeUtils.escapeHtml4((String)bodyContent.getBody()));
        return result;
    }

    @Override
    public String render(String xml, ConversionContext conversionContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RenderResult renderWithResult(String xml, ConversionContext conversionContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BatchedRenderResult> render(BatchedRenderRequest ... renderRequests) {
        throw new UnsupportedOperationException();
    }
}

