/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class ThreadedCommentsCondition
extends BaseConfluenceCondition {
    private SettingsManager settingsManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.settingsManager.getGlobalSettings().isAllowThreadedComments();
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

