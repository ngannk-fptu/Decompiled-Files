/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.TransferLogEnablement
 *  com.atlassian.migration.app.dto.TransferLogResponse
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.rest.StartAppMigRequestDto;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.impl.AppMigrationDevelopmentService;
import com.atlassian.migration.agent.service.impl.AppRerunService;
import com.atlassian.migration.agent.service.impl.AppTransferLogService;
import com.atlassian.migration.app.dto.TransferLogEnablement;
import com.atlassian.migration.app.dto.TransferLogResponse;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;

@Path(value="app-migration")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class AppMigrationResource {
    private static final String TEXT_CSV = "text/csv";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final AppMigrationDevelopmentService appMigrationDevelopmentService;
    private final PreflightService preflightService;
    private final AppRerunService appRerunService;
    private final AppTransferLogService appTransferLogService;
    private static final Logger log = ContextLoggerFactory.getLogger(AppMigrationResource.class);

    @Inject
    public AppMigrationResource(AppMigrationDevelopmentService appMigrationDevelopmentService, PreflightService preflightService, AppRerunService appRerunService, AppTransferLogService appTransferLogService) {
        this.appMigrationDevelopmentService = appMigrationDevelopmentService;
        this.preflightService = preflightService;
        this.appRerunService = appRerunService;
        this.appTransferLogService = appTransferLogService;
    }

    @GET
    @Deprecated
    @Path(value="/rerun/{planId}")
    @Consumes(value={"*/*"})
    public Response startMigration(@PathParam(value="planId") String planId) {
        try {
            log.warn("You are using a deprecated endpoint. Please update your scripts to use GET /app-migration/trigger/{planId}");
            this.appMigrationDevelopmentService.rerunAppMigrationForPlan(planId);
        }
        catch (Exception e) {
            log.warn("Exception:", (Throwable)e);
            return Response.serverError().build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path(value="/trigger/{planId}")
    @Consumes(value={"*/*"})
    public Response triggerAppMigration(@PathParam(value="planId") String planId) {
        try {
            this.appMigrationDevelopmentService.rerunAppMigrationForPlan(planId);
        }
        catch (Exception e) {
            log.warn("Exception:", (Throwable)e);
            return Response.serverError().build();
        }
        return Response.noContent().build();
    }

    @POST
    @Path(value="/trigger/{planId}")
    @Consumes(value={"*/*"})
    public Response startMigration(@PathParam(value="planId") String planId, StartAppMigRequestDto request) {
        this.appMigrationDevelopmentService.rerunAppMigrationForPlan(planId, request.includeServerAppKeys);
        return Response.noContent().build();
    }

    @GET
    @Path(value="/check/{planId}/run/{appKey}")
    public Response executeAppOwnedPreflightCheck(@PathParam(value="appKey") String serverAppKey, @PathParam(value="planId") String planId) {
        try {
            this.preflightService.executeAllAppVendorChecksForApp(planId, serverAppKey);
        }
        catch (Exception e) {
            log.warn("Exception:", (Throwable)e);
            return Response.serverError().build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path(value="/rerun/{planId}/{containerId}/enabled")
    public Response isRerunEnabled(@PathParam(value="containerId") String containerId, @PathParam(value="planId") String planId, @QueryParam(value="serverAppKey") String serverAppKey) {
        try {
            return Response.ok((Object)this.appRerunService.isRerunEnabled(planId, containerId, serverAppKey)).build();
        }
        catch (Exception e) {
            log.warn("Exception:", (Throwable)e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path(value="/rerun/{planId}/{containerId}")
    public void rerunAppMigration(@PathParam(value="containerId") String containerId, @PathParam(value="planId") String planId, @QueryParam(value="serverAppKey") String serverAppKey) {
        this.appRerunService.rerunAppMigration(planId, containerId, serverAppKey);
    }

    @GET
    @Path(value="/transfer/log/csv")
    public Response getTransferLogCsv(@QueryParam(value="planId") String planId, @QueryParam(value="containerId") String containerId) {
        TransferLogResponse response = this.appTransferLogService.getTransferLogResponse(planId, containerId);
        return Response.ok((Object)response.getCsv()).type(TEXT_CSV).header(CONTENT_DISPOSITION, (Object)response.getContentDispositionHeader()).build();
    }

    @GET
    @Path(value="/transfer/log/enabled")
    public Response getTransferLogEnabled(@QueryParam(value="planId") String planId, @QueryParam(value="containerId") String containerId) {
        TransferLogEnablement response = this.appTransferLogService.isTransferLogsEnabled(planId, containerId);
        return Response.ok((Object)response).build();
    }
}

