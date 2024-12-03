/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder$RecordedResources
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.plugins.dashboard;

import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.fugue.Pair;

public interface DashboardContext {
    public String getWelcomeMessage();

    public String getEditWelcomePageUrl();

    public boolean customPageTemplateExists();

    public boolean shouldShowWelcomeMessageEditLink();

    public boolean shouldShowEditButton();

    public boolean showOnboarding();

    public boolean shouldDisplayCreateButton();

    public String getJsResources();

    public String getCssResources();

    public boolean visibleToAnonymousUsers();

    public Pair<String, WebResourceDependenciesRecorder.RecordedResources> getWelcomeMessageWithResources();
}

