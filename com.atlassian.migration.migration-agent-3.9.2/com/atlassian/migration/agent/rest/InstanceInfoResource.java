/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.InstanceInfoDto;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="instance")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Produces(value={"application/json"})
public class InstanceInfoResource {
    private final SENSupplier senSupplier;

    public InstanceInfoResource(SENSupplier senSupplier) {
        this.senSupplier = senSupplier;
    }

    @GET
    @Path(value="/info")
    public Response getServerInstanceInfo() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return Response.ok((Object)new InstanceInfoDto(user.getEmail(), user.getName(), this.senSupplier.get())).build();
    }
}

