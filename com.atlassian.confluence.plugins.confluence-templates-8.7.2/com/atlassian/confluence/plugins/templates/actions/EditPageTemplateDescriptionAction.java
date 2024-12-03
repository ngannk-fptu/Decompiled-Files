/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException;
import com.atlassian.confluence.plugins.templates.actions.AbstractPageTemplateAction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditPageTemplateDescriptionAction
extends AbstractPageTemplateAction {
    private static final Logger log = LoggerFactory.getLogger(EditPageTemplateDescriptionAction.class);
    private String newDescription;

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    @Override
    public void validate() {
        super.validate();
        if (StringUtils.isNotBlank((CharSequence)this.newDescription) && this.newDescription.length() > 255) {
            this.addActionError(this.getText("page.template.description.too.long"));
        }
    }

    public String doEdit() {
        this.getPageTemplate().setDescription(StringUtils.trimToNull((String)this.newDescription));
        try {
            this.pageTemplateManager.savePageTemplate(this.pageTemplate, this.originalPageTemplate);
            return "success" + this.globalTemplateSuffix();
        }
        catch (StaleObjectStateException e) {
            return this.addStaleError((Exception)((Object)e));
        }
    }

    private String addStaleError(Exception e) {
        this.pageTemplateManager.refreshPageTemplate(this.getPageTemplate());
        log.debug("Editing an outdated version of the page!", (Throwable)e);
        this.addActionError(this.getText("editing.an.outdated.emailtemplate.version"));
        return "error";
    }
}

