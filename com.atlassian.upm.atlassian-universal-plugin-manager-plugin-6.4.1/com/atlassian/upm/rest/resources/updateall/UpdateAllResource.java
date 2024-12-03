/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.rest.resources.updateall;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.resources.UpmResources;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.token.TokenManager;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.resources.updateall.UpdateAllTask;
import com.atlassian.upm.schedule.UpmScheduler;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/updates/all")
public class UpdateAllResource {
    private final AsynchronousTaskManager taskManager;
    private final PacClient pacClient;
    private final PluginInstallationService pluginInstaller;
    private final UpmRepresentationFactory representationFactory;
    private final PluginRetriever pluginRetriever;
    private final PluginMetadataAccessor metadata;
    private final PluginDownloadService pluginDownloadService;
    private final PermissionEnforcer permissionEnforcer;
    private final AuditLogService auditLogger;
    private final UpmLinkBuilder linkBuilder;
    private final UpmUriBuilder uriBuilder;
    private final UserManager userManager;
    private final TokenManager tokenManager;
    private final PluginLicenseRepository licenseRepository;
    private final UpmScheduler upmScheduler;
    private final UpmInformation upm;
    private final SafeModeAccessor safeMode;
    private final RoleBasedLicensingPluginService roleBasedLicensingService;
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;
    private final HostLicenseInformation hostLicenseInformation;
    private final LicensingUsageVerifier licensingUsageVerifier;

    public UpdateAllResource(PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, AsynchronousTaskManager taskManager, PacClient pacClient, PluginDownloadService pluginDownloadService, PluginInstallationService pluginInstaller, UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer, AuditLogService auditLogger, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder, UserManager userManager, TokenManager tokenManager, PluginLicenseRepository licenseRepository, UpmScheduler upmScheduler, UpmInformation upm, SafeModeAccessor safeMode, RoleBasedLicensingPluginService roleBasedLicensingService, AsyncTaskRepresentationFactory taskRepresentationFactory, HostLicenseInformation hostLicenseInformation, LicensingUsageVerifier licensingUsageVerifier) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.tokenManager = Objects.requireNonNull(tokenManager, "tokenManager");
        this.linkBuilder = Objects.requireNonNull(linkBuilder, "linkBuilder");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.pluginDownloadService = Objects.requireNonNull(pluginDownloadService, "pluginDownloadService");
        this.pluginInstaller = Objects.requireNonNull(pluginInstaller, "pluginInstaller");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.upmScheduler = Objects.requireNonNull(upmScheduler, "upmScheduler");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.roleBasedLicensingService = Objects.requireNonNull(roleBasedLicensingService, "roleBasedLicensingService");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.hostLicenseInformation = Objects.requireNonNull(hostLicenseInformation, "hostLicenseInformation");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins.updateall+json"})
    public Response updateAll(@QueryParam(value="token") String token) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI);
        UpmResources.validateToken(token, this.userManager.getRemoteUserKey(), "application/json", this.tokenManager, this.representationFactory);
        if (this.safeMode.isSafeMode()) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.update.error.safe.mode")).type("application/vnd.atl.plugins.task.error+json").build();
        }
        UpdateAllTask task = new UpdateAllTask(this.pacClient, this.permissionEnforcer, this.pluginDownloadService, this.pluginRetriever, this.metadata, this.pluginInstaller, this.upmScheduler, this.auditLogger, this.linkBuilder, this.uriBuilder, this.licenseRepository, this.roleBasedLicensingService, this.upm, this.hostLicenseInformation, this.userManager.getRemoteUserKey(), this.licensingUsageVerifier);
        AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task);
        return this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
    }
}

