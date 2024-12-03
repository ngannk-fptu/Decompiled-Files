/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.rest.ContainerTokenValidator;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.email.UserBaseScanRunner;
import com.atlassian.migration.agent.service.email.UserBaseScanService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Path(value="userbase")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class UserBaseScanResource {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserBaseScanResource.class);
    private final UserBaseScanRunner userBaseScanRunner;
    private final UserBaseScanService userBaseScanService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final CloudSiteService cloudSiteService;
    private final ContainerTokenValidator containerTokenValidator;

    public UserBaseScanResource(UserBaseScanRunner userBaseScanRunner, UserBaseScanService userBaseScanService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, CloudSiteService cloudSiteService, ContainerTokenValidator containerTokenValidator) {
        this.userBaseScanRunner = userBaseScanRunner;
        this.userBaseScanService = userBaseScanService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.cloudSiteService = cloudSiteService;
        this.containerTokenValidator = containerTokenValidator;
    }

    @POST
    @Path(value="/scan/{cloudId}")
    public Response scanUserBase(@PathParam(value="cloudId") String cloudId) {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        CloudSite cloudSite = this.cloudSiteService.getByCloudId(cloudId).orElse(null);
        Optional<Response> responseWhenNotValid = this.containerTokenValidator.validateContainerToken(cloudSite).toResponseWhenNotValid();
        if (responseWhenNotValid.isPresent()) {
            return responseWhenNotValid.get();
        }
        this.userBaseScanRunner.startUserBaseScan(cloudSite.getCloudId());
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    @GET
    @Path(value="/scan/summary")
    public Response getScanSummary() {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)this.userBaseScanService.getScanSummary()).build();
    }

    private boolean isGlobalEmailFixesDisabled() {
        return !this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes();
    }
}

