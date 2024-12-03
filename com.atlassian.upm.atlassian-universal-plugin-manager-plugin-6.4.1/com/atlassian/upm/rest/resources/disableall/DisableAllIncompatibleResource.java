/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources.disableall;

import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.resources.disableall.DisableAllIncompatibleTask;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/incompatible/disable-all")
public class DisableAllIncompatibleResource {
    private final AsynchronousTaskManager taskManager;
    private final BaseUriBuilder uriBuilder;
    private final PacClient pacClient;
    private final PluginEnablementService enabler;
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;
    private final UpmInformation upm;
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;

    public DisableAllIncompatibleResource(AsynchronousTaskManager taskManager, AsyncTaskRepresentationFactory taskRepresentationFactory, BaseUriBuilder uriBuilder, PacClient pacClient, PluginEnablementService enabler, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, UpmInformation upm) {
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.enabler = Objects.requireNonNull(enabler, "enabler");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.upm = Objects.requireNonNull(upm, "upm");
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins.disableall+json"})
    public Response disableAll() {
        this.permissionEnforcer.enforcePermission(Permission.DISABLE_ALL_USER_INSTALLED);
        DisableAllIncompatibleTask task = new DisableAllIncompatibleTask(this.pacClient, this.enabler, this.pluginRetriever, this.upm);
        AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task);
        return this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
    }
}

