/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.cache.rest.v1;

import com.atlassian.audit.cache.schedule.BuildCacheJobScheduler;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.scheduler.SchedulerServiceException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenAPIDefinition(info=@Info(title="Audit feature cache management", version="1.0.0", description="API to manage caches the audit feature uses internally. The root path is /rest/auditing/1.0"))
@Path(value="/cache")
public class AuditCacheRestResource {
    private static final Logger log = LoggerFactory.getLogger(AuditCacheRestResource.class);
    private final BuildCacheJobScheduler buildCacheJobScheduler;
    private final PermissionChecker permissionChecker;

    public AuditCacheRestResource(BuildCacheJobScheduler buildCacheJobScheduler, PermissionChecker permissionChecker) {
        this.buildCacheJobScheduler = buildCacheJobScheduler;
        this.permissionChecker = permissionChecker;
    }

    @Path(value="rebuild")
    @POST
    @Operation(summary="Rebuilds caches used by the audit feature (e.g actions and categories", tags={"audit", "cache"})
    @ApiResponses(value={@ApiResponse(responseCode="204", description="Successful operation"), @ApiResponse(responseCode="403", description="Forbidden"), @ApiResponse(responseCode="500", description="Internal server error")})
    public Response scheduleCacheRebuild() throws SchedulerServiceException {
        if (!this.permissionChecker.hasCacheRebuildPermission()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"Must be system admin to be able to rebuild the cache").build();
        }
        try {
            this.buildCacheJobScheduler.scheduleIfNeeded();
            log.info("Scheduled an audit cache rebuild job as requested");
            return Response.noContent().build();
        }
        catch (Exception exception) {
            log.error("Failed to schedule a audit cache rebuild job as requested", (Throwable)exception);
            return Response.serverError().build();
        }
    }
}

