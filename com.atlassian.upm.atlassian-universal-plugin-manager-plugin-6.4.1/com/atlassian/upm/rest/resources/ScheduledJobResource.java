/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.schedule.UpmScheduler;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/scheduler")
public class ScheduledJobResource {
    public static final String UPDATES_RESOURCE = "updates";
    private final PermissionEnforcer permissionEnforcer;

    public ScheduledJobResource(PermissionEnforcer permissionEnforcer, UpmScheduler upmScheduler) {
        this.permissionEnforcer = permissionEnforcer;
    }

    @POST
    @Path(value="updates")
    @XsrfProtectionExcluded
    public Response executeUpdateCheckJob() {
        this.permissionEnforcer.enforceSystemAdmin();
        return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
    }
}

