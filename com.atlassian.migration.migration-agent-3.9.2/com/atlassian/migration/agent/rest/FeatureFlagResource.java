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
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.featureflag.FeatureFlagClient;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.HashSet;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="featureflag")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class FeatureFlagResource {
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final FeatureFlagClient featureFlagClient;

    public FeatureFlagResource(MigrationDarkFeaturesManager migrationDarkFeaturesManager, FeatureFlagClient featureFlagClient) {
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.featureFlagClient = featureFlagClient;
    }

    @GET
    @Path(value="/all")
    public Response getAllEnabledFeatureFlags() {
        return Response.ok(this.migrationDarkFeaturesManager.getAllEnabledFeatures()).build();
    }

    @GET
    @Path(value="/all/{planId}")
    public Response getAllEnabledFeatureFlagsForPlan(@PathParam(value="planId") String planId) {
        return Response.ok(this.migrationDarkFeaturesManager.getAllEnabledFeatures()).build();
    }

    @GET
    @Path(value="/launchdarkly/all")
    public Response getAllLaunchDarklyFeatureFlags() {
        return Response.ok(new HashSet<String>(this.featureFlagClient.getAllEnabledFeatureFlags())).build();
    }
}

