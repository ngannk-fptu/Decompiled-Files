/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.confluence.plugins.gatekeeper.controllers.AbstractPermissionsAction;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationLevel;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;

public class GlobalPermissionsAction
extends AbstractPermissionsAction {
    public GlobalPermissionsAction() {
        super(EvaluationLevel.EVALUATE_GLOBAL);
    }

    @Override
    public boolean isPermitted() {
        return this.checkLicense() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public boolean isViewPermissionRequired() {
        return false;
    }

    public boolean isSpaceRequired() {
        return false;
    }

    @Override
    public void setPageId(long pageId) {
        pageId = 0L;
    }

    @Override
    public void setPageTitle(String pageTitle) {
        pageTitle = null;
    }
}

