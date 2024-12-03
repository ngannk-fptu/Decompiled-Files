/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.rest.AbstractTaskResource;
import com.atlassian.confluence.plugins.tasklist.rest.TaskStatusUpdate;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path(value="/task")
@AnonymousAllowed
@Component
public class TaskResource
extends AbstractTaskResource {
    private static final String CONTENT_LOCK_PREFIX = "TaskResource";
    private final PageManager pageManager;
    private final InlineTaskService inlineTaskService;

    private TaskResource() {
        this.pageManager = null;
        this.inlineTaskService = null;
    }

    @Autowired
    public TaskResource(UserAccessor userAccessor, SpacePermissionManager spm, PageManager pageManager, InlineTaskService inlineTaskService) {
        super(userAccessor, spm);
        this.pageManager = pageManager;
        this.inlineTaskService = inlineTaskService;
    }

    @GET
    @Produces(value={"application/xml"})
    public Response doDefault() {
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Response setTaskStatus(long cid, String taskId, String status) {
        String stripedContentLock;
        String string = stripedContentLock = this.createStripedLockForContent(cid);
        synchronized (string) {
            return this.setTaskStatus(cid, taskId, new TaskStatusUpdate(status, "UNKNOWN"));
        }
    }

    private String createStripedLockForContent(long cid) {
        return (CONTENT_LOCK_PREFIX + cid).intern();
    }

    @POST
    @Path(value="/{contentId}/{taskId}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/xml", "application/json"})
    public Response setTaskStatus(@PathParam(value="contentId") long cid, @PathParam(value="taskId") String taskId, TaskStatusUpdate request) {
        PageUpdateTrigger decodedTrigger;
        TaskStatus decodedStatus;
        this.createRequestContext();
        ContentEntityObject cob = this.pageManager.getById(cid);
        if (request == null || request.getStatus() == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Invalid status").build();
        }
        try {
            decodedStatus = TaskStatus.valueOf(request.getStatus());
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Invalid status").build();
        }
        try {
            decodedTrigger = request.getTrigger() == null ? PageUpdateTrigger.UNKNOWN : PageUpdateTrigger.valueOf((String)request.getTrigger());
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Invalid trigger").build();
        }
        try {
            switch (this.inlineTaskService.setTaskStatus(cob, taskId, decodedStatus, decodedTrigger)) {
                case SUCCESS: {
                    return Response.status((Response.Status)Response.Status.OK).build();
                }
                case TASK_NOT_FOUND: {
                    return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Specified task not found").build();
                }
                case MERGE_CONFLICT: {
                    return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)"Merge conflict while attempting to set status").build();
                }
            }
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)"Received an unexpected response code from the Inline Task Service").build();
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)e.getMessage()).build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
    }
}

