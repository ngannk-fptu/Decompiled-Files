/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.SafeModeService;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/enablement-state")
public class EnablementStateResource {
    private final SafeModeService safeMode;
    private final PermissionEnforcer permissionEnforcer;

    public EnablementStateResource(SafeModeService safeMode, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getEnablementState() {
        this.permissionEnforcer.enforcePermission(Permission.GET_SAFE_MODE);
        return Response.ok((Object)this.safeMode.getCurrentConfiguration()).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response putEnablementState(PluginsEnablementState config) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_SAFE_MODE);
        this.safeMode.applyConfiguration(config);
        return Response.ok().build();
    }
}

