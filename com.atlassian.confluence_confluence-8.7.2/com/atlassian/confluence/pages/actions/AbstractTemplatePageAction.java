/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.actions.AbstractPreviewPageAction;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import java.util.List;

public abstract class AbstractTemplatePageAction
extends AbstractPreviewPageAction {
    private List globalPageTemplates;
    private List spacePageTemplates;
    protected String templateId;
    protected PageTemplateManager pageTemplateManager;
    private PageTemplate pageTemplate;

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    protected void loadTemplates() {
        this.globalPageTemplates = this.pageTemplateManager.getGlobalPageTemplates();
        if (this.getSpace() != null) {
            this.spacePageTemplates = this.getSpace().getPageTemplates();
        }
    }

    public List getGlobalPageTemplates() {
        return this.globalPageTemplates;
    }

    public List getSpacePageTemplates() {
        return this.spacePageTemplates;
    }

    public boolean isPageTemplatesAvailable() {
        return this.globalPageTemplates != null && !this.globalPageTemplates.isEmpty() || this.spacePageTemplates != null && !this.spacePageTemplates.isEmpty();
    }

    public void setPageTemplateManager(PageTemplateManager pageTemplateManager) {
        this.pageTemplateManager = pageTemplateManager;
    }

    public PageTemplate getPageTemplate() {
        if (this.templateId != null && this.pageTemplate == null) {
            this.pageTemplate = this.pageTemplateManager.getPageTemplate(Long.parseLong(this.templateId));
        }
        return this.pageTemplate;
    }
}

