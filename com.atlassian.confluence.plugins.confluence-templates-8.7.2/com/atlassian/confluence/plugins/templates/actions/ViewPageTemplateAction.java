/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.renderer.PageTemplateContext
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.plugins.templates.actions.AbstractPageTemplateAction;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class ViewPageTemplateAction
extends AbstractPageTemplateAction {
    private FormatConverter formatConverter;
    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.content = this.getPageTemplate().getContent();
        return "success";
    }

    @HtmlSafe
    public String getPageXHtmlContent() {
        PageTemplateContext pageTemplateContext = new PageTemplateContext(this.getPageTemplate());
        pageTemplateContext.setOutputType("preview");
        pageTemplateContext.addParam((Object)"com.atlassian.confluence.plugins.templates", (Object)true);
        pageTemplateContext.addParam((Object)"com.atlassian.confluence.plugins.templates.input.disable", (Object)true);
        return this.formatConverter.convertToViewFormat(this.getPageTemplate().getContent(), (RenderContext)pageTemplateContext);
    }

    public void setFormatConverter(FormatConverter formatConverter) {
        this.formatConverter = formatConverter;
    }
}

