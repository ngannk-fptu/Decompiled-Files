/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.wysiwyg.ConfluenceWysiwygConverter;

public class WysiwygConverterAction
extends ConfluenceActionSupport
implements Beanable {
    private String bean;
    private ConfluenceWysiwygConverter confluenceWysiwygConverter;
    private String wikiMarkup;
    private String xhtml;
    private String pageId;
    private String spaceKey;

    public String convertWikiMarkupToXHtmlWithoutPageWithSpaceKey() {
        this.bean = this.confluenceWysiwygConverter.convertWikiMarkupToXHtml(this.wikiMarkup, this.pageId, this.spaceKey);
        return "success";
    }

    public String convertXHtmlToWikiMarkupWithoutPage() {
        this.bean = this.confluenceWysiwygConverter.convertXHtmlToWikiMarkup(this.xhtml, this.pageId);
        return "success";
    }

    public void setWikiMarkup(String wikiMarkup) {
        this.wikiMarkup = wikiMarkup;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setXhtml(String xhtml) {
        this.xhtml = xhtml;
    }

    public void setConfluenceWysiwygConverter(ConfluenceWysiwygConverter confluenceWysiwygConverter) {
        this.confluenceWysiwygConverter = confluenceWysiwygConverter;
    }

    @Override
    public Object getBean() {
        return this.bean;
    }
}

