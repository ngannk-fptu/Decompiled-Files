/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class ContentHasInheritedPermissionsSetCondition
extends BaseConfluenceCondition {
    private ContentPermissionManager contentPermissionManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        AbstractPage parentPage = context.getParentPage();
        return parentPage != null && (!this.contentPermissionManager.getContentPermissionSets(parentPage, "View").isEmpty() || !this.contentPermissionManager.getInheritedContentPermissionSets(parentPage).isEmpty());
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }
}

