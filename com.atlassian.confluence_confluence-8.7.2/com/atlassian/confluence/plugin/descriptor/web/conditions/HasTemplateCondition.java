/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public class HasTemplateCondition
extends BaseConfluenceCondition {
    PageTemplateManager pageTemplateManager;

    public void setPageTemplateManager(PageTemplateManager pageTemplateManager) {
        this.pageTemplateManager = pageTemplateManager;
    }

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        boolean hasGlobalTemplates;
        Space space = context.getSpace();
        List globalTemplateList = this.pageTemplateManager.getGlobalPageTemplates();
        boolean bl = hasGlobalTemplates = globalTemplateList != null && !globalTemplateList.isEmpty();
        if (space != null) {
            List spaceTemplates = space.getPageTemplates();
            return hasGlobalTemplates || spaceTemplates != null && !spaceTemplates.isEmpty();
        }
        return hasGlobalTemplates;
    }
}

