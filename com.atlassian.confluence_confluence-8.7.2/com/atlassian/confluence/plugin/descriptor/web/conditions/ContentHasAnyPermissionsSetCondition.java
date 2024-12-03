/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class ContentHasAnyPermissionsSetCondition
extends BaseConfluenceCondition {
    private ContentPermissionManager contentPermissionManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        ContentEntityObject lowestLevelContentWithPermissions;
        ContentEntityObject contentEntityObject = lowestLevelContentWithPermissions = context.getPage() != null ? context.getPage() : context.getDraft();
        if (lowestLevelContentWithPermissions == null) {
            lowestLevelContentWithPermissions = context.getParentPage();
        }
        if (lowestLevelContentWithPermissions == null) {
            return false;
        }
        if (lowestLevelContentWithPermissions.hasContentPermissions()) {
            return true;
        }
        return !this.contentPermissionManager.getInheritedContentPermissionSets(lowestLevelContentWithPermissions).isEmpty();
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }
}

