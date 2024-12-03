/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;

@WebSudoRequired
@AdminOnly
public class RefreshLicensingAction
extends ConfluenceActionSupport {
    private UserChecker userChecker;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String execute() throws Exception {
        this.userChecker.resetResult();
        this.userChecker.hasTooManyUsers();
        return "success";
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }
}

