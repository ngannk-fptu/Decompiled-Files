/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.PermissionService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

public class CanEditCondition
extends BaseConfluenceCondition {
    private final PermissionService permissionService;

    public CanEditCondition(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return webInterfaceContext.getPage() != null && this.permissionService.canEdit((User)AuthenticatedUserThreadLocal.get(), webInterfaceContext.getPage().getId());
    }
}

