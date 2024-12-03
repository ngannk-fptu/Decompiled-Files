/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.test.rest.resources.async;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.test.rest.resources.async.IncrementationTestTask;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/test/incrementation")
public class IncrementationTestTaskResource {
    private final AsynchronousTaskManager taskManager;
    private final BaseUriBuilder uriBuilder;
    private final PermissionEnforcer permissionEnforcer;
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;

    public IncrementationTestTaskResource(AsynchronousTaskManager taskManager, PermissionEnforcer permissionEnforcer, AsyncTaskRepresentationFactory taskRepresentationFactory, BaseUriBuilder uriBuilder) {
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response disableAll() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        IncrementationTestTask task = new IncrementationTestTask();
        AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task, Option.some(task.getCanceller()));
        return this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
    }
}

