/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.core.rest.async;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.async.AsynchronousTaskStatusStore;
import com.atlassian.upm.core.async.CancellableAsyncTask;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskCollectionRepresentation;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskRepresentation;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/pending")
public class LegacyAsyncTaskResource {
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;
    private final AsynchronousTaskManager taskManager;
    private final BaseUriBuilder uriBuilder;
    private final PermissionEnforcer permissionEnforcer;
    private final AsynchronousTaskStatusStore statusStore;

    public LegacyAsyncTaskResource(AsyncTaskRepresentationFactory taskRepresentationFactory, AsynchronousTaskManager taskManager, AsynchronousTaskStatusStore statusStore, BaseUriBuilder uriBuilder, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "upmUriBuilder");
        this.statusStore = Objects.requireNonNull(statusStore, "statusStore");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.pending-tasks+json"})
    public Response getTasksCollection() {
        this.permissionEnforcer.enforceAdmin();
        LegacyAsyncTaskCollectionRepresentation result = this.taskRepresentationFactory.createLegacyAsyncTaskCollectionRepresentation(this.statusStore.getOngoingTasks());
        return Response.ok().entity((Object)result).build();
    }

    @GET
    @Path(value="{taskId}")
    public Response getTask(@PathParam(value="taskId") String taskId) {
        this.permissionEnforcer.enforceAdmin();
        Iterator<AsyncTaskInfo> iterator = this.statusStore.getTask(taskId).iterator();
        if (iterator.hasNext()) {
            AsyncTaskInfo taskInfo = iterator.next();
            LegacyAsyncTaskRepresentation representation = this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo);
            if (taskInfo.getStatus().isDone()) {
                return this.done(representation, taskInfo.getStatus());
            }
            return representation.toResponse();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @POST
    @XsrfProtectionExcluded
    public Response createCancellableTask() {
        this.permissionEnforcer.enforceAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        CancellableAsyncTask task = new CancellableAsyncTask();
        AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task, Option.some(task.getCanceller()));
        return this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
    }

    @DELETE
    @Path(value="{taskId}")
    public Response cancelCancellableTask(@PathParam(value="taskId") String taskId) {
        this.permissionEnforcer.enforceAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        Iterator<Object> iterator = this.taskManager.getTaskCanceller(taskId).iterator();
        if (iterator.hasNext()) {
            Runnable canceller = iterator.next();
            canceller.run();
            return Response.status((Response.Status)Response.Status.OK).build();
        }
        for (AsyncTaskInfo taskInfo : this.statusStore.getTask(taskId)) {
            if (taskInfo.isCancellable() && !taskInfo.getStatus().isDone()) continue;
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private Response done(LegacyAsyncTaskRepresentation representation, AsyncTaskStatus status) {
        Iterator<URI> iterator = status.getResultUri().iterator();
        if (iterator.hasNext()) {
            URI uri = iterator.next();
            return Response.seeOther((URI)this.uriBuilder.makeAbsolute(uri)).build();
        }
        return representation.toResponse();
    }
}

