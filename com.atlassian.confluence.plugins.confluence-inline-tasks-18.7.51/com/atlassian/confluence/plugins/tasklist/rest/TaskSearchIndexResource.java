/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexQueueProcessor;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexTaskRegistrator;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/search-index")
@Consumes(value={"application/json"})
public class TaskSearchIndexResource {
    private final PermissionManager permissionManager;
    private final IndexQueueProcessor indexQueueProcessor;
    private final IndexTaskRegistrator indexTaskRegistrator;

    public TaskSearchIndexResource(PermissionManager permissionManager, IndexQueueProcessor indexQueueProcessor, IndexTaskRegistrator indexTaskRegistrator) {
        this.permissionManager = permissionManager;
        this.indexQueueProcessor = indexQueueProcessor;
        this.indexTaskRegistrator = indexTaskRegistrator;
    }

    @POST
    @Path(value="/flush")
    @Consumes(value={"application/json"})
    @Produces(value={"application/xml", "application/json"})
    public Response flushIndex() {
        if (!this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.indexQueueProcessor.flushQueue();
        return Response.status((Response.Status)Response.Status.OK).build();
    }

    @POST
    @Path(value="/reindex-all")
    @Consumes(value={"application/json"})
    @Produces(value={"application/xml", "application/json"})
    public Response reIndexAll() {
        if (!this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.indexTaskRegistrator.requestToReindexAllInlineTasks();
        return Response.status((Response.Status)Response.Status.OK).build();
    }
}

