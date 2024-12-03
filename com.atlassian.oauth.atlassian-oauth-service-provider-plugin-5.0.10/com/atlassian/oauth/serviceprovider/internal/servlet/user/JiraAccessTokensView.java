/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.plugin.profile.OptionalUserProfilePanel
 *  com.atlassian.jira.plugin.profile.ViewProfilePanel
 *  com.atlassian.jira.plugin.profile.ViewProfilePanelModuleDescriptor
 *  com.atlassian.jira.user.ApplicationUser
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.web.context.HttpContext
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.jira.plugin.profile.OptionalUserProfilePanel;
import com.atlassian.jira.plugin.profile.ViewProfilePanel;
import com.atlassian.jira.plugin.profile.ViewProfilePanelModuleDescriptor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletContext;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletValidation;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.web.context.HttpContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JiraAccessTokensView
implements ViewProfilePanel,
OptionalUserProfilePanel {
    private final AccessTokensServletContext accessTokensServletContext;
    private final AccessTokensServletValidation accessTokensServletValidation;
    private final HttpContext httpContext;
    private final ApplicationProperties applicationProperties;
    private ViewProfilePanelModuleDescriptor moduleDescriptor;

    public JiraAccessTokensView(AccessTokensServletContext accessTokensServletContext, AccessTokensServletValidation accessTokensServletValidation, HttpContext httpContext, ApplicationProperties applicationProperties) {
        this.accessTokensServletContext = Objects.requireNonNull(accessTokensServletContext, "accessTokensServletContext");
        this.accessTokensServletValidation = Objects.requireNonNull(accessTokensServletValidation, "accessTokensServletValidation");
        this.httpContext = Objects.requireNonNull(httpContext, "httpContext");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    public void init(ViewProfilePanelModuleDescriptor viewProfilePanelModuleDescriptor) {
        this.moduleDescriptor = viewProfilePanelModuleDescriptor;
    }

    public String getHtml(ApplicationUser applicationUser) {
        this.accessTokensServletValidation.validate(this.httpContext.getRequest());
        return this.moduleDescriptor.getHtml("view", this.getContext(applicationUser));
    }

    private Map<String, Object> getContext(ApplicationUser applicationUser) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.putAll(this.accessTokensServletContext.getContext(applicationUser.getUsername()));
        context.put("applicationProperties", this.applicationProperties);
        return context;
    }

    public boolean showPanel(ApplicationUser profileUser, ApplicationUser currentUser) {
        return profileUser.equals((Object)currentUser);
    }

    public ApplicationProperties getApplicationProperties() {
        return this.applicationProperties;
    }
}

