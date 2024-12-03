/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.renderer.PageTemplateContext
 *  com.atlassian.confluence.util.LabelUtil
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.templates.actions.AbstractPageTemplateAction;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEditPageTemplateAction
extends AbstractPageTemplateAction {
    protected String preview;
    protected String back;
    protected String title;
    protected String wysiwygContent;
    protected String description;
    protected FormatConverter formatConverter;
    private LabelManager labelManager;
    protected PageTemplateContext context = new PageTemplateContext(new PageTemplate());

    public AbstractEditPageTemplateAction() {
        this.context.addParam((Object)"com.atlassian.confluence.plugins.templates", (Object)true);
    }

    @Override
    public void validate() {
        super.validate();
        String title = this.getTitle();
        if (!StringUtils.isNotEmpty((CharSequence)title)) {
            this.addActionError(this.getText("page.template.name.empty"));
        } else if (title.length() > 255) {
            this.addActionError(this.getText("page.template.name.too.long"));
        }
        if (this.otherTemplateExists(title)) {
            this.addActionError(this.getText("page.template.name.exists"));
        }
        this.formatConverter.validateAndConvertToStorageFormat((ConfluenceActionSupport)this, this.wysiwygContent, this.getRenderContext());
    }

    private boolean otherTemplateExists(String title) {
        PageTemplate currentTemplate = this.getPageTemplate();
        PageTemplate otherTemplate = this.pageTemplateManager.getPageTemplate(title, this.getSpace());
        return this.pageTemplateManager.canCreate(currentTemplate, otherTemplate);
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = StringUtils.trim((String)title);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWysiwygContent() {
        return this.wysiwygContent;
    }

    public void setWysiwygContent(String wysiwygContent) {
        this.wysiwygContent = wysiwygContent;
    }

    public void setFormatConverter(FormatConverter formatConverter) {
        this.formatConverter = formatConverter;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    protected void setLabelsOnTemplate(PageTemplate template) {
        LabelUtil.syncState((String)this.getLabelsString(), (LabelManager)this.labelManager, (User)this.getAuthenticatedUser(), (Labelable)template);
    }

    protected RenderContext getRenderContext() {
        return this.context;
    }

    public String getCancelResult() {
        return "cancel" + this.globalTemplateSuffix();
    }
}

