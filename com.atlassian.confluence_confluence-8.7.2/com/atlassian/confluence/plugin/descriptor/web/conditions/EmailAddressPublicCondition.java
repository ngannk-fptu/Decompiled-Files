/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class EmailAddressPublicCondition
extends BaseConfluenceCondition {
    SettingsManager settingsManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return "email.address.public".equals(this.settingsManager.getGlobalSettings().getEmailAddressVisibility());
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

