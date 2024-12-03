/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.dataset.DatasetProvider
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  io.swagger.v3.oas.annotations.OpenAPIDefinition
 *  io.swagger.v3.oas.annotations.Parameter
 *  io.swagger.v3.oas.annotations.info.Info
 *  io.swagger.v3.oas.annotations.media.Schema
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.business.insights.core.rest;

import com.atlassian.business.insights.api.dataset.DatasetProvider;
import com.atlassian.business.insights.core.analytics.history.JobHistoryRequestedAnalyticEvent;
import com.atlassian.business.insights.core.rest.model.Page;
import com.atlassian.business.insights.core.rest.model.ProcessStatusResponse;
import com.atlassian.business.insights.core.rest.validation.ValidateLicenseIsDc;
import com.atlassian.business.insights.core.rest.validation.ValidateQueryParams;
import com.atlassian.business.insights.core.rest.validation.ValidateUserIsAuthedAsSysAdmin;
import com.atlassian.business.insights.core.rest.validation.Validator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.PageLimitValidator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.PageOffsetValidator;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.ExportJobStateService;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@OpenAPIDefinition(info=@Info(title="Data Pipeline History Resource", version="1.0.0", description="Experimental Data Pipeline API. Endpoint to query for job history from previous Data Pipeline runs in descending order. The root path is /rest/datapipeline/latest"))
@Path(value="/history")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@Component
@InterceptorChain(value={ValidateUserIsAuthedAsSysAdmin.class, ValidateLicenseIsDc.class, ValidateQueryParams.class})
public class HistoryResource {
    private final ExportJobStateService exportJobStateService;
    private final DateConversionUtil dateConversionUtil;
    private final DatasetProvider datasetProvider;
    private final EventPublisherService eventPublisherService;

    @Autowired
    public HistoryResource(ExportJobStateService exportJobStateService, DateConversionUtil dateConversionUtil, DatasetProvider datasetProvider, EventPublisherService eventPublisherService) {
        this.exportJobStateService = exportJobStateService;
        this.dateConversionUtil = dateConversionUtil;
        this.datasetProvider = datasetProvider;
        this.eventPublisherService = eventPublisherService;
    }

    @GET
    public Response queryHistory(@Parameter(description="The offset from when the history should be queried for") @Schema(type="integer") @QueryParam(value="offset") @DefaultValue(value="0") @Validator(value=PageOffsetValidator.class) String offsetString, @Parameter(description="The amount of entries the response should be limited to") @Schema(type="integer") @QueryParam(value="limit") @DefaultValue(value="10") @Validator(value=PageLimitValidator.class) String limitString) {
        int limit;
        int offset = Integer.parseInt(offsetString);
        List<ProcessStatusResponse> processStatusResponses = this.exportJobStateService.getExportJobStates(offset, limit = Integer.parseInt(limitString)).stream().map(this::getResponseEntity).collect(Collectors.toList());
        boolean isLastPage = processStatusResponses.size() < limit;
        this.publishJobHistoryRequestedAnalyticEvent(offset, limit, processStatusResponses, isLastPage);
        return Response.status((Response.Status)Response.Status.OK).entity(new Page<ProcessStatusResponse>(processStatusResponses, offset, limit, isLastPage)).build();
    }

    private ProcessStatusResponse getResponseEntity(ExportJobState exportJobState) {
        return new ProcessStatusResponse(exportJobState, this.dateConversionUtil::formatToIso, this.datasetProvider.getSchemaStatus(exportJobState.getSchemaVersion()));
    }

    private void publishJobHistoryRequestedAnalyticEvent(int offset, int limit, List<ProcessStatusResponse> processStatusResponses, boolean isLastPage) {
        this.eventPublisherService.publish(new JobHistoryRequestedAnalyticEvent(this.eventPublisherService.getPluginVersion(), isLastPage, limit, offset, processStatusResponses.size()));
    }
}

