/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.confluence.pages.actions.ViewPageAction
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.benryan.webwork;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@ReadOnlyAccessBlocked
public class WordAction
extends ViewPageAction {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, (Object)this.getPage());
    }
}

