/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class DraftStatusLozengeCondition
extends BaseConfluenceCondition {
    private DraftsTransitionHelper draftsTransitionHelper;

    @Override
    protected final boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        AbstractPage page = webInterfaceContext.getPage();
        return page != null && !this.draftsTransitionHelper.getEditMode(page.getSpaceKey()).equals("legacy") && page.isDraft();
    }

    public void setDraftsTransitionHelper(DraftsTransitionHelper draftsTransitionHelper) {
        this.draftsTransitionHelper = draftsTransitionHelper;
    }
}

