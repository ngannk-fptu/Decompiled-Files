/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.jira.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.util.ResponseUtil;
import com.atlassian.confluence.plugins.jira.beans.JiraIssueBean;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/jira-issue")
public class CreateJiraIssueResource {
    private final ReadOnlyApplicationLinkService appLinkService;
    private final JiraIssuesManager jiraIssuesManager;
    private final PermissionManager permissionManager;

    public CreateJiraIssueResource(ReadOnlyApplicationLinkService appLinkService, JiraIssuesManager jiraIssuesManager, PermissionManager permissionManager) {
        this.appLinkService = appLinkService;
        this.jiraIssuesManager = jiraIssuesManager;
        this.permissionManager = permissionManager;
    }

    @POST
    @Path(value="create-jira-issues/{appLinkId}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response createJiraIssues(@PathParam(value="appLinkId") String appLinkId, List<JiraIssueBean> jiraIssueBeans) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        try {
            ReadOnlyApplicationLink appLink = this.appLinkService.getApplicationLink(new ApplicationId(appLinkId));
            List<JiraIssueBean> resultJiraIssueBeans = this.jiraIssuesManager.createIssues(jiraIssueBeans, appLink);
            Predicate jiraIssueSuccess = jiraIssueBean -> jiraIssueBean.getErrors() == null || jiraIssueBean.getErrors().isEmpty();
            if (Collections2.filter(resultJiraIssueBeans, (Predicate)jiraIssueSuccess).isEmpty()) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity(resultJiraIssueBeans).build();
            }
            return Response.ok(resultJiraIssueBeans).build();
        }
        catch (CredentialsRequiredException e) {
            String authorisationURI = e.getAuthorisationURI().toString();
            return ResponseUtil.buildUnauthorizedResponse(authorisationURI);
        }
        catch (ResponseException re) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)re.getMessage()).build();
        }
    }
}

