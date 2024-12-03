/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.user.User;

public class UserCanCreatePersonalSpaceCondition
extends BaseConfluenceCondition {
    private PermissionManager permissionManager;
    private PersonalInformationManager personalInformationManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser user = context.getCurrentUser();
        if (user == null) {
            return false;
        }
        return this.permissionManager.hasCreatePermission((User)user, (Object)this.personalInformationManager.getOrCreatePersonalInformation(user), Space.class);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }
}

