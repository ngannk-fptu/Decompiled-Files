/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.AbstractPageAction;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPreviewPageAction
extends AbstractPageAction
implements Beanable {
    private static final Logger log = LoggerFactory.getLogger(AbstractPreviewPageAction.class);
    protected FormatConverter formatConverter;
    protected WikiStyleRenderer wikiStyleRenderer;
    private String xHtmlContent;
    @Deprecated
    protected String preview;
    @Deprecated
    protected String back;
    protected Map bean = new HashMap();
    protected String wysiwygContent;
    private String cleanedWysiwygContent;
    protected String storageFormat;
    @Deprecated
    protected boolean inPreview;

    @Override
    public Object getBean() {
        return this.bean;
    }

    @Deprecated
    public void setInPreview(boolean inPreview) {
        this.inPreview = inPreview;
    }

    @Deprecated
    public boolean getInPreview() {
        return this.inPreview;
    }

    @Deprecated
    public void setPreview(String preview) {
        this.preview = preview;
    }

    @Deprecated
    public void setBack(String back) {
        this.back = back;
    }

    public void setWysiwygContent(String content) {
        this.wysiwygContent = content;
    }

    @HtmlSafe
    public String getxHtmlContent() {
        if (this.xHtmlContent == null) {
            this.updateXHtmlContent();
        }
        return this.xHtmlContent;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public void setFormatConverter(FormatConverter formatConverter) {
        if (this.formatConverter != formatConverter) {
            this.formatConverter = formatConverter;
            this.storageFormat = null;
        }
    }

    protected void updateXHtmlContent() {
        this.xHtmlContent = this.wysiwygContent;
    }

    @Override
    public void validate() {
        super.validate();
        this.storageFormat = this.formatConverter.validateAndConvertToStorageFormat(this, this.wysiwygContent, this.getRenderContext());
    }

    public String getWysiwygContent() throws XhtmlException {
        if (this.wysiwygContent == null) {
            AbstractPage page = this.getPage();
            this.wysiwygContent = page == null ? "" : this.getEditorFormattedContent(this.formatConverter.cleanStorageFormat(page.getBodyAsString()));
            this.cleanedWysiwygContent = this.wysiwygContent;
        }
        if (!this.wysiwygContent.equals(this.cleanedWysiwygContent)) {
            this.cleanedWysiwygContent = this.formatConverter.cleanEditorFormat(this.wysiwygContent, this.getRenderContext());
        }
        return this.cleanedWysiwygContent;
    }

    public String getEditorFormattedContent(String storageFormat) {
        return this.formatConverter.convertToEditorFormat(storageFormat, this.getRenderContext());
    }

    protected String getContentForSaving() throws XhtmlParsingException, XhtmlException {
        return this.storageFormat != null ? this.storageFormat : this.formatConverter.convertToStorageFormat(this.wysiwygContent, this.getRenderContext());
    }

    protected RenderContext getRenderContext() {
        return this.getAttachmentSourceContent() != null ? this.getAttachmentSourceContent().toPageContext() : new PageContext();
    }

    protected ConversionContext getConversionContext() {
        return new DefaultConversionContext(this.getRenderContext());
    }

    protected String getStorageFormat() throws XhtmlException {
        if (this.storageFormat == null) {
            this.storageFormat = this.getContentForSaving();
        }
        return this.storageFormat;
    }
}

