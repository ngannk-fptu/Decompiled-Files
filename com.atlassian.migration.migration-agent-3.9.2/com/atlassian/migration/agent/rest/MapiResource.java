/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.RequestValidationException;
import com.atlassian.migration.agent.mapi.entity.AttachCloudError;
import com.atlassian.migration.agent.mapi.entity.AttachCloudResponse;
import com.atlassian.migration.agent.mapi.executor.CloudExecutorService;
import com.atlassian.migration.agent.mapi.external.model.JobValidationException;
import com.atlassian.migration.agent.mapi.external.model.PublicApiException;
import com.atlassian.migration.agent.service.MigrationMetric;
import com.atlassian.migration.agent.service.impl.InvalidPlanException;
import com.atlassian.migration.agent.service.prc.PrcClientService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="public")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class MapiResource {
    private CloudExecutorService cloudExecutorService;
    private PrcClientService prcClientService;
    private final Supplier<Instant> instantSupplier;
    private static final String MAPI_ATTACH_JOB_ACTION_NAME = "attached";

    public MapiResource(CloudExecutorService cloudExecutorService, PrcClientService prcClientService) {
        this.cloudExecutorService = cloudExecutorService;
        this.prcClientService = prcClientService;
        this.instantSupplier = Instant::now;
    }

    @POST
    @Path(value="/v1/attach")
    public Response attachCloudUrl(@QueryParam(value="cloudUrl") String cloudUrl) {
        try {
            Instant pollerExpiryTime = this.prcClientService.attach(cloudUrl);
            return Response.status((Response.Status)Response.Status.OK).entity((Object)new AttachCloudResponse(cloudUrl, pollerExpiryTime)).build();
        }
        catch (PublicApiException.CloudUrlDoesNotExist ex) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new AttachCloudError("Invalid Request. Reason = " + ex.getMessage())).build();
        }
        catch (Exception ex) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)new AttachCloudError("Please check Confluence Logs to find the issue or contact support, if the issue persists.")).build();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @Path(value="/v1/jobs/{jobId}/attach")
    public Response createPlan(@PathParam(value="jobId") String jobId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        int statusCode = Response.Status.CREATED.getStatusCode();
        String errorReason = "";
        PlanDto planDto = null;
        try {
            planDto = this.cloudExecutorService.createPlan(jobId, Optional.empty());
            this.cloudExecutorService.sendCreatePlanAnalyticsEvents(planDto, AuthenticatedUserThreadLocal.get(), Optional.of(jobId));
            Response response = Response.status((int)statusCode).entity((Object)planDto).build();
            return response;
        }
        catch (Exception ex) {
            Response response = this.createErrorResponse(ex);
            statusCode = response.getStatus();
            errorReason = ex.getMessage();
            Response response2 = response;
            return response2;
        }
        finally {
            long totalTime = this.instantSupplier.get().toEpochMilli() - startTime;
            this.cloudExecutorService.sendMapiJobAnalyticsEvents(jobId, planDto, statusCode, errorReason, totalTime, MAPI_ATTACH_JOB_ACTION_NAME, MigrationMetric.MAPI_ATTACH_JOB_TIMER_METRIC_EVENT_NAME.metricName);
        }
    }

    private Response createErrorResponse(Exception ex) {
        if (ex instanceof InvalidPlanException || ex instanceof IllegalArgumentException || ex instanceof JobValidationException || ex instanceof RequestValidationException) {
            return this.buildResponse(Response.Status.BAD_REQUEST, "Invalid Request. Reason = " + ex.getMessage());
        }
        if (ex instanceof PublicApiException.DuplicateRequestException) {
            return this.buildResponse(Response.Status.CONFLICT, "Duplicate Request. Reason = " + ex.getMessage());
        }
        if (ex instanceof PublicApiException) {
            return this.buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error. Reason = " + ex.getMessage());
        }
        return this.buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Please check Confluence Logs to find the issue or contact support, if the issue persists.");
    }

    private Response buildResponse(Response.Status status, String message) {
        return Response.status((Response.Status)status).entity((Object)message).build();
    }
}

