/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.HashMap;

public class InlineStyleHelper {
    private TemplateRenderer templateRenderer;
    private I18NBeanFactory i18nBeanFactory;

    public InlineStyleHelper(TemplateRenderer templateRenderer, I18NBeanFactory i18nBeanFactory) {
        this.templateRenderer = templateRenderer;
        this.i18nBeanFactory = i18nBeanFactory;
    }

    public String render(String content, PageContext pageContext) {
        String title = pageContext.getPageTitle();
        if (title == null) {
            title = this.i18nBeanFactory.getI18NBean().getText("untitled.content.render.title");
        }
        Space space = new Space(pageContext.getSpaceKey());
        StringBuilder builder = new StringBuilder();
        HashMap<String, Object> soyContext = new HashMap<String, Object>();
        soyContext.put("title", title);
        soyContext.put("styles", ConfluenceRenderUtils.renderSpaceStylesheet(space));
        soyContext.put("baseUrl", GeneralUtil.getGlobalSettings().getBaseUrl());
        soyContext.put("content", content);
        this.templateRenderer.renderTo(builder, "confluence.web.resources:content-body-with-styles", "Confluence.Templates.Export.withStyles.soy", soyContext);
        return builder.toString();
    }
}

