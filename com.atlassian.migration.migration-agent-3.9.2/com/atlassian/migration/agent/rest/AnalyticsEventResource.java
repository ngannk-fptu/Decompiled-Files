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
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.analytics.ScreenAnalyticsEventDto;
import com.atlassian.migration.agent.dto.analytics.TrackAnalyticsEventDto;
import com.atlassian.migration.agent.dto.analytics.UIAnalyticsEventDto;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="mas")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@ReadOnlyAccessAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class AnalyticsEventResource {
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final PlanService planService;

    @Inject
    public AnalyticsEventResource(AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanService planService) {
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.planService = planService;
    }

    @POST
    @Path(value="/event/ui")
    public Response saveAnalyticsEvent(UIAnalyticsEventDto uiEvent) {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUIAnalyticsEvent(uiEvent, confluenceUser));
        return Response.noContent().build();
    }

    @POST
    @Path(value="/event/screen")
    public Response saveAnalyticsEvent(ScreenAnalyticsEventDto screenEvent) {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildScreenAnalyticsEvent(screenEvent, confluenceUser));
        return Response.noContent().build();
    }

    @POST
    @Path(value="/event/track")
    public Response saveAnalyticsEvent(TrackAnalyticsEventDto trackEvent) {
        PlanDto planDto = this.planService.getPlan(trackEvent.getActionSubjectId());
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildTrackAnalyticsEvent(trackEvent, planDto, confluenceUser));
        return Response.noContent().build();
    }
}

