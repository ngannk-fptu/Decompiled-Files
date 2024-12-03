/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/test/marketplace-cache")
public class MarketplaceCacheResource {
    private final PermissionEnforcer permissionEnforcer;
    private final PacClient pacClient;

    public MarketplaceCacheResource(PermissionEnforcer permissionEnforcer, PacClient pacClient) {
        this.permissionEnforcer = permissionEnforcer;
        this.pacClient = pacClient;
    }

    @DELETE
    public Response clearCache() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!UpmSys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.pacClient.clearAllCachedMarketplaceState();
        return Response.ok().build();
    }
}

