/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;

public class EditorReportsPermittedCondition
extends BaseConfluenceCondition {
    private final SettingsManager settingsManager;

    public EditorReportsPermittedCondition(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return this.settingsManager.isEditorReportsEnabled();
    }
}

