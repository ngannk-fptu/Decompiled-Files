/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.schedule.UpmScheduler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/test/scheduler")
public class RunScheduledJobResource {
    private final UpmScheduler upmScheduler;
    private final PermissionEnforcer permissionEnforcer;

    public RunScheduledJobResource(PermissionEnforcer permissionEnforcer, UpmScheduler upmScheduler) {
        this.permissionEnforcer = permissionEnforcer;
        this.upmScheduler = upmScheduler;
    }

    @GET
    public Response waitForCompletion() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!UpmSys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.upmScheduler.waitForTriggeredJobs();
        return Response.ok().build();
    }

    @Path(value="/{jobClass}")
    @POST
    @Consumes(value={"application/x-www-form-urlencoded"})
    @XsrfProtectionExcluded
    public Response triggerJob(@PathParam(value="jobClass") String jobClassName, @QueryParam(value="runMode") UpmScheduler.RunMode runMode) throws Exception {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!UpmSys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        Class<?> jobClass = Class.forName(jobClassName);
        this.upmScheduler.triggerJob(jobClass, runMode == null ? UpmScheduler.RunMode.TRIGGERED_INTERNALLY : runMode);
        return Response.ok().build();
    }
}

