/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.dataset.DatasetProvider
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.message.I18nResolver
 *  io.swagger.v3.oas.annotations.OpenAPIDefinition
 *  io.swagger.v3.oas.annotations.Operation
 *  io.swagger.v3.oas.annotations.Parameter
 *  io.swagger.v3.oas.annotations.info.Info
 *  io.swagger.v3.oas.annotations.media.ArraySchema
 *  io.swagger.v3.oas.annotations.media.Content
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.responses.ApiResponse
 *  io.swagger.v3.oas.annotations.responses.ApiResponses
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.business.insights.core.rest;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.dataset.DatasetProvider;
import com.atlassian.business.insights.core.analytics.export.FullExportCancelStartedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportCancelTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportStatusGetRequestedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportTriggeredFailedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportTriggeredNotStartedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportTriggeredStartedAnalyticEvent;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.rest.exception.InvalidQueryParamException;
import com.atlassian.business.insights.core.rest.model.CancellationStatusResponse;
import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.rest.model.ProcessStatusResponse;
import com.atlassian.business.insights.core.rest.validation.ValidateLicenseIsDc;
import com.atlassian.business.insights.core.rest.validation.ValidateQueryParams;
import com.atlassian.business.insights.core.rest.validation.ValidateUserIsAuthedAsSysAdmin;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.rest.validation.Validator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.ExportFromValidator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.ForceExportValidator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.JobIdValidator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.SchemaVersionValidator;
import com.atlassian.business.insights.core.rest.validation.validators.util.SchemaVersionValueParser;
import com.atlassian.business.insights.core.service.api.DataExportOrchestrator;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.ExportJobStateService;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.business.insights.core.util.DateDifferenceUtil;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.message.I18nResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@OpenAPIDefinition(info=@Info(title="Data Pipeline Export Resource", version="0.0.0", description="Experimental Data Pipeline API. Export Data Pipeline dataset from the host product. The root path is /rest/datapipeline/latest"))
@Path(value="/export")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@Component
@InterceptorChain(value={ValidateUserIsAuthedAsSysAdmin.class, ValidateLicenseIsDc.class, ValidateQueryParams.class})
public class ExportResource {
    public static final int UNPROCESSABLE_ENTITY = 422;
    @VisibleForTesting
    static final int DEFAULT_EXPORT_FROM_DAYS = 365;
    private static final Logger log = LoggerFactory.getLogger(ExportResource.class);
    private final DataExportOrchestrator dataExportOrchestrator;
    private final ExportJobStateService exportJobStateService;
    private final I18nResolver i18nResolver;
    private final EventPublisherService eventPublisherService;
    private final DateConversionUtil dateConversionUtil;
    private final DatasetProvider datasetProvider;

    @Autowired
    public ExportResource(DataExportOrchestrator dataExportOrchestrator, ExportJobStateService exportJobStateService, I18nResolver i18nResolver, EventPublisherService eventPublisherService, DateConversionUtil dateConversionUtil, DatasetProvider datasetProvider) {
        this.dataExportOrchestrator = dataExportOrchestrator;
        this.exportJobStateService = exportJobStateService;
        this.i18nResolver = i18nResolver;
        this.eventPublisherService = eventPublisherService;
        this.dateConversionUtil = dateConversionUtil;
        this.datasetProvider = datasetProvider;
    }

