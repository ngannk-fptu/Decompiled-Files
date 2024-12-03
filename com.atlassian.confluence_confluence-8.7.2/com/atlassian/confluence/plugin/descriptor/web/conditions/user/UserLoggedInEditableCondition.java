/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;

public class UserLoggedInEditableCondition
extends BaseConfluenceCondition {
    private UserAccessor userAccessor;
    private SettingsManager settingsManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        if (this.settingsManager.getGlobalSettings().isExternalUserManagement()) {
            return false;
        }
        ConfluenceUser user = context.getCurrentUser();
        return user != null && !this.userAccessor.isReadOnly(user);
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

