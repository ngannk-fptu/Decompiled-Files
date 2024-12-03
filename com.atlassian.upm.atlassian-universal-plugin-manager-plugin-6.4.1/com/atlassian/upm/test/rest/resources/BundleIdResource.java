/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/test/bundle-id")
public class BundleIdResource {
    private final PermissionEnforcer permissionEnforcer;
    private final UpmInformation upm;

    public BundleIdResource(PermissionEnforcer permissionEnforcer, UpmInformation upm) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.upm = Objects.requireNonNull(upm, "upm");
    }

    @GET
    @Produces(value={"text/plain"})
    public Response getBundleId() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!UpmSys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        return Response.ok((Object)String.valueOf(this.upm.getBundleId())).build();
    }
}

