/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.ImmutableSet
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.internal.integration.jira.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.integration.jira.JiraIssuesRequest;
import com.atlassian.internal.integration.jira.InternalJiraService;
import com.atlassian.internal.integration.jira.request.MyAssignedJiraIssuesRequest;
import com.atlassian.internal.integration.jira.rest.RestUtils;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Path(value="/issues")
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class JiraResource {
    private final InternalJiraService jiraService;

    public JiraResource(InternalJiraService jiraService) {
        this.jiraService = jiraService;
    }

    @POST
    public Response createIssues(@Nonnull @QueryParam(value="applicationId") ApplicationId id, String createIssueRequestJson) {
        return RestUtils.ok(this.jiraService.createIssues(id, createIssueRequestJson)).build();
    }

    @GET
    public Response getDetailsForIssueKeys(@QueryParam(value="issueKey") Set<String> issueKeys, @QueryParam(value="entityKey") String entityKey, @QueryParam(value="fields") String fields, @QueryParam(value="minimum") @DefaultValue(value="0") int minimum, @QueryParam(value="showErrors") boolean showErrors) {
        JiraIssuesRequest request = ((JiraIssuesRequest.Builder)((JiraIssuesRequest.Builder)new JiraIssuesRequest.Builder().entityKey(entityKey)).fields(JiraResource.parseFields(fields)).issueKeys(issueKeys)).minimum(minimum).showErrors(showErrors).build();
        return RestUtils.ok(this.jiraService.getIssuesAsJson(request)).build();
    }

    @GET
    @Path(value="{issueKey}/transitions")
    public Response getTransitions(@QueryParam(value="applicationId") ApplicationId id, @PathParam(value="issueKey") String issueKey) {
        return RestUtils.ok(this.jiraService.getIssueTransitionsAsJson(issueKey, id)).build();
    }

    @POST
    @Path(value="{issueKey}/transitions")
    public Response transition(@QueryParam(value="applicationId") ApplicationId id, @QueryParam(value="fields") String fields, @PathParam(value="issueKey") String issueKey, String transitionJson) {
        return RestUtils.ok(this.jiraService.transitionIssue(issueKey, JiraResource.parseFields(fields), id, transitionJson)).build();
    }

    @GET
    @Path(value="assignedToMe")
    public Response getIssuesAssignedToMe(@QueryParam(value="applicationId") ApplicationId applicationId, @QueryParam(value="fields") String fields, @QueryParam(value="maxResults") @DefaultValue(value="50") int maxResults, @QueryParam(value="startAt") @DefaultValue(value="0") int startAt) {
        MyAssignedJiraIssuesRequest request = ((MyAssignedJiraIssuesRequest.Builder)((MyAssignedJiraIssuesRequest.Builder)new MyAssignedJiraIssuesRequest.Builder(applicationId).fields(JiraResource.parseFields(fields)).maxResults(maxResults)).startAt(startAt)).build();
        return RestUtils.ok(this.jiraService.getMyAssignedIssues(request)).build();
    }

    @GET
    @Path(value="findValidIssues")
    public Response findValidIssues(@QueryParam(value="applicationId") ApplicationId applicationId, @QueryParam(value="issueKeys") Set<String> issueKeys) {
        return RestUtils.ok(this.jiraService.findValidIssues(issueKeys, applicationId)).build();
    }

    private static Set<String> parseFields(String fields) {
        return ImmutableSet.copyOf((Object[])fields.split(","));
    }
}

