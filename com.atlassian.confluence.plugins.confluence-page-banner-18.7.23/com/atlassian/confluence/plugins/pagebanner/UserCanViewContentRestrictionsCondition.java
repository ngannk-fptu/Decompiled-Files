/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.pagebanner;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;

public class UserCanViewContentRestrictionsCondition
extends BaseConfluenceCondition {
    private final SpacePermissionManager spacePermissionManager;

    public UserCanViewContentRestrictionsCondition(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser currentUser = context.getCurrentUser();
        return currentUser != null && this.spacePermissionManager.hasPermission("USECONFLUENCE", null, (User)currentUser);
    }
}

