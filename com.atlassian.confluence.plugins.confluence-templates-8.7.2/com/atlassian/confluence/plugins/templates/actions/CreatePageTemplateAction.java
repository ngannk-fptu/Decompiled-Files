/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.renderer.RenderContext
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.templates.actions.AbstractEditPageTemplateAction;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePageTemplateAction
extends AbstractEditPageTemplateAction {
    private static final Logger log = LoggerFactory.getLogger(CreatePageTemplateAction.class);

    public String execute() throws Exception {
        if (StringUtils.isNotEmpty((CharSequence)this.back)) {
            return "input";
        }
        if (StringUtils.isNotEmpty((CharSequence)this.preview)) {
            return "preview";
        }
        this.pageTemplate = this.createPageTemplate();
        if (this.getSpace() != null) {
            this.getSpace().addPageTemplate(this.pageTemplate);
        }
        this.pageTemplateManager.savePageTemplate(this.pageTemplate, null);
        this.setLabelsOnTemplate(this.pageTemplate);
        return "success" + this.globalTemplateSuffix();
    }

    private PageTemplate createPageTemplate() throws XhtmlException {
        PageTemplate template = new PageTemplate();
        template.setName(this.title);
        template.setDescription(this.description);
        template.setContent(this.formatConverter.convertToStorageFormat(this.wysiwygContent, this.getRenderContext()));
        template.setBodyType(BodyType.XHTML);
        return template;
    }

    @Override
    public String getWysiwygContent() {
        try {
            if (this.wysiwygContent != null && this.wysiwygContent.length() > 0) {
                return this.formatConverter.cleanEditorFormat(this.wysiwygContent, (RenderContext)new PageContext());
            }
            return super.getWysiwygContent();
        }
        catch (XhtmlException e) {
            log.error("CreatePageTemplateAction.getWysiwygContent", (Throwable)e);
            return super.getWysiwygContent();
        }
    }
}

