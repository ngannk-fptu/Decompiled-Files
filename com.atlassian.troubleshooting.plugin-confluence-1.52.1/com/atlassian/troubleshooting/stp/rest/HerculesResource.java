/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.rest.CacheControlUtils;
import com.atlassian.troubleshooting.stp.rest.RestLogScanStatus;
import com.atlassian.troubleshooting.stp.scheduler.ScheduledHerculesHealthReportAction;
import com.atlassian.troubleshooting.stp.security.AuthorisationException;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.checkerframework.checker.nullness.qual.NonNull;

@Path(value="hercules")
@Produces(value={"application/json"})
@Singleton
public class HerculesResource {
    private final LogScanService scanService;
    private final ScheduledHerculesHealthReportAction action;
    private final PermissionValidationService permissionValidationService;

    public HerculesResource(@Nonnull LogScanService scanService, @Nonnull ScheduledHerculesHealthReportAction action, @NonNull PermissionValidationService permissionValidationService) {
        this.scanService = Objects.requireNonNull(scanService);
        this.action = Objects.requireNonNull(action);
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
    }

    @GET
    @Path(value="scans/{id}")
    public Response getScanDetails(@PathParam(value="id") String taskId) {
        this.permissionValidationService.validateIsSysadmin();
        LogScanMonitor monitor = this.scanService.getMonitor(taskId);
        if (monitor == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(CacheControlUtils.NO_CACHE).build();
        }
        if (monitor.isCancelled()) {
            return Response.status((Response.Status)Response.Status.NO_CONTENT).cacheControl(CacheControlUtils.NO_CACHE).build();
        }
        Response.ResponseBuilder builder = Response.ok().entity((Object)new RestLogScanStatus(monitor));
        if (!monitor.isDone()) {
            builder.cacheControl(CacheControlUtils.NO_CACHE);
        }
        return builder.build();
    }

    @GET
    @Path(value="periodicScanner/settings")
    public Response getPeriodicScannerSettings(@Context HttpServletRequest req) {
        this.permissionValidationService.validateIsSysadmin();
        Map<String, Object> map = this.action.getScannerSettings(req);
        Response.ResponseBuilder builder = Response.ok().entity(map);
        return builder.build();
    }

    @POST
    @Path(value="periodicScanner/settings")
    @XsrfProtectionExcluded
    public Response storePeriodicScannerSettings(@FormParam(value="atl_token") String token, @FormParam(value="enabled") String isEnabled, @FormParam(value="start-time-hour") int startHour, @FormParam(value="start-time-minute") int startMinute, @FormParam(value="frequency") String frequency, @FormParam(value="recipients") String recipients, @Context HttpServletRequest req) {
        try {
            this.permissionValidationService.validateIsSysadmin();
            Map<String, Object> map = this.action.storeScannerSettings(isEnabled, startHour, startMinute, frequency, recipients, token, req);
            Response.ResponseBuilder builder = map.containsKey("errors") || map.containsKey("tokenError") ? Response.status((Response.Status)Response.Status.BAD_REQUEST).entity(map) : Response.ok().entity(map);
            return builder.build();
        }
        catch (AuthorisationException e) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)e.getMessage()).build();
        }
    }

    @ExperimentalApi
    @DELETE
    @Path(value="scans/removeCache")
    @XsrfProtectionExcluded
    public Response clearScanCache() {
        this.scanService.clearScanResultCache();
        return Response.status((Response.Status)Response.Status.OK).cacheControl(CacheControlUtils.NO_CACHE).build();
    }
}

