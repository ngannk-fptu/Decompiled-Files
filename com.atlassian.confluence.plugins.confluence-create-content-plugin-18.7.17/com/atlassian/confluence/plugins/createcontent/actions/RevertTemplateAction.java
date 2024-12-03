/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.plugins.templates.actions.AbstractEditPageTemplateAction
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.templates.actions.AbstractEditPageTemplateAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;

public class RevertTemplateAction
extends AbstractEditPageTemplateAction {
    public void validate() {
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String doRemove() {
        PageTemplate template = this.pageTemplateManager.getPageTemplate(this.entityId);
        this.pageTemplateManager.removePageTemplate(template);
        return "success" + this.globalTemplateSuffix();
    }
}

