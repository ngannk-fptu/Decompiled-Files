/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.SpacePermissionManager;

public class TargetUserCanUseConfluenceCondition
extends BaseConfluenceCondition {
    private SpacePermissionManager spacePermissionManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.spacePermissionManager.hasPermission("USECONFLUENCE", null, context.getTargetedUser());
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }
}