    @Operation(summary="Trigger an export of the full dataset for the host product.", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="202", description="An export has been successfully triggered.", content={@Content(schema=@Schema(implementation=ProcessStatusResponse.class))}), @ApiResponse(responseCode="409", description="The export can't be triggered because another export is currently in progress", content={@Content(schema=@Schema(implementation=ProcessStatusResponse.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="422", description="The export can't be triggered because data is inconsistent", content={@Content(schema=@Schema(implementation=ProcessStatusResponse.class))})})
    @POST
    public Response triggerFullExport(@Parameter(description="The start from date and time using the ISO 8601 format: yyyy-MM-ddTHH:mmTZD", example="2019-12-30T22:01+01:00 or 2019-12-30T23:01Z") @Schema(type="string", format="date-time") @QueryParam(value="fromDate") @Validator(value=ExportFromValidator.class) String fromDateParam, @Parameter(description="Force exporting even if data is not up to date.") @DefaultValue(value="false") @Schema(type="boolean") @QueryParam(value="forceExport") @Validator(value=ForceExportValidator.class) boolean forceExportParam, @Parameter(description="Override the default schema version number.") @Schema(type="string") @QueryParam(value="schemaVersion") @Validator(value=SchemaVersionValidator.class) String schemaVersion) {
        Dataset dataset = Optional.ofNullable(schemaVersion).map(this::toDataset).orElseGet(() -> ((DatasetProvider)this.datasetProvider).getDefaultDataset());
        Instant fromDate = this.getDefaultExportFromDateIfNull(fromDateParam);
        fromDate = DateConversionUtil.truncateToMinutes(fromDate);
        this.publishFullExportTriggeredAnalyticEvent(fromDate);
        Optional<ExportJobState> latestRunningJobState = this.exportJobStateService.getLatestRunningExportJobState();
        if (latestRunningJobState.isPresent()) {
            this.publishFullExportTriggeredNotStartedAnalyticEvent(fromDate);
            ExportJobState runningExportStatus = latestRunningJobState.get();
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)new ProcessStatusResponse(runningExportStatus, this.dateConversionUtil::formatToIso, this.datasetProvider.getSchemaStatus(runningExportStatus.getSchemaVersion()))).build();
        }
        ExportJobState ranExportStatus = this.dataExportOrchestrator.runFullExport(dataset, fromDate, forceExportParam);
        if (ranExportStatus.getStatus() == ExportProgressStatus.FAILED) {
            this.publishFullExportTriggeredFailedAnalyticEvent(fromDate);
            return Response.status((int)422).entity((Object)new ProcessStatusResponse(ranExportStatus, this.dateConversionUtil::formatToIso, this.datasetProvider.getSchemaStatus(ranExportStatus.getSchemaVersion()))).build();
        }
        this.publishFullExportTriggeredStartedAnalyticEvent(fromDate);
        return Response.status((Response.Status)Response.Status.ACCEPTED).entity((Object)new ProcessStatusResponse(ranExportStatus, this.dateConversionUtil::formatToIso, this.datasetProvider.getSchemaStatus(ranExportStatus.getSchemaVersion()))).build();
    }

    @Operation(summary="Retrieve the current export status an export process.", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns a status description of the current or last run export process.", content={@Content(schema=@Schema(implementation=ProcessStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @GET
    public Response getFullExportStatus() {
        this.publishFullExportStatusGetRequestedAnalyticEvent();
        Optional<ExportJobState> status = this.exportJobStateService.getLatestExportJobState();
        if (status.isPresent()) {
            return Response.ok((Object)new ProcessStatusResponse(status.get(), this.dateConversionUtil::formatToIso, this.datasetProvider.getSchemaStatus(status.get().getSchemaVersion()))).build();
        }
        return Response.ok((Object)ProcessStatusResponse.getEmptyResponse()).build();
    }

    @Operation(summary="Cancel the currently running export process, if there is one.", description="If an export process was killed, it needs to be cancelled before triggering another export.", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="If an export process was running it is now cancelled and now return a status description of the cancelled export process.", content={@Content(schema=@Schema(implementation=ProcessStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @DELETE
    public Response cancelFullExport(@Parameter(description="Request cancellation of a specific job, if left blank the latest job will be cancelled") @Schema(type="integer") @QueryParam(value="jobId") @Validator(value=JobIdValidator.class) String jobId) {
        this.publishFullExportCancelTriggeredAnalyticEvent();
        Optional<ExportJobState> exportJobState = this.getExportJobStateToBeCancelled(jobId);
        Integer jobIdToCancel = exportJobState.map(ExportJobState::getId).orElseGet(() -> this.getJobIdOrNull(jobId));
        if (!exportJobState.isPresent()) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)CancellationStatusResponse.getCancellationJobDoesntExistStatusResponse(jobIdToCancel, this.i18nResolver)).build();
        }
        if (!this.exportJobStateService.canJobBeCancelled(exportJobState.get())) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)CancellationStatusResponse.getCancellationJobAlreadyCompletedStatusResponse(jobIdToCancel, this.i18nResolver)).build();
        }
        try {
            this.exportJobStateService.requestCancellation(exportJobState.get());
        }
        catch (Exception e) {
            log.error("Error while trying to request a job cancellation", (Throwable)e);
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)CancellationStatusResponse.getCancellationErrorStatusResponse(jobIdToCancel, this.i18nResolver)).build();
        }
        this.publishFullExportCancelStartedAnalyticEvent();
        return Response.ok((Object)CancellationStatusResponse.getCancellationSuccessfulStatusResponse(jobIdToCancel, this.i18nResolver)).build();
    }

    private Optional<ExportJobState> getExportJobStateToBeCancelled(String jobId) {
        if (StringUtils.isBlank((CharSequence)jobId)) {
            return this.exportJobStateService.getLatestExportJobState();
        }
        return this.exportJobStateService.findJobById(Integer.parseInt(jobId));
    }

    private Instant getDefaultExportFromDateIfNull(String fromDate) {
        if (StringUtils.isNotBlank((CharSequence)fromDate)) {
            return DateConversionUtil.parseIsoOffsetDatetime(fromDate);
        }
        return Instant.now().minus(365L, ChronoUnit.DAYS);
    }

    private Integer getJobIdOrNull(String jobId) {
        return jobId == null ? null : Integer.valueOf(Integer.parseInt(jobId));
    }

    private Dataset toDataset(String schemaVersion) {
        return (Dataset)SchemaVersionValueParser.parse(schemaVersion).flatMap(arg_0 -> ((DatasetProvider)this.datasetProvider).getDataset(arg_0)).orElseThrow(() -> new InvalidQueryParamException(new ValidationResult("data-pipeline.api.rest.queryparam.schemaversion.invalid.should.be.supported.integer.schemaversion")));
    }

    private void publishFullExportCancelTriggeredAnalyticEvent() {
        this.eventPublisherService.publish(new FullExportCancelTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishFullExportCancelStartedAnalyticEvent() {
        this.eventPublisherService.publish(new FullExportCancelStartedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishFullExportTriggeredAnalyticEvent(Instant fromDate) {
        this.eventPublisherService.publish(new FullExportTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion(), DateDifferenceUtil.absoluteDifferenceInDays(fromDate, Instant.now())));
    }

    private void publishFullExportTriggeredNotStartedAnalyticEvent(Instant fromDate) {
        this.eventPublisherService.publish(new FullExportTriggeredNotStartedAnalyticEvent(this.eventPublisherService.getPluginVersion(), DateDifferenceUtil.absoluteDifferenceInDays(fromDate, Instant.now())));
    }

    private void publishFullExportTriggeredFailedAnalyticEvent(Instant fromDate) {
        this.eventPublisherService.publish(new FullExportTriggeredFailedAnalyticEvent(this.eventPublisherService.getPluginVersion(), DateDifferenceUtil.absoluteDifferenceInDays(fromDate, Instant.now())));
    }

    private void publishFullExportTriggeredStartedAnalyticEvent(Instant fromDate) {
        this.eventPublisherService.publish(new FullExportTriggeredStartedAnalyticEvent(this.eventPublisherService.getPluginVersion(), DateDifferenceUtil.absoluteDifferenceInDays(fromDate, Instant.now())));
    }

    private void publishFullExportStatusGetRequestedAnalyticEvent() {
        this.eventPublisherService.publish(new FullExportStatusGetRequestedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }
}

