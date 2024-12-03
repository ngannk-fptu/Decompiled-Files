/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources;

import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources.AbstractUrlReadingCondition;

public class CollaborativeEditingEnabledUrlReadingCondition
extends AbstractUrlReadingCondition {
    private static final String COLLAB_EDITING_ENABLED_QUERY_PARAM = "cefp_collab_enabled";
    private final SettingsManager settingsManager;

    public CollaborativeEditingEnabledUrlReadingCondition(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    protected String getQueryParamName() {
        return COLLAB_EDITING_ENABLED_QUERY_PARAM;
    }

    @Override
    protected boolean getQueryParamValue() {
        return this.settingsManager.collaborativeEditingEnabled();
    }
}

