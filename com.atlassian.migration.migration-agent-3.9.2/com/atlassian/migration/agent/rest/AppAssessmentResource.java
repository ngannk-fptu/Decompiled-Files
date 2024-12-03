/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.dto.AppInstallInfoRequest;
import com.atlassian.migration.agent.dto.assessment.AppAssessmentUpdateRequest;
import com.atlassian.migration.agent.dto.assessment.AppConsentDto;
import com.atlassian.migration.agent.dto.assessment.ConsentRequestDto;
import com.atlassian.migration.agent.dto.assessment.UpdateAllAppAssessmentInfoRequest;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.AppConsentService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;

@Path(value="app")
@ReadOnlyAccessAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class AppAssessmentResource {
    private final AppAssessmentFacade appAssessmentService;
    private final AppConsentService appConsentService;
    private static final Logger log = ContextLoggerFactory.getLogger(AppAssessmentResource.class);

    @Inject
    public AppAssessmentResource(AppAssessmentFacade appAssessmentService, AppConsentService appConsentService) {
        this.appAssessmentService = appAssessmentService;
        this.appConsentService = appConsentService;
    }

    @GET
    @Path(value="/")
    public Response getPluginInfo() {
        return Response.ok(this.appAssessmentService.getPlugins()).build();
    }

    @GET
    @Path(value="/stats")
    public Response getPluginStats() {
        return Response.ok((Object)this.appAssessmentService.getPluginStats()).build();
    }

    @POST
    @Path(value="/update")
    public Response updateAppAssessmentInfo(AppAssessmentUpdateRequest updateRequest) {
        this.appAssessmentService.updateAppAssessmentInfo(updateRequest.getAppKey(), updateRequest);
        return Response.ok().build();
    }

    @POST
    @Path(value="/update/all")
    public Response updateAllAppAssessmentInfo(UpdateAllAppAssessmentInfoRequest updateRequest) {
        this.appAssessmentService.updateAllAppAssessmentInfo(updateRequest);
        return Response.ok().build();
    }

    @GET
    @Path(value="/usage")
    public Response getAppUsageStats() {
        return Response.ok(this.appAssessmentService.getAppUsageStats()).build();
    }

    @POST
    @Path(value="/usage/clearAppUsageCache")
    public Response clearAppUsageCache() {
        return Response.ok((Object)this.appAssessmentService.clearAppUsageCache()).build();
    }

    @POST
    @Path(value="/siteinfo")
    public Response getAppInstallationInfo(AppInstallInfoRequest request) {
        return Response.ok(this.appAssessmentService.getCloudAppsInfo(request)).build();
    }

    @GET
    @Path(value="/consent")
    public Response getConsentApps() {
        return Response.ok(this.appAssessmentService.getAllConsentApps()).build();
    }

    @GET
    @Path(value="/consent/allowAppMigrationsDataShare")
    public Response getSharedConsentForMarketplacePartners() {
        return Response.ok((Object)this.appConsentService.getSharedConsentForMarketplacePartners()).build();
    }

    @PUT
    @Path(value="/consent/allowAppMigrationsDataShare")
    public Response saveSharedConsentForMarketplacePartners(ConsentRequestDto consentRequestDto) {
        return Response.ok((Object)this.appConsentService.saveSharedConsentForMarketplacePartners(consentRequestDto.getDisplayedText())).build();
    }

    @POST
    @Path(value="/consent/update")
    public Response updateAppConsent(AppAssessmentUpdateRequest updateRequest) {
        Optional<AppConsentDto> maybeAppConsentDto = this.appAssessmentService.updateAppConsent(updateRequest.getAppKey(), updateRequest);
        if (!maybeAppConsentDto.isPresent()) {
            log.warn("App key not found {}", (Object)updateRequest.getAppKey());
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)new MessageDto(String.format("App key not found = %s", updateRequest.getAppKey()))).build();
        }
        return Response.ok((Object)maybeAppConsentDto.get()).build();
    }

    @GET
    @Path(value="/list/neededInCloud")
    public Response getAppsNeededInCloud() {
        return Response.ok(this.appAssessmentService.getAppsNeededInCloud()).build();
    }
}

