/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.conluenceview.rest;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.conluenceview.query.ConfluencePagesQuery;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.ConfluencePagesDto;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.LinkedSpacesDto;
import com.atlassian.confluence.plugins.conluenceview.rest.params.PagesSearchParam;
import com.atlassian.confluence.plugins.conluenceview.services.ConfluenceJiraLinksService;
import com.atlassian.confluence.plugins.conluenceview.services.ConfluencePagesService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/confluence-view-in-jira")
@ReadOnlyAccessAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@AnonymousAllowed
public class ConfluenceInJiraViewResource {
    private final ConfluencePagesService confluencePagesService;
    private final ConfluenceJiraLinksService confluenceJiraLinksService;
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final PermissionManager permissionManager;

    public ConfluenceInJiraViewResource(ConfluencePagesService confluencePagesService, ConfluenceJiraLinksService confluenceJiraLinksService, ReadOnlyApplicationLinkService readOnlyApplicationLinkService, PermissionManager permissionManager) {
        this.confluencePagesService = confluencePagesService;
        this.confluenceJiraLinksService = confluenceJiraLinksService;
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.permissionManager = permissionManager;
    }

    @POST
    @Path(value="/pages/search")
    public Response getPagesByIds(PagesSearchParam param) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        ConfluencePagesDto result = this.confluencePagesService.getPagesByIds(ConfluencePagesQuery.newBuilder().withCacheToken(param.getCacheToken()).withPageIds(param.getPageIds()).withSearchString(param.getSearchString()).withLimit(param.getLimit()).withStart(param.getStart()).build());
        return Response.ok((Object)result).build();
    }

    @GET
    @Path(value="/{spaceKey}/pages")
    public Response getPagesInSpace(@PathParam(value="spaceKey") String spaceKey, @QueryParam(value="start") int start, @QueryParam(value="limit") int limit) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        ConfluencePagesDto result = this.confluencePagesService.getPagesInSpace(ConfluencePagesQuery.newBuilder().withSpaceKey(spaceKey).withLimit(limit).withStart(start).build());
        return Response.ok((Object)result).build();
    }

    @GET
    @Path(value="/od-application-link-id")
    public Response getODApplicationId() {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return Response.ok((Object)this.confluenceJiraLinksService.getODApplicationLinkId()).build();
    }

    @GET
    @Path(value="/jira-applink-id")
    public Response getJIRAApplinkId(@QueryParam(value="jiraUrl") String jiraUrl) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        String appLinkId = "";
        Iterable appLinks = this.readOnlyApplicationLinkService.getApplicationLinks(JiraApplicationType.class);
        for (ReadOnlyApplicationLink appLink : appLinks) {
            if (!jiraUrl.startsWith(appLink.getRpcUrl().toString()) && !jiraUrl.startsWith(appLink.getDisplayUrl().toString())) continue;
            appLinkId = appLink.getId().toString();
        }
        return Response.ok((Object)appLinkId).build();
    }

    @GET
    @Path(value="/linked-spaces")
    public Response getLinkedSpace(@QueryParam(value="jiraUrl") String jiraUrl, @QueryParam(value="projectKey") String projectKey) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return Response.ok((Object)LinkedSpacesDto.newBuilder().withSpaces(this.confluenceJiraLinksService.getLinkedSpaces(jiraUrl, projectKey)).build()).build();
    }
}

