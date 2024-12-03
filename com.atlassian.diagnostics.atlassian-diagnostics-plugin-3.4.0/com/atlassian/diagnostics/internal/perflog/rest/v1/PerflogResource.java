/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.diagnostics.internal.perflog.rest.v1;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.diagnostics.internal.perflog.IpdLogService;
import com.atlassian.diagnostics.internal.perflog.model.InstrumentQueryResults;
import com.atlassian.diagnostics.internal.perflog.model.IpdLogsResponse;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Produces(value={"application/json"})
@Path(value="/")
public class PerflogResource {
    private final IpdLogService ipdLogService;
    private final DarkFeatureManager darkFeatureManager;
    private final PermissionEnforcer permissionEnforcer;
    private final TimeZoneManager timeZoneManager;

    @GET
    @Path(value="/in-product-diagnostics")
    @ExperimentalApi
    public Response getInProductDiagnosticsLogs(@QueryParam(value="metricType") List<String> metricTypes) {
        if (!this.permissionEnforcer.isSystemAdmin()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        Optional enabledForCurrentUser = this.darkFeatureManager.isEnabledForCurrentUser("com.atlassian.jira.in.product.diagnostics.wip.enabled");
        if (enabledForCurrentUser.isPresent() && ((Boolean)enabledForCurrentUser.get()).booleanValue()) {
            List<InstrumentQueryResults> instrumentQueries = this.ipdLogService.readLog(metricTypes);
            IpdLogsResponse result = IpdLogsResponse.builder().logLines(instrumentQueries).currentUserTimezone(this.timeZoneManager.getUserTimeZone().getID()).build();
            return Response.ok((Object)result).build();
        }
        return Response.ok(Collections.emptyList()).build();
    }

    public PerflogResource(IpdLogService ipdLogService, DarkFeatureManager darkFeatureManager, PermissionEnforcer permissionEnforcer, TimeZoneManager timeZoneManager) {
        this.ipdLogService = ipdLogService;
        this.darkFeatureManager = darkFeatureManager;
        this.permissionEnforcer = permissionEnforcer;
        this.timeZoneManager = timeZoneManager;
    }
}

