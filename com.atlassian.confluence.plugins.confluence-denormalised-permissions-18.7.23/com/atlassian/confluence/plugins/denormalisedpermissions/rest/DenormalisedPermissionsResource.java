/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState
 *  com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.denormalisedpermissions.rest;

import com.atlassian.confluence.plugins.denormalisedpermissions.state.ServiceStateJson;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class DenormalisedPermissionsResource {
    private final DenormalisedPermissionStateManager denormalisedPermissionStateManager;

    public DenormalisedPermissionsResource(@ComponentImport DenormalisedPermissionStateManager denormalisedPermissionStateManager) {
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
    }

    @GET
    @Path(value="/state")
    public Response getState(@QueryParam(value="logLimit") int logLimit) {
        DenormalisedPermissionServiceState spaceServiceState = this.denormalisedPermissionStateManager.getSpaceServiceState(true);
        DenormalisedPermissionServiceState contentServiceState = this.denormalisedPermissionStateManager.getContentServiceState(true);
        Long spaceLag = this.denormalisedPermissionStateManager.getSpacePermissionUpdateLag();
        Long contentLag = this.denormalisedPermissionStateManager.getContentPermissionUpdateLag();
        List stateChangeLog = this.denormalisedPermissionStateManager.getStateChangeLog(logLimit);
        return Response.ok((Object)new ServiceStateJson(spaceServiceState, contentServiceState, spaceLag, contentLag, stateChangeLog)).build();
    }

    @POST
    @Path(value="/enableservice")
    public Response enableService() {
        this.denormalisedPermissionStateManager.enableService();
        return Response.ok().build();
    }

    @POST
    @Path(value="/disableservice")
    public Response disableService(@QueryParam(value="cleanDenormalisedData") boolean cleanDenormalisedData) {
        this.denormalisedPermissionStateManager.disableService(cleanDenormalisedData);
        return Response.ok().build();
    }
}

