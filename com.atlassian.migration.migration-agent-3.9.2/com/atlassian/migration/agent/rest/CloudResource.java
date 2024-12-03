/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.cmpt.domain.Edition;
import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.dto.CloudSiteDto;
import com.atlassian.migration.agent.entity.CloudEdition;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.rest.GenerateCloudSiteSetupUrlDto;
import com.atlassian.migration.agent.service.NetworkStatisticsService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.cloud.CloudSiteSetupService;
import com.atlassian.migration.agent.service.cloud.LegalService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="cloud")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class CloudResource {
    private final CloudSiteService cloudSiteService;
    private final LegalService legalService;
    private final CloudSiteSetupService cloudSiteSetupService;
    private final NetworkStatisticsService networkStatisticsService;

    CloudResource(CloudSiteService cloudSiteService, LegalService legalService, CloudSiteSetupService cloudSiteSetupService, NetworkStatisticsService networkStatisticsService) {
        this.cloudSiteService = cloudSiteService;
        this.legalService = legalService;
        this.cloudSiteSetupService = cloudSiteSetupService;
        this.networkStatisticsService = networkStatisticsService;
    }

    @GET
    @Path(value="/")
    public Response getSites() {
        List cloudSites = this.cloudSiteService.getAllSites().stream().map(this::cloudSite2dto).collect(Collectors.toList());
        return Response.ok(cloudSites).build();
    }

    @POST
    @Path(value="/rememberLegalOptIn")
    public void rememberLegalOptIn() {
        this.legalService.rememberLegalOptIn();
        this.networkStatisticsService.measureConnectionStats();
    }

    @DELETE
    @Path(value="/rememberLegalOptIn")
    public void forgetLegalOptIn() {
        this.legalService.forgetLegalOptIn();
    }

    @GET
    @Path(value="/rememberLegalOptIn")
    public Response getRememberLegalOptIn() {
        return Response.ok((Object)this.legalService.getRememberLegalOptIn()).build();
    }

    @POST
    @Path(value="/generateCloudSiteSetupUrl")
    public Response generateCloudSiteSetupUrl(GenerateCloudSiteSetupUrlDto cloudSiteSetupDto) {
        String setupUrl = this.cloudSiteSetupService.generateCloudSiteSetupUrl(cloudSiteSetupDto);
        return Response.ok((Object)setupUrl).build();
    }

    @POST
    @Path(value="/generateEctlRedirectUrl")
    public Response generateEctlRedirectUrl(GenerateCloudSiteSetupUrlDto cloudSiteSetupDto) {
        String setupUrl = this.cloudSiteSetupService.generateEctlRedirectUrl(cloudSiteSetupDto);
        return Response.ok((Object)setupUrl).build();
    }

    private CloudSiteDto cloudSite2dto(CloudSite cloudSite) {
        Edition edition = Optional.ofNullable(cloudSite.getEdition()).map(CloudEdition::getKey).orElse(null);
        return new CloudSiteDto(cloudSite.getCloudUrl(), cloudSite.getCloudId(), edition, cloudSite.getCloudType());
    }
}

