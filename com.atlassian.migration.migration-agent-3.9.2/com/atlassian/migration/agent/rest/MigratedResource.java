/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.store.impl.MigratedSpaceStore;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Arrays;
import java.util.TreeSet;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="migrated")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class MigratedResource {
    private final MigratedSpaceStore migratedSpaceService;

    public MigratedResource(MigratedSpaceStore migratedSpaceService) {
        this.migratedSpaceService = migratedSpaceService;
    }

    @GET
    @Path(value="/")
    public Response getAllMigratedSpaces() {
        return Response.ok(this.migratedSpaceService.getAllSpaces()).build();
    }

    @GET
    @Path(value="/{spacekey}")
    public Response getMigratedSpace(@PathParam(value="spacekey") String spacekey) {
        TreeSet<String> allSpaces = new TreeSet<String>(this.migratedSpaceService.getAllSpaces());
        return Response.ok((Object)allSpaces.retainAll(Arrays.asList(spacekey))).build();
    }
}

