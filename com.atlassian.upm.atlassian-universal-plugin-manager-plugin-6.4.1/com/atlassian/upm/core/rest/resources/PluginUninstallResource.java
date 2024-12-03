/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.PluginRestUninstaller;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.resources.install.BulkUninstallTask;
import com.atlassian.upm.core.token.TokenManager;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/uninstall")
public class PluginUninstallResource {
    private final AsynchronousTaskManager taskManager;
    private final BaseUriBuilder uriBuilder;
    private final PluginRetriever pluginRetriever;
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;
    private final PluginRestUninstaller uninstaller;
    private final UserManager userManager;
    private final TokenManager tokenManager;
    private final BasePluginRepresentationFactory representationFactory;

    public PluginUninstallResource(AsynchronousTaskManager taskManager, BaseUriBuilder uriBuilder, PluginRetriever pluginRetriever, AsyncTaskRepresentationFactory taskRepresentationFactory, PluginRestUninstaller uninstaller, UserManager userManager, TokenManager tokenManager, BasePluginRepresentationFactory representationFactory) {
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.uninstaller = Objects.requireNonNull(uninstaller, "uninstaller");
        this.userManager = userManager;
        this.tokenManager = tokenManager;
        this.representationFactory = representationFactory;
    }

    @POST
    @Consumes(value={"application/x-www-form-urlencoded"})
    public Response bulkUninstall(@FormParam(value="pluginKey") List<String> pluginKeys, @FormParam(value="token") String token) {
        boolean valid = this.tokenManager.attemptToMatchAndInvalidateToken(this.userManager.getRemoteUserKey(), token);
        if (!valid) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).type("application/vnd.atl.plugins.error+json").entity((Object)this.representationFactory.createErrorRepresentation("upm.error.invalid.token")).build();
        }
        BulkUninstallTask task = new BulkUninstallTask(pluginKeys, this.pluginRetriever, this.uninstaller);
        AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task);
        return this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
    }
}

