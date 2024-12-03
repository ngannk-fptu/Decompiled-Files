/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.PreflightCheckPlanDto;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.impl.CloudTypeNotEnabledException;
import com.atlassian.migration.agent.service.impl.InvalidPlanException;
import com.atlassian.migration.agent.service.impl.PlanDecoratorService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="plan")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class PlanResource {
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final PlanDecoratorService planDecoratorService;

    public PlanResource(AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanDecoratorService planDecoratorService) {
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.planDecoratorService = planDecoratorService;
    }

    @GET
    @Path(value="/{planId}")
    public Response getPlan(@PathParam(value="planId") String planId) {
        return Response.ok((Object)this.planDecoratorService.getPlan(planId)).build();
    }

    @GET
    @Path(value="/")
    public Response getAllPlans() {
        return Response.ok(this.planDecoratorService.getAllPlans()).build();
    }

    @POST
    @Path(value="/")
    public Response createPlan(PlanDto plan, @QueryParam(value="omitTasks") @DefaultValue(value="false") Boolean shouldOmitTasks) {
        try {
            PlanDto planDto = this.planDecoratorService.createPlan(plan, shouldOmitTasks);
            ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
            this.analyticsEventService.sendAnalyticsEventsAsync(() -> this.analyticsEventBuilder.buildCreatePlanAndTasksAnalyticsEvents(planDto, confluenceUser, Optional.empty()));
            return Response.ok((Object)this.planDecoratorService.getPreflightCheckPlanDto(planDto, true)).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(e.getMessage())).build();
        }
        catch (InvalidPlanException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(e.getMessage())).build();
        }
    }

    @PUT
    @Path(value="/")
    public Response updatePlan(PlanDto planDto) {
        PlanDto updatedPlanDto = this.planDecoratorService.updatePlan(planDto);
        return Response.ok((Object)updatedPlanDto).build();
    }

    @GET
    @Path(value="/{planId}/progress")
    public Response getProgress(@PathParam(value="planId") String planId) {
        try {
            return Response.ok((Object)this.planDecoratorService.getPlanProgress(planId)).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)new MessageDto(String.format("Plan with id=%s hasn't been found", planId))).build();
        }
    }

    @POST
    @Path(value="/{planId}/start")
    public Response start(@PathParam(value="planId") String planId) {
        try {
            PreflightCheckPlanDto planDto = this.planDecoratorService.verifyAndStart(planId);
            return Response.ok((Object)planDto).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)new MessageDto(String.format("Plan with id=%s has already been started", planId))).build();
        }
        catch (CloudTypeNotEnabledException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(e.getMessage())).build();
        }
    }

    @POST
    @Path(value="/{planId}/stop")
    public Response stop(@PathParam(value="planId") String planId) {
        boolean hasPlanBeenStopped = this.planDecoratorService.stop(planId);
        if (!hasPlanBeenStopped) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)new MessageDto(String.format("Plan with id=%s cannot be stopped as it is not running", planId))).build();
        }
        return Response.ok((Object)this.planDecoratorService.getPlan(planId)).build();
    }

    @GET
    @Path(value="/validate/nameExists")
    public Response checkPlanNameExists(@QueryParam(value="name") String planName, @Nullable @QueryParam(value="planId") String planId) {
        return Response.ok((Object)this.planDecoratorService.planNameExists(planName, Objects.toString(planId, ""))).build();
    }

    @POST
    @Path(value="/{planId}/copy")
    public Response copy(@PathParam(value="planId") String planId) {
        return Response.ok((Object)this.planDecoratorService.copy(planId)).build();
    }

    @POST
    @Path(value="{planId}/archive")
    public Response archive(@PathParam(value="planId") String planId) {
        return Response.ok((Object)this.planDecoratorService.updateActiveStatus(planId, PlanActiveStatus.ARCHIVED)).build();
    }

    @DELETE
    @Path(value="{planId}/archive")
    public Response undoArchive(@PathParam(value="planId") String planId) {
        return Response.ok((Object)this.planDecoratorService.updateActiveStatus(planId, PlanActiveStatus.ACTIVE)).build();
    }

    @DELETE
    @Path(value="{planId}")
    public Response delete(@PathParam(value="planId") String planId) {
        return Response.ok((Object)this.planDecoratorService.deletePlan(planId)).build();
    }

    @GET
    @Path(value="{planId}/spaces")
    public Response getSpaces(@PathParam(value="planId") String planId) {
        return Response.ok(this.planDecoratorService.getSpaces(planId)).build();
    }
}

