/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
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

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.async.AsynchronousTaskStatusStore;
import com.atlassian.upm.core.async.CancellableAsyncTask;
import com.atlassian.upm.core.rest.async.AsyncTaskCollectionRepresentation;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/tasks")
@AnonymousAllowed
@WebSudoNotRequired
public class AsyncTaskResource {
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;
    private final AsynchronousTaskManager taskManager;
    private final PermissionEnforcer permissionEnforcer;
    private final AsynchronousTaskStatusStore statusStore;
    private final UserManager userManager;

    public AsyncTaskResource(AsyncTaskRepresentationFactory taskRepresentationFactory, AsynchronousTaskManager taskManager, AsynchronousTaskStatusStore statusStore, PermissionEnforcer permissionEnforcer, UserManager userManager) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.statusStore = Objects.requireNonNull(statusStore, "statusStore");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getTasksCollection() {
        AsyncTaskCollectionRepresentation result = this.taskRepresentationFactory.createAsyncTaskCollectionRepresentation(this.statusStore.getOngoingTasks(), this.isAdminUser());
        return Response.ok().entity((Object)result).build();
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="{taskId}")
    public Response getTask(@PathParam(value="taskId") String taskId) {
        Iterator<AsyncTaskInfo> iterator = this.statusStore.getTask(taskId).iterator();
        if (iterator.hasNext()) {
            AsyncTaskInfo taskInfo = iterator.next();
            return this.taskRepresentationFactory.createAsyncTaskRepresentation(taskInfo, this.isAdminUser()).toResponse();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private boolean isAdminUser() {
        UserKey u = this.userManager.getRemoteUserKey();
        return u != null && this.userManager.isAdmin(u);
    }

    @DELETE
    public Response clearOngoingTasks() {
        this.permissionEnforcer.enforceSystemAdmin();
        this.statusStore.clearOngoingTasks();
        return Response.noContent().build();
    }

    @POST
    public Response createCancellableTask() {
        this.permissionEnforcer.enforceAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        CancellableAsyncTask task = new CancellableAsyncTask();
        AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task, Option.some(task.getCanceller()));
        return this.taskRepresentationFactory.createAsyncTaskRepresentation(taskInfo, true).toResponse();
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
}

