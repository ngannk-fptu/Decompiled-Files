/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.crowd.embedded.admin.rest;

import com.atlassian.crowd.embedded.admin.rest.entities.ApplicationEntity;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(value="/application")
@Produces(value={"application/xml", "application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class ApplicationResource {
    @Context
    private UriInfo uriInfo;
    private final CrowdDirectoryService crowdDirectoryService;

    public ApplicationResource(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    @GET
    public ApplicationEntity get() {
        boolean membershipAggregationEnabled = this.crowdDirectoryService.isMembershipAggregationEnabled();
        ApplicationEntity entity = new ApplicationEntity();
        entity.setMembershipAggregationEnabled(membershipAggregationEnabled);
        return entity;
    }

    @PUT
    @Consumes(value={"application/xml", "application/json"})
    public Response update(ApplicationEntity entity) {
        Boolean membershipAggregationEnabled = entity.isMembershipAggregationEnabled();
        if (membershipAggregationEnabled != null) {
            this.crowdDirectoryService.setMembershipAggregationEnabled(membershipAggregationEnabled.booleanValue());
        }
        return Response.noContent().build();
    }
}

