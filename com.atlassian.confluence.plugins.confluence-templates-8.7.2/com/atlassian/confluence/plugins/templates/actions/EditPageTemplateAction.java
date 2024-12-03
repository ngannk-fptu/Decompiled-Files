/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.templates.actions.AbstractEditPageTemplateAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditPageTemplateAction
extends AbstractEditPageTemplateAction {
    private static final Logger log = LoggerFactory.getLogger(EditPageTemplateAction.class);
    private boolean versionMismatch;
    private String oldContent;
    private boolean isTitleReadOnly;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public boolean isVersionMismatch() {
        return this.versionMismatch;
    }

    public void setVersionMismatch(boolean versionMismatch) {
        this.versionMismatch = versionMismatch;
    }

    public String getOldContent() {
        return this.oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public boolean isTitleReadOnly() {
        PageTemplate pageTemplate = this.getPageTemplate();
        if (pageTemplate != null) {
            this.isTitleReadOnly = StringUtils.isNotBlank((CharSequence)pageTemplate.getPluginKey());
        }
        return this.isTitleReadOnly;
    }

    @Override
    public String getTitle() {
        if (this.title == null && this.getPageTemplate() != null) {
            this.title = this.getPageTemplate().getName();
        }
        return this.title;
    }

    @Override
    public String getDescription() {
        if (this.description == null && this.getPageTemplate() != null) {
            this.description = this.getPageTemplate().getDescription();
        }
        return this.description;
    }

    @Override
    public String getWysiwygContent() {
        if (this.wysiwygContent == null && this.getPageTemplate() != null) {
            this.wysiwygContent = this.formatConverter.convertToEditorFormat(this.getPageTemplate().getContent(), this.getRenderContext());
        }
        return this.wysiwygContent;
    }

    public String doEdit() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.back)) {
            return "input";
        }
        if (StringUtils.isNotBlank((CharSequence)this.preview)) {
            return "preview";
        }
        PageTemplate pageTemplate = this.getPageTemplate();
        pageTemplate.setName(this.getTitle());
        pageTemplate.setDescription(this.getDescription());
        pageTemplate.setContent(this.formatConverter.convertToStorageFormat(this.getWysiwygContent(), this.getRenderContext()));
        try {
            this.pageTemplateManager.savePageTemplate(pageTemplate, this.originalPageTemplate);
        }
        catch (StaleObjectStateException e) {
            return this.addStaleError((Exception)((Object)e));
        }
        return "success" + this.globalTemplateSuffix();
    }

    private String addStaleError(Exception e) {
        this.versionMismatch = true;
        this.pageTemplateManager.refreshPageTemplate(this.getPageTemplate());
        this.oldContent = this.getPageTemplate().getContent();
        log.debug("Editing an outdated version of the page!", (Throwable)e);
        this.addActionError(this.getText("editing.an.outdated.emailtemplate.version"));
        return "error";
    }
}

