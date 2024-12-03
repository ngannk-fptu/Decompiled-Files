/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.createjiracontent.rest;

import com.atlassian.confluence.plugins.createjiracontent.services.FeatureDiscoveryService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="metadata")
@Produces(value={"application/json"})
public class CreateIssueMetadataResource {
    private final FeatureDiscoveryService featureDiscoveryService;

    public CreateIssueMetadataResource(FeatureDiscoveryService featureDiscoveryService) {
        this.featureDiscoveryService = featureDiscoveryService;
    }

    @Path(value="/discovered")
    @PUT
    public Response setDiscovered() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.featureDiscoveryService.setUserDiscovered(user, true);
        return Response.ok().build();
    }
}

