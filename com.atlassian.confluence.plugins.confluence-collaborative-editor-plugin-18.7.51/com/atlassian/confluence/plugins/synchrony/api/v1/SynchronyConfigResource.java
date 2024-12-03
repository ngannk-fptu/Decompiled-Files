/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.synchrony.api.v1;

import com.atlassian.confluence.plugins.synchrony.api.v1.model.CollaborativeEditingSetup;
import com.atlassian.confluence.plugins.synchrony.api.v1.model.CollaborativeEditingStatus;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/config")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Produces(value={"application/json"})
public class SynchronyConfigResource {
    private final SynchronyConfigurationManager synchronyConfigurationManager;

    public SynchronyConfigResource(SynchronyConfigurationManager synchronyConfigurationManager) {
        this.synchronyConfigurationManager = synchronyConfigurationManager;
    }

    @GET
    @Path(value="/status")
    public Response getStatus() {
        CollaborativeEditingStatus response = new CollaborativeEditingStatus(this.synchronyConfigurationManager.isSharedDraftsEnabled(), this.synchronyConfigurationManager.isSharedDraftsExplicitlyDisabled(), this.synchronyConfigurationManager.getExternalBaseUrl(), Optional.ofNullable(this.synchronyConfigurationManager.getConfiguredAppID()).orElse(""), this.synchronyConfigurationManager.isRegistrationComplete(), this.synchronyConfigurationManager.getSynchronyPublicKey());
        return Response.ok().entity((Object)response).build();
    }

    @PUT
    @Path(value="/setup")
    public Response doSetup() {
        CollaborativeEditingSetup response = new CollaborativeEditingSetup(this.synchronyConfigurationManager.registerWithSynchrony(), this.synchronyConfigurationManager.retrievePublicKey());
        return Response.ok().entity((Object)response).build();
    }
}

