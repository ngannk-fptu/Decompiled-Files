/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.service.WelcomeMessageService
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.dashboard;

import com.atlassian.confluence.content.service.WelcomeMessageService;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.confluence.plugins.dashboard.DefaultDashboardContext;
import com.atlassian.confluence.plugins.dashboard.service.OnboardingService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class DashboardContextProvider
implements ContextProvider {
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final HttpContext httpContext;
    private final ConfluenceWebResourceManager confluenceWebResourceManager;
    private final OnboardingService onboardingService;
    private final DefaultDashboardContext dashboardSharedContext;

    public DashboardContextProvider(WelcomeMessageService welcomeMessageService, PageTemplateManager pageTemplateManager, PermissionManager permissionManager, WebResourceUrlProvider webResourceUrlProvider, HttpContext httpContext, ConfluenceWebResourceManager confluenceWebResourceManager, OnboardingService onboardingService, DarkFeaturesManager darkFeaturesManager, SpaceManager spaceManager, ConfluenceAccessManager confluenceAccessManager, WebResourceDependenciesRecorder webResourceDependenciesRecorder) {
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.httpContext = httpContext;
        this.confluenceWebResourceManager = confluenceWebResourceManager;
        this.onboardingService = onboardingService;
        this.dashboardSharedContext = new DefaultDashboardContext(confluenceWebResourceManager, darkFeaturesManager, welcomeMessageService, pageTemplateManager, permissionManager, spaceManager, confluenceAccessManager, webResourceDependenciesRecorder);
    }

    public void init(Map<String, String> ignored) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        if (this.dashboardSharedContext.showOnboarding()) {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            this.confluenceWebResourceManager.putMetadata("is-new-user", Boolean.toString(this.onboardingService.isNewUser(user)));
            this.confluenceWebResourceManager.putMetadata("show-dashboard-onboarding-dialog", Boolean.toString(this.onboardingService.shouldShowDialog(user)));
            this.confluenceWebResourceManager.putMetadata("show-dashboard-onboarding-tips", Boolean.toString(this.onboardingService.shouldShowTips(user)));
        }
        this.confluenceWebResourceManager.putMetadata("user-can-create-content", Boolean.toString(this.dashboardSharedContext.shouldDisplayCreateButton()));
        context.put("welcomeMessageHtml", this.dashboardSharedContext.getWelcomeMessage());
        context.put("welcomeMessageEditUrl", this.dashboardSharedContext.getEditWelcomePageUrl());
        context.put("showWelcomeMessageEditHint", this.dashboardSharedContext.shouldShowWelcomeMessageEditLink());
        context.put("showEditButton", this.dashboardSharedContext.shouldShowEditButton());
        context.put("isAnonymous", AuthenticatedUserThreadLocal.isAnonymousUser());
        context.put("baseUrl", this.webResourceUrlProvider.getBaseUrl());
        HttpServletRequest request = this.httpContext.getRequest();
        Map panelContext = request == null ? Collections.emptyMap() : request.getParameterMap();
        context.put("panelContext", panelContext);
        return context;
    }
}

