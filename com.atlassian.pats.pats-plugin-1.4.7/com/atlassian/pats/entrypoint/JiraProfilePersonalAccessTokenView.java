/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.plugin.profile.OptionalUserProfilePanel
 *  com.atlassian.jira.plugin.profile.ViewProfilePanel
 *  com.atlassian.jira.plugin.profile.ViewProfilePanelModuleDescriptor
 *  com.atlassian.jira.user.ApplicationUser
 */
package com.atlassian.pats.entrypoint;

import com.atlassian.jira.plugin.profile.OptionalUserProfilePanel;
import com.atlassian.jira.plugin.profile.ViewProfilePanel;
import com.atlassian.jira.plugin.profile.ViewProfilePanelModuleDescriptor;
import com.atlassian.jira.user.ApplicationUser;
import java.util.Collections;

public class JiraProfilePersonalAccessTokenView
implements ViewProfilePanel,
OptionalUserProfilePanel {
    private ViewProfilePanelModuleDescriptor moduleDescriptor;

    public void init(ViewProfilePanelModuleDescriptor viewProfilePanelModuleDescriptor) {
        this.moduleDescriptor = viewProfilePanelModuleDescriptor;
    }

    public String getHtml(ApplicationUser applicationUser) {
        return this.moduleDescriptor.getHtml("view", Collections.emptyMap());
    }

    public boolean showPanel(ApplicationUser profileUser, ApplicationUser currentUser) {
        return profileUser.equals((Object)currentUser);
    }
}

