/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.copyspace;

import com.atlassian.confluence.plugin.copyspace.service.PermissionService;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;

public class CopySpaceCondition
extends BaseConfluenceCondition {
    private final PermissionService permissionService;

    public CopySpaceCondition(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        Space space = context.getSpace();
        ConfluenceUser user = context.getCurrentUser();
        if (user == null || space == null) {
            return false;
        }
        return this.permissionService.canInitiateSpaceCopy((User)user, space);
    }
}

