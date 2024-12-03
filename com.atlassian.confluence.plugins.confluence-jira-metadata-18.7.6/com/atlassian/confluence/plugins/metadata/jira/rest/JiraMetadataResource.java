/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.metadata.jira.rest;

import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataService;
import com.atlassian.confluence.plugins.metadata.jira.util.GlobalPageIdUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="metadata")
@Produces(value={"application/json"})
public class JiraMetadataResource {
    private JiraMetadataService jiraMetadataService;

    public JiraMetadataResource(JiraMetadataService jiraMetadataService) {
        this.jiraMetadataService = jiraMetadataService;
    }

    @AnonymousAllowed
    @GET
    public Response getMetadata(@QueryParam(value="pageId") long pageId) {
        return Response.ok((Object)this.jiraMetadataService.getMetadata(pageId)).build();
    }

    @AnonymousAllowed
    @GET
    @Path(value="/aggregate")
    public Response getAggregateData(@QueryParam(value="pageId") long pageId) {
        return Response.ok((Object)this.jiraMetadataService.getAggregateData(pageId)).build();
    }

    @DELETE
    @Path(value="/cache")
    @AnonymousAllowed
    public Response invalidateCachedAggregateData(@QueryParam(value="globalId") String globalId) {
        Long pageId = GlobalPageIdUtil.getPageId(globalId);
        if (pageId == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        this.jiraMetadataService.invalidateCachedAggregateData(pageId);
        return Response.ok().build();
    }
}

