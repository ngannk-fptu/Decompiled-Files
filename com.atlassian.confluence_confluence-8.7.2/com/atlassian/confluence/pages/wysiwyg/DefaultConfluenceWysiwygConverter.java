/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.renderer.wysiwyg.WysiwygConverter
 */
package com.atlassian.confluence.pages.wysiwyg;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.wysiwyg.ConfluenceWysiwygConverter;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.wysiwyg.WysiwygConverter;

public class DefaultConfluenceWysiwygConverter
implements ConfluenceWysiwygConverter {
    private ContentEntityManager contentEntityManager;
    private WysiwygConverter converter;
    private WikiStyleRenderer wikiStyleRenderer;

    public void setWysiwygConverter(WysiwygConverter converter) {
        this.converter = converter;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    public String convertXHtmlToWikiMarkup(ContentEntityObject content, String xhtml) {
        return this.convertXHtmlToWikiMarkup(xhtml);
    }

    private String convertXHtmlToWikiMarkup(String xhtml) {
        return this.converter.convertXHtmlToWikiMarkup(xhtml);
    }

    @Override
    @HtmlSafe
    public String convertWikiMarkupToXHtml(ContentEntityObject content, String wikiMarkup) {
        return this.convertWikiMarkupToXHtml(content, null, wikiMarkup);
    }

    @Override
    @HtmlSafe
    public String convertWikiMarkupToXHtml(ContentEntityObject content, String spaceKey, String wikiMarkup) {
        return this.converter.convertWikiMarkupToXHtml((RenderContext)this.getPageContext(content, spaceKey), wikiMarkup);
    }

    @Override
    @HtmlSafe
    public String convertXHtmlToWikiMarkup(String xhtml, String pageId) {
        return this.convertXHtmlToWikiMarkup(xhtml);
    }

    private long convertPageId(String pageIdString) {
        return Long.parseLong(pageIdString);
    }

    @Override
    @HtmlSafe
    public String convertWikiMarkupToXHtml(String wikiMarkup, String pageId) {
        return this.convertWikiMarkupToXHtml(wikiMarkup, pageId, null);
    }

    @Override
    @HtmlSafe
    public String convertWikiMarkupToXHtml(String wikiMarkup, String pageId, String spaceKey) {
        return this.convertWikiMarkupToXHtml(this.contentEntityManager.getById(this.convertPageId(pageId)), spaceKey, wikiMarkup);
    }

    @Override
    @HtmlSafe
    public String convertToPreview(String inputText, String contentId, String spaceKey, String fromMode) {
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(this.convertPageId(contentId));
        if ("richtext".equals(fromMode)) {
            inputText = this.convertXHtmlToWikiMarkup(contentEntityObject, inputText);
        }
        PageContext context = this.getPageContext(contentEntityObject, spaceKey);
        context.setOutputType("preview");
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)context, inputText);
    }

    private PageContext getPageContext(ContentEntityObject contentEntityObject, String spaceKey) {
        return contentEntityObject == null ? new PageContext(spaceKey) : contentEntityObject.toPageContext();
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }
}

