/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class CanSignupCondition
extends BaseConfluenceCondition {
    private SettingsManager settingsManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        Settings settings = this.settingsManager.getGlobalSettings();
        return !settings.isExternalUserManagement() && !settings.isDenyPublicSignup();
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

