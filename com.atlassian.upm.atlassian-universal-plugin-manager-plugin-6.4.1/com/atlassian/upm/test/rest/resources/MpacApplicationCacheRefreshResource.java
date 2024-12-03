/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.MpacApplicationCacheManager;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/refresh-cache")
public class MpacApplicationCacheRefreshResource {
    private final MpacApplicationCacheManager mpacApplicationCacheManager;
    private final PermissionEnforcer permissionEnforcer;

    public MpacApplicationCacheRefreshResource(MpacApplicationCacheManager mpacApplicationCacheManager, PermissionEnforcer permissionEnforcer) {
        this.mpacApplicationCacheManager = Objects.requireNonNull(mpacApplicationCacheManager);
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer);
    }

    @PUT
    @Path(value="/populate")
    public Response updateCache(@Context HttpServletRequest request) {
        this.permissionEnforcer.enforceSystemAdmin();
        this.mpacApplicationCacheManager.populateCache();
        return Response.ok().build();
    }

    @PUT
    @Path(value="/reset")
    public Response resetCache(@Context HttpServletRequest request) {
        this.permissionEnforcer.enforceSystemAdmin();
        this.mpacApplicationCacheManager.reset();
        return Response.ok().build();
    }
}

