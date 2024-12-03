/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources;

import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources.AbstractUrlReadingCondition;

public class EditorReportsPermittedUrlReadingCondition
extends AbstractUrlReadingCondition {
    private static final String EDITOR_REPORT_PERMITTED_QUERY_PARAM = "cefp_ed_perm";
    private final SettingsManager settingsManager;

    public EditorReportsPermittedUrlReadingCondition(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    protected String getQueryParamName() {
        return EDITOR_REPORT_PERMITTED_QUERY_PARAM;
    }

    @Override
    protected boolean getQueryParamValue() {
        return this.settingsManager.isEditorReportsEnabled();
    }
}

