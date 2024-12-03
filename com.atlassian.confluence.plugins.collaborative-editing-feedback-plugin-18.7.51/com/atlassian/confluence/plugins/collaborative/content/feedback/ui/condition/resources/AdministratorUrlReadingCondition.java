/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources;

import com.atlassian.confluence.plugins.collaborative.content.feedback.service.PermissionService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources.AbstractUrlReadingCondition;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

public class AdministratorUrlReadingCondition
extends AbstractUrlReadingCondition {
    private static final String ADMIN_CONDITION_QUERY_PARAM = "cefp_is_admin";
    private final PermissionService permissionService;

    public AdministratorUrlReadingCondition(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    protected String getQueryParamName() {
        return ADMIN_CONDITION_QUERY_PARAM;
    }

    @Override
    protected boolean getQueryParamValue() {
        return this.permissionService.isSysAdmin((User)AuthenticatedUserThreadLocal.get());
    }
}

