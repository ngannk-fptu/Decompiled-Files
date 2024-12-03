/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.setup.settings.UnknownFeatureException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.SystemAdminOnly
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.darkfeatures.rest;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.SystemAdminOnly;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@WebSudoRequired
@SystemAdminOnly
@Path(value="/site")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class DarkFeatureSiteResource {
    private final DarkFeaturesManager darkFeaturesManager;

    public DarkFeatureSiteResource(@ComponentImport DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @POST
    public Response enableFeatures(String[] featureKeys) throws UnknownFeatureException {
        for (String featureKey : featureKeys) {
            this.darkFeaturesManager.enableSiteFeature(featureKey);
        }
        return Response.status((Response.Status)Response.Status.CREATED).build();
    }

    @Deprecated
    @PUT
    @Path(value="/{featureKeys}")
    public Response enableFeatures(@PathParam(value="featureKeys") String featureKeys) throws UnknownFeatureException {
        return this.enableFeatures(featureKeys.split(","));
    }

    @DELETE
    @Path(value="/{featureKeys}")
    public Response disableFeatures(@PathParam(value="featureKeys") String featureKeys) throws UnknownFeatureException {
        for (String featureKey : featureKeys.split(",")) {
            this.darkFeaturesManager.disableSiteFeature(featureKey);
        }
        return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path(value="/{featureKey}")
    public Response getFeature(@PathParam(value="featureKey") String featureKey) {
        DarkFeatures darkFeatures = this.darkFeaturesManager.getDarkFeatures();
        return darkFeatures.getGlobalEnabledFeatures().contains(featureKey) ? Response.status((Response.Status)Response.Status.OK).build() : Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

