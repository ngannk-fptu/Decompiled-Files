/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.internal.integration.jira.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.internal.integration.jira.InternalJiraService;
import com.atlassian.internal.integration.jira.rest.RestUtils;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Path(value="/servers")
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class JiraServerResource {
    private final InternalJiraService jiraService;

    public JiraServerResource(InternalJiraService jiraService) {
        this.jiraService = jiraService;
    }

    @GET
    @Path(value="/{applicationId}/projects/{project}/issue-types/{issueType}/fields-meta")
    public Response getIssueTypeMeta(@PathParam(value="applicationId") ApplicationId id, @PathParam(value="project") String project, @PathParam(value="issueType") int issueType) {
        return RestUtils.ok(this.jiraService.getIssueTypeMetaAsJson(id, project, issueType)).build();
    }

    @GET
    @Path(value="/{applicationId}/projects/{project}/issue-types")
    public Response getIssueTypes(@PathParam(value="applicationId") ApplicationId id, @PathParam(value="project") String project) {
        return RestUtils.ok(this.jiraService.getIssueTypesAsJson(id, project)).build();
    }

    @GET
    @Path(value="/{applicationId}/projects")
    public Response getProjectsMeta(@PathParam(value="applicationId") ApplicationId id) {
        return RestUtils.ok(this.jiraService.getProjectsAsJson(id)).build();
    }

    @GET
    public Response getServers() {
        return RestUtils.ok(this.jiraService.getServersAsJson()).build();
    }

    @GET
    @Path(value="/{applicationId}/features")
    public Response getFeatures(@PathParam(value="applicationId") ApplicationId id) {
        return RestUtils.ok(this.jiraService.getSupportedFeatures(id)).build();
    }
}

