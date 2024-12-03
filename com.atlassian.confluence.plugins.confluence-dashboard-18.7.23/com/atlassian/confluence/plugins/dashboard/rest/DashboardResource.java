/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder$RecordedResources
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Pair
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.dashboard.rest;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.confluence.plugins.dashboard.DashboardContext;
import com.atlassian.confluence.plugins.dashboard.service.OnboardingService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Pair;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/")
@AnonymousAllowed
public class DashboardResource {
    private final OnboardingService onboardingService;
    private final DashboardContext dashboardContext;

    public DashboardResource(OnboardingService onboardingService, DashboardContext dashboardContext) {
        this.onboardingService = onboardingService;
        this.dashboardContext = dashboardContext;
    }

    @GET
    @Path(value="/dashboard-view-model")
    @Produces(value={"application/json"})
    public Response getDashboardViewModel() {
        return Response.ok((Object)this.getDashboardViewModelObject().toString()).build();
    }

    JSONObject getDashboardViewModelObject() throws PermissionException {
        JSONObject data = new JSONObject();
        boolean isAnonymous = AuthenticatedUserThreadLocal.isAnonymousUser();
        if (isAnonymous && !this.dashboardContext.visibleToAnonymousUsers()) {
            throw new PermissionException("The dashboard template cannot be viewed by anonymous users");
        }
        if (this.dashboardContext.showOnboarding()) {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            data.put("isNewUser", this.onboardingService.isNewUser(user));
            data.put("showDashboardOnboardingDialog", this.onboardingService.shouldShowDialog(user));
            data.put("showDashboardOnboardingTips", this.onboardingService.shouldShowTips(user));
        }
        data.put("userCanCreateContent", this.dashboardContext.shouldDisplayCreateButton());
        data.put("welcomeMessageEditUrl", (Object)this.dashboardContext.getEditWelcomePageUrl());
        Pair<String, WebResourceDependenciesRecorder.RecordedResources> welcomeMessageWithResources = this.dashboardContext.getWelcomeMessageWithResources();
        data.put("welcomeMessageHtml", welcomeMessageWithResources.left());
        data.put("resourceKeys", (Object)((Collection)((WebResourceDependenciesRecorder.RecordedResources)welcomeMessageWithResources.right()).resourceKeys()));
        data.put("resourceContexts", (Object)((Collection)((WebResourceDependenciesRecorder.RecordedResources)welcomeMessageWithResources.right()).contexts()));
        data.put("showWelcomeMessageEditHint", this.dashboardContext.shouldShowWelcomeMessageEditLink());
        data.put("showEditButton", this.dashboardContext.shouldShowEditButton());
        data.put("isAnonymous", isAnonymous);
        return data;
    }
}

